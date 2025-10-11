#!/usr/bin/env bash
set -euo pipefail

# Pre-tag release automation for Indic Browser
# - Validates tag format (X.Y.Z) and non-existence
# - Ensures git working tree is clean
# - Runs full local test workflow on a device/emulator if available; falls back to managed device
# - On success, creates and pushes the tag to origin
#
# Usage:
#   scripts/release_tag_with_tests.sh 1.8.7
# Optional flags forwarded to run_android_tests.sh:
#   --connected   Run instrumentation tests on an already running connected device/emulator

ROOT_DIR="$(cd "$(dirname "$0")"/.. && pwd)"
cd "$ROOT_DIR"

if [ $# -lt 1 ]; then
  echo "Usage: $0 <tag> [--connected]"
  exit 2
fi

TAG="$1"; shift || true
FORWARD_ARGS=("$@")

# Validate tag format X.Y.Z (e.g., 1.8.7)
if ! [[ "$TAG" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "[ERROR] Tag must match X.Y.Z (e.g., 1.8.7). Got: $TAG"
  exit 3
fi

# Ensure git is available
if ! command -v git >/dev/null 2>&1; then
  echo "[ERROR] git is not installed or not in PATH"
  exit 4
fi

# Ensure working tree is clean
if ! git diff --quiet || ! git diff --cached --quiet; then
  echo "[ERROR] Working tree has uncommitted changes. Please commit or stash before tagging."
  git status -s
  exit 5
fi

# Ensure tag does not already exist locally or remotely
if git rev-parse "$TAG" >/dev/null 2>&1; then
  echo "[ERROR] Tag already exists locally: $TAG"
  exit 6
fi
if git ls-remote --tags origin "refs/tags/$TAG" | grep -q "$TAG"; then
  echo "[ERROR] Tag already exists on origin: $TAG"
  exit 7
fi

# Make sure test script is executable
chmod +x scripts/run_android_tests.sh

# Decide whether to use connected device automatically if flag is not provided
USE_CONNECTED=false
if [[ " ${FORWARD_ARGS[*]-} " == *"--connected"* ]]; then
  USE_CONNECTED=true
else
  if command -v adb >/dev/null 2>&1; then
    # If at least one device is connected and not offline, use connected
    if adb devices | awk 'NR>1 && $2=="device" {print $1}' | grep -q .; then
      USE_CONNECTED=true
    fi
  fi
fi

# Run tests
if [ "$USE_CONNECTED" = true ]; then
  echo "[INFO] Running tests on CONNECTED device before tagging $TAG ..."
  scripts/run_android_tests.sh --connected
else
  echo "[INFO] Running tests on MANAGED device before tagging $TAG ..."
  scripts/run_android_tests.sh
fi

# If we get here, tests passed and logcat analysis found no critical issues
# Create and push the tag

echo "[INFO] Creating tag $TAG"
git tag "$TAG"
echo "[INFO] Pushing tag $TAG to origin"
git push origin "$TAG"

echo "[SUCCESS] Tag $TAG created and pushed after successful local tests."
