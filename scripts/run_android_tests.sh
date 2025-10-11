#!/usr/bin/env bash
set -euo pipefail

# Automated test runner for Indic Browser
# - Runs unit tests
# - Runs instrumentation tests on a Gradle Managed Device (Pixel 6, API 33)
# - Captures Logcat during instrumentation tests
# - Collects reports into scripts/test_artifacts

ROOT_DIR="$(cd "$(dirname "$0")"/.. && pwd)"
OUT_DIR="$ROOT_DIR/scripts/test_artifacts"

# Parse args / mode selection
# Modes:
#   auto (default): use connected device if present, else managed device
#   connected: force connectedDebugAndroidTest
#   managed: force managed device task
#   managed-tv: force Android TV managed device task
MODE="auto"
MD_TASK=":app:pixel6Api33DebugAndroidTest" # default managed device task
for arg in "$@"; do
  case "$arg" in
    --connected)
      MODE="connected" ;;
    --managed)
      MODE="managed" ;;
    --managed-tv)
      MODE="managed" ;
      MD_TASK=":app:tv1080pApi33DebugAndroidTest" ;;
  esac
done
mkdir -p "$OUT_DIR"

TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
LOGCAT_FILE="$OUT_DIR/logcat_${TIMESTAMP}.txt"

pushd "$ROOT_DIR" >/dev/null

echo "[1/5] Cleaning project..."
./gradlew clean -q

echo "[2/5] Running unit tests (testDebugUnitTest)..."
./gradlew :app:testDebugUnitTest --no-daemon --stacktrace

# Copy unit test reports
UNIT_REPORT_DIR="$ROOT_DIR/app/build/reports/tests/testDebugUnitTest"
if [ -d "$UNIT_REPORT_DIR" ]; then
  cp -R "$UNIT_REPORT_DIR" "$OUT_DIR/unit_${TIMESTAMP}" || true
fi

# Prepare Logcat
if command -v adb >/dev/null 2>&1; then
  echo "[3/5] Preparing Logcat capture..."
  adb start-server >/dev/null 2>&1 || true
  adb logcat -c || true
  # Start background logcat capture in the current shell so $! is set correctly
  adb logcat -v time > "$LOGCAT_FILE" &
  LOGCAT_PID=$!
  if [ -z "${LOGCAT_PID:-}" ]; then
    echo "[WARN] Failed to capture LOGCAT PID; continuing without managed shutdown."
  fi
else
  echo "[WARN] adb not found in PATH. Skipping logcat capture."
  LOGCAT_PID=""
fi

# Decide execution path
run_connected=false
if [ "$MODE" = "connected" ]; then
  run_connected=true
elif [ "$MODE" = "auto" ]; then
  if command -v adb >/dev/null 2>&1; then
    # Any device in 'device' state?
    if adb devices | awk 'NR>1 && $2=="device" {print $1}' | grep -q .; then
      run_connected=true
    fi
  fi
fi

if [ "$run_connected" = true ]; then
  echo "[4/5] Running instrumentation tests on CONNECTED device (connectedDebugAndroidTest)..."
  adb devices || true
  ./gradlew :app:connectedDebugAndroidTest --no-daemon --stacktrace
else
  echo "[4/5] Preparing to run instrumentation tests on MANAGED device ($MD_TASK)..."
  # Verify the managed device test task exists; if not, try to fall back to connected
  TASK_NAME=$(echo "$MD_TASK" | sed 's/^.*:\([^:]*\)$/\1/')
  if ./gradlew :app:tasks --all | grep -q "$TASK_NAME"; then
    echo "[INFO] Managed device task appears available: $MD_TASK"
    ./gradlew $MD_TASK --no-daemon --stacktrace
  else
    echo "[WARN] Managed device task $MD_TASK not found. Attempting CONNECTED device instead..."
    if command -v adb >/dev/null 2>&1 && adb devices | awk 'NR>1 && $2=="device" {print $1}' | grep -q .; then
      ./gradlew :app:connectedDebugAndroidTest --no-daemon --stacktrace
    else
      echo "[ERROR] No connected device available and managed device task not found. Ensure ADB is installed/in PATH (Android SDK platform-tools) or open an emulator from Android Studio, then re-run with --connected." >&2
      exit 12
    fi
  fi
fi

# Copy instrumentation test reports
INST_REPORT_DIR="$ROOT_DIR/app/build/reports/androidTests/connected"
if [ -d "$INST_REPORT_DIR" ]; then
  cp -R "$INST_REPORT_DIR" "$OUT_DIR/instrumentation_${TIMESTAMP}" || true
fi

# Stop logcat capture
if [ -n "${LOGCAT_PID:-}" ]; then
  echo "[5/5] Stopping Logcat capture (PID=$LOGCAT_PID)"
  kill "$LOGCAT_PID" || true
fi

# Analyze logcat for critical issues
EXIT_CODE=0
if [ -f "$LOGCAT_FILE" ]; then
  echo "\nAnalyzing Logcat for critical issues..."
  if grep -q "FATAL EXCEPTION" "$LOGCAT_FILE"; then
    echo "[ERROR] Detected FATAL EXCEPTION in Logcat. See: $LOGCAT_FILE"
    EXIT_CODE=1
  fi
  if grep -q "ANR in" "$LOGCAT_FILE"; then
    echo "[ERROR] Detected ANR in Logcat. See: $LOGCAT_FILE"
    EXIT_CODE=1
  fi
  if grep -q "Process \([0-9]\+\) crashed" "$LOGCAT_FILE"; then
    echo "[ERROR] Detected process crash in Logcat. See: $LOGCAT_FILE"
    EXIT_CODE=1
  fi
  if grep -q "StrictMode policy violation" "$LOGCAT_FILE"; then
    echo "[WARN] StrictMode policy violation detected. Consider fixing to keep builds clean."
  fi
fi

# Summary
echo "\nTest run complete. Artifacts:"
echo " - Unit test report: $OUT_DIR/unit_${TIMESTAMP} (if present)"
echo " - Instrumentation test report: $OUT_DIR/instrumentation_${TIMESTAMP} (if present)"
echo " - Logcat: $LOGCAT_FILE (if adb available)"

exit $EXIT_CODE

popd >/dev/null
