 package com.neew.browser;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.GeckoSession.NavigationDelegate;
import org.mozilla.geckoview.GeckoSession.ProgressDelegate;
import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.WebRequestError;
import org.mozilla.geckoview.WebRequest;
import android.util.Log;
import android.util.Patterns;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Gravity;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.net.Uri;
import org.mozilla.geckoview.GeckoSession.ScrollDelegate;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.widget.Switch;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.mozilla.geckoview.ContentBlocking;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate;
import java.util.List;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;
import org.mozilla.geckoview.GeckoSession.ContentDelegate;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Intent;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.content.res.Configuration;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.Set;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.DownloadManager;
import android.os.Environment;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import org.mozilla.geckoview.WebExtensionController;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.GeckoSession.PromptDelegate;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.SharePrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.PromptResponse;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.AlertPrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.AuthPrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.BeforeUnloadPrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ButtonPrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ColorPrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.DateTimePrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.FilePrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ChoicePrompt;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.ChoicePrompt.Choice;
import org.mozilla.geckoview.GeckoSession.PromptDelegate.TextPrompt;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate.ContentPermission;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate.MediaSource;
import org.mozilla.geckoview.GeckoSession.PermissionDelegate.MediaCallback;
import java.util.Arrays; // Added for Arrays.toString
import android.content.ClipData; // Added for file picker multiple selection
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.Window;
import java.text.DecimalFormat;
import android.view.ViewTreeObserver;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import java.net.URISyntaxException; // <-- Add this import
import android.app.PictureInPictureParams;
import android.os.Build;
import android.util.Rational;
import android.util.TypedValue;

// For snapshot persistence
import java.io.FileInputStream;
import android.graphics.BitmapFactory;
import java.security.MessageDigest;
import java.math.BigInteger;
// import org.mozilla.geckoview.GeckoSession.ConsoleMessage; // Import for ConsoleDelegate

import com.google.firebase.analytics.FirebaseAnalytics;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log; // If not already present
import java.util.List; // If not already present (for ActivityManager.AppTask)
// java.io.UnsupportedEncodingException is handled by its fully qualified name in the code

import android.webkit.MimeTypeMap; // Added for MimeTypeMap
import android.webkit.URLUtil; // Added for URLUtil
import org.mozilla.geckoview.MediaSession; // Added this import
import org.mozilla.geckoview.GeckoSessionSettings; // Added this import
import org.mozilla.geckoview.PanZoomController;
import org.mozilla.geckoview.ScreenLength;
import org.mozilla.geckoview.WebResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ScrollDelegate, GeckoSession.PromptDelegate, MediaSession.Delegate {
    private static final String TAG = "MainActivity";
    private static final String SNAPSHOT_DIRECTORY_NAME = "tab_snapshots";

    // --- Ephemeral popup tracking (suppress blank/new-tab until it proves useful) ---
    private final java.util.Set<GeckoSession> ephemeralSessions = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<GeckoSession, Boolean>());
    private final java.util.Map<GeckoSession, Runnable> ephemeralTimeoutTasks = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<GeckoSession, GeckoSession> popupParentMap = new java.util.concurrent.ConcurrentHashMap<>();
    private final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    // Single-threaded executor for IO-heavy tasks to avoid blocking the UI thread
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    // Debounce storage checks to avoid repeated heavy scans
    private volatile boolean storageCheckInFlight = false;
    // Guard to ensure we only attempt a single lightweight recovery per resume
    private volatile boolean resumeRecoveryAttempted = false;
    // Track whether we saw page progress shortly after resume to avoid unnecessary recovery
    private volatile boolean resumeHadProgress = false;
    private volatile long resumeAtMs = 0L;
    private volatile boolean hasEverResumed = false;
    private volatile boolean tvBlankBounceRecoveryAttempted = false;

    private boolean isBlankOrNullUri(String uri) {
        if (uri == null) return true;
        String u = uri.trim();
        if (u.isEmpty()) return true;
        String lu = u.toLowerCase();
        if (lu.startsWith("about:blank")) return true; // covers about:blank and about:blank#...
        if (lu.startsWith("about:srcdoc")) return true; // srcdoc placeholder windows
        if (lu.startsWith("javascript:")) return true; // avoid showing js: popups as tabs
        return false;
    }

    // Poll translation state via reflection and log whatever fields/methods we can find
    private void pollTranslationState(String tag) {
        try {
            if (sessionTranslationObj == null) return;
            Object state = null;
            try {
                java.lang.reflect.Method getState = sessionTranslationObj.getClass().getMethod("getState");
                state = getState.invoke(sessionTranslationObj);
            } catch (Throwable t) {
                Log.d(TAG, "[Translate][State] getState() not available", t);
            }
            if (state == null) return;
            Log.d(TAG, "[Translate][State] (" + tag + ") stateObj=" + state);
            try { // status or phase
                for (String mName : new String[]{"getStatus","getPhase","getState"}) {
                    try {
                        java.lang.reflect.Method m = state.getClass().getMethod(mName);
                        Object v = m.invoke(state);
                        Log.d(TAG, "[Translate][State] " + mName + "=" + v);
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            try { // progress
                for (String mName : new String[]{"getProgress","getProgressPercent"}) {
                    try {
                        java.lang.reflect.Method m = state.getClass().getMethod(mName);
                        Object v = m.invoke(state);
                        Log.d(TAG, "[Translate][State] " + mName + "=" + v);
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            try { // error info
                for (String mName : new String[]{"getError","getFailureReason"}) {
                    try {
                        java.lang.reflect.Method m = state.getClass().getMethod(mName);
                        Object v = m.invoke(state);
                        Log.d(TAG, "[Translate][State] " + mName + "=" + v);
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            try { // target and source
                for (String mName : new String[]{"getFromLanguage","getPageLanguage","getToLanguage","getTargetLanguage"}) {
                    try {
                        java.lang.reflect.Method m = state.getClass().getMethod(mName);
                        Object v = m.invoke(state);
                        Log.d(TAG, "[Translate][State] " + mName + "=" + v);
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
        } catch (Throwable t) {
            Log.d(TAG, "[Translate][State] poll failed", t);
        }
    }

    // --- Persist per-origin content permission decisions ---
    private void loadSavedContentPermissions() {
        if (prefs == null) return;
        String json = prefs.getString(PREF_SAVED_CONTENT_PERMISSIONS, null);
        savedContentPermissions.clear();
        if (json == null || json.isEmpty()) return;
        try {
            org.json.JSONObject obj = new org.json.JSONObject(json);
            java.util.Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String key = it.next();
                int val = obj.optInt(key, ContentPermission.VALUE_PROMPT);
                if (val == ContentPermission.VALUE_ALLOW || val == ContentPermission.VALUE_DENY) {
                    savedContentPermissions.put(key, val);
                }
            }
            Log.d(TAG, "[Perm] Loaded " + savedContentPermissions.size() + " saved content permissions from prefs");
        } catch (Exception e) {
            Log.w(TAG, "[Perm] Failed to parse saved content permissions JSON", e);
        }
    }

    private void saveSavedContentPermissions() {
        if (prefs == null) return;
        try {
            org.json.JSONObject obj = new org.json.JSONObject();
            for (java.util.Map.Entry<String, Integer> e : savedContentPermissions.entrySet()) {
                int v = e.getValue();
                if (v == ContentPermission.VALUE_ALLOW || v == ContentPermission.VALUE_DENY) {
                    obj.put(e.getKey(), v);
                }
            }
            prefs.edit().putString(PREF_SAVED_CONTENT_PERMISSIONS, obj.toString()).apply();
        } catch (Exception e) {
            Log.w(TAG, "[Perm] Failed to persist saved content permissions", e);
        }
    }

    private void probeTranslationsSupportOnce() {
        if (isTvDevice()) return;
        if (translationsEngineAvailable != null) return;
        try {
            Class<?> rtCls = Class.forName("org.mozilla.geckoview.TranslationsController$RuntimeTranslation");
            java.lang.reflect.Method m = rtCls.getMethod("getTranslationSupport");
            Object res = m.invoke(null);
            Log.d(TAG, "[Translate][Support] getTranslationSupport invoked: " + res);
            try { attachGeckoResultLogger(res, "Runtime.getTranslationSupport"); } catch (Throwable ignored) {}
            translationsEngineAvailable = Boolean.TRUE; // Invocation available; actual support will be reflected in result
        } catch (Throwable t) {
            translationsEngineAvailable = Boolean.FALSE;
            Log.w(TAG, "[Translate][Support] RuntimeTranslation.getTranslationSupport not available or failed", t);
        }
    }

    // Try to build TranslationOptions via Builder and set downloadModel=true; return null if unavailable
    private @Nullable Object buildTranslateOptionsWithDownload() {
        try {
            Class<?> builderCls = Class.forName("org.mozilla.geckoview.TranslationsController$SessionTranslation$TranslationOptions$Builder");
            Object builder = builderCls.getDeclaredConstructor().newInstance();
            try {
                java.lang.reflect.Method m = builderCls.getMethod("setDownloadModel", boolean.class);
                builder = m.invoke(builder, true);
            } catch (Throwable ignored) {}
            try {
                java.lang.reflect.Method m2 = builderCls.getMethod("build");
                Object options = m2.invoke(builder);
                Log.d(TAG, "[Translate] Built TranslationOptions with downloadModel=true: " + options);
                return options;
            } catch (Throwable t) {
                Log.d(TAG, "[Translate] TranslationOptions.Builder build() not available", t);
                return null;
            }
        } catch (Throwable t) {
            Log.d(TAG, "[Translate] TranslationOptions.Builder not available", t);
            return null;
        }
    }

    // Attach a .then(...) logger on GeckoResult via reflection if possible
    private void attachGeckoResultLogger(Object result, String tag) {
        if (result == null) return;
        try {
            Class<?> grCls = Class.forName("org.mozilla.geckoview.GeckoResult");
            if (!grCls.isInstance(result)) return;
            // Try then(Function)
            try {
                Class<?> funcCls = Class.forName("java.util.function.Function");
                java.lang.reflect.Method thenFn = grCls.getMethod("then", funcCls);
                Object fnProxy = java.lang.reflect.Proxy.newProxyInstance(
                        funcCls.getClassLoader(), new Class<?>[]{funcCls},
                        (p, m, a) -> {
                            if ("apply".equals(m.getName())) {
                                Object v = (a != null && a.length > 0) ? a[0] : null;
                                Log.d(TAG, "[Translate][Result] " + tag + " then(Function) value=" + v);
                                return null; // propagate null
                            }
                            return null;
                        }
                );
                thenFn.invoke(result, fnProxy);
                return;
            } catch (Throwable ignored) {}
            // Try then(Consumer)
            try {
                Class<?> consCls = Class.forName("java.util.function.Consumer");
                java.lang.reflect.Method thenCons = grCls.getMethod("then", consCls);
                Object consProxy = java.lang.reflect.Proxy.newProxyInstance(
                        consCls.getClassLoader(), new Class<?>[]{consCls},
                        (p, m, a) -> {
                            if ("accept".equals(m.getName())) {
                                Object v = (a != null && a.length > 0) ? a[0] : null;
                                Log.d(TAG, "[Translate][Result] " + tag + " then(Consumer) value=" + v);
                            }
                            return null;
                        }
                );
                thenCons.invoke(result, consProxy);
            } catch (Throwable ignored2) {}
        } catch (Throwable t) {
            // ignore
        }
    }

    // Mark that we've seen navigation/progress soon after resume to suppress probe recovery
    private void markResumeProgressIfWithinWindow(GeckoSession session) {
        try {
            if (session == getActiveSession()) {
                long dt = System.currentTimeMillis() - resumeAtMs;
                if (dt >= 0 && dt < 3000) {
                    resumeHadProgress = true;
                }
            }
        } catch (Throwable ignored) {}
    }

    private void forceReloadActiveSession(String reason) {
        try {
            final GeckoSession active = getActiveSession();
            if (active == null) return;
            if (geckoView == null) return;

            if (geckoView.getSession() != active) {
                Log.w(TAG, "[Reload] Re-attaching active session before reload (" + reason + ")");
                geckoView.setSession(active);
                updateUIForActiveSession();
            }
            try {
                active.setActive(true);
            } catch (Throwable ignored) {}

            String url = sessionUrlMap.get(active);
            if (url != null && isHttpLike(url)) {
                Log.w(TAG, "[Reload] Forcing loadUri(" + url + ") (" + reason + ")");
                active.loadUri(url);
            } else {
                Log.w(TAG, "[Reload] Forcing reload() (" + reason + ") url=" + url);
                active.reload();
            }
        } catch (Throwable t) {
            Log.w(TAG, "[Reload] forceReloadActiveSession failed (" + reason + ")", t);
        }
    }

    // Validate that the uBlock assets are real files, not Git LFS pointer stubs
    private boolean isUblockAssetsValid() {
        // Asset paths relative to assets/ folder
        String base = "extensions/uBlockOriginMV2/";
        return isAssetRealJson(base + "manifest.json") && isAssetRealJson(base + "managed_storage.json");
    }

    private boolean isAssetRealJson(String assetPath) {
        java.io.InputStream is = null;
        try {
            is = getAssets().open(assetPath);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            // Read small prefix
            char[] buf = new char[64];
            int n = br.read(buf);
            if (n <= 0) return false;
            String head = new String(buf, 0, n).trim();
            // Git LFS pointer files begin with 'version https://git-lfs.github.com/spec/v1'
            if (head.startsWith("version https://git-lfs")) {
                Log.e(TAG, "Asset appears to be a Git LFS pointer: " + assetPath);
                return false;
            }
            // Basic sanity: JSON files should start with '{' or '['
            return head.startsWith("{") || head.startsWith("[");
        } catch (Throwable t) {
            Log.e(TAG, "Failed to read asset: " + assetPath, t);
            return false;
        } finally {
            if (is != null) try { is.close(); } catch (Exception ignored) {}
        }
    }

    

    private boolean isHttpLike(String uri) {
        if (uri == null) return false;
        String u = uri.toLowerCase();
        return u.startsWith("http://") || u.startsWith("https://");
    }

    private @Nullable String getHostSafely(String uri) {
        try { java.net.URI u = new java.net.URI(uri); return u.getHost(); } catch (Exception e) { return null; }
    }

    private boolean isSameSite(@Nullable String a, @Nullable String b) {
        if (a == null || b == null) return false;
        if (a.equalsIgnoreCase(b)) return true;
        // naive suffix match: sub.example.com vs example.com
        return a.endsWith("." + b) || b.endsWith("." + a);
    }

    private void scheduleEphemeralTimeout(final GeckoSession session, long ms) {
        Runnable task = () -> {
            if (ephemeralSessions.contains(session)) {
                Log.i(TAG, "Ephemeral session timed out; closing.");
                ephemeralSessions.remove(session);
                ephemeralTimeoutTasks.remove(session);
                int idx = geckoSessionList.indexOf(session);
                if (idx != -1) { closeTab(idx); } else { session.close(); }
            }
        };
        ephemeralTimeoutTasks.put(session, task);
        mainHandler.postDelayed(task, ms);
    }

    private void cancelEphemeralTimeout(GeckoSession session) {
        Runnable r = ephemeralTimeoutTasks.remove(session);
        if (r != null) { mainHandler.removeCallbacks(r); }
    }

    // --- Consolidated PromptDelegate Fields ---
    private FilePrompt pendingFilePrompt;
    private GeckoResult<PromptResponse> pendingGeckoResultForFilePrompt;

    private ColorPrompt pendingColorPrompt;
    private GeckoResult<PromptResponse> pendingGeckoResultForColorPrompt;

    private DateTimePrompt pendingDateTimePrompt;
    private GeckoResult<PromptResponse> pendingGeckoResultForDateTimePrompt;

    // Fields for ChoicePrompt
    private org.mozilla.geckoview.GeckoSession.PromptDelegate.ChoicePrompt pendingChoicePrompt;
    private org.mozilla.geckoview.GeckoResult<org.mozilla.geckoview.GeckoSession.PromptDelegate.PromptResponse> pendingGeckoResultForChoicePrompt;
    // --- End PromptDelegate Fields ---

    // --- Control Bar State Views ---
    private FrameLayout controlBarContainer; // Assuming this and subsequent fields are correctly placed after prompt fields
    private LinearLayout expandedControlBar;
    private LinearLayout minimizedControlBar;
    private EditText minimizedUrlBar;
    // Buttons within minimized bar
    private ImageButton minimizedBackButton;
    private ImageButton minimizedNewTabButton;
    private ImageButton minimizedRefreshButton;
    private ImageButton minimizedForwardButton;

    private View translatePill;
    private TextView translatePillTitle;
    private TextView translateFromView;
    private TextView translateToView;
    private ImageButton translateCloseButton;
    private ProgressBar translateProgress;
    private androidx.appcompat.widget.SwitchCompat panelTranslateSwitch;
    private boolean translateEnabledPref = false;
    private String translateToLangPref = null;
    private String currentDetectedFromLang = null;
    private boolean isPageTranslated = false;
    private boolean updatingTranslateUi = false;

    private void showTranslatePill(boolean show) {
        if (translatePill == null) return;
        translatePill.setVisibility(show ? View.VISIBLE : View.GONE);
        if (panelTranslateSwitch != null) {
            if (panelTranslateSwitch.isChecked() != show) {
                updatingTranslateUi = true;
                panelTranslateSwitch.setChecked(show);
                updatingTranslateUi = false;
            }
        }
    }

    private void hideTranslatePill() {
        showTranslatePill(false);
    }

    private void syncTranslateSwitch() {
        if (panelTranslateSwitch != null) {
            if (panelTranslateSwitch.isChecked() != translateEnabledPref) {
                updatingTranslateUi = true;
                panelTranslateSwitch.setChecked(translateEnabledPref);
                updatingTranslateUi = false;
            }
        }
    }

    private void initTranslatePrefs() {
        translateEnabledPref = prefs.getBoolean("translate_enabled", false);
        translateToLangPref = prefs.getString("translate_to_lang", null);
    }

    private void saveTranslatePrefs() {
        prefs.edit().putBoolean("translate_enabled", translateEnabledPref).apply();
        prefs.edit().putString("translate_to_lang", translateToLangPref).apply();
    }

    private void closeTranslateHandler() {
        translateEnabledPref = false;
        saveTranslatePrefs();
        hideTranslatePill();
        if (isPageTranslated) {
            performRestoreOriginal();
            isPageTranslated = false;
        }
    }

    // --- Translation impl (reflection for safety across GV versions) ---
    private Object sessionTranslationObj = null; // org.mozilla.geckoview.TranslationsController$SessionTranslation
    private GeckoSession sessionTranslationSession = null; // the session this SessionTranslation belongs to
    private Object translationDelegateProxy = null; // cached Proxy for Delegate
    private Boolean translationsEngineAvailable = null; // null=unknown

    private void ensureSessionTranslation(GeckoSession session) {
        if (isTvDevice()) return;
        if (session == null) return;
        try {
            Log.d(TAG, "[Translate] ensureSessionTranslation: begin for session=" + session + ", hasExisting=" + (sessionTranslationObj != null));
            // If cached object belongs to a different session, drop it so we only listen to the active tab
            if (sessionTranslationObj != null && sessionTranslationSession != session) {
                Log.d(TAG, "[Translate] Session changed; resetting cached SessionTranslation");
                sessionTranslationObj = null;
                sessionTranslationSession = null;
            }
            // Preferred path: via TranslationsController (v146+)
            boolean gotViaController = false;
            if (sessionTranslationObj == null) {
                try {
                    Class<?> controllerCls = Class.forName("org.mozilla.geckoview.TranslationsController");
                    Object controller = null;
                    try {
                        java.lang.reflect.Method getCtrlFromSession = session.getClass().getMethod("getTranslationsController");
                        controller = getCtrlFromSession.invoke(session);
                        Log.d(TAG, "[Translate] TranslationsController from session: " + controller);
                        if (controller != null && controllerCls.isInstance(controller)) {
                            try {
                                java.lang.reflect.Method getSessionTranslation = controllerCls.getMethod("getSessionTranslation");
                                Object st = getSessionTranslation.invoke(controller);
                                Log.d(TAG, "[Translate] SessionTranslation via controller.getSessionTranslation(): " + st);
                                if (st != null) {
                                    sessionTranslationObj = st;
                                    gotViaController = true;
                                }
                            } catch (Throwable t0) {
                                Log.d(TAG, "[Translate] controller.getSessionTranslation() not available", t0);
                            }
                        }
                    } catch (Throwable tSess) {
                        Log.d(TAG, "[Translate] session.getTranslationsController() not available", tSess);
                    }
                    if (!gotViaController) {
                        // Older path via runtime (some builds)
                        Class<?> runtimeCls = Class.forName("org.mozilla.geckoview.GeckoRuntime");
                        Object runtime = null;
                        try {
                            java.lang.reflect.Method getRt = session.getClass().getMethod("getRuntime");
                            runtime = getRt.invoke(session);
                            Log.d(TAG, "[Translate] Got GeckoRuntime from session: " + runtime);
                        } catch (Throwable t) {
                            Log.d(TAG, "[Translate] session.getRuntime() not available", t);
                        }
                        if (runtime != null && runtimeCls.isInstance(runtime)) {
                            try {
                                java.lang.reflect.Method getCtrl = runtimeCls.getMethod("getTranslationsController");
                                Object ctrl2 = getCtrl.invoke(runtime);
                                Log.d(TAG, "[Translate] TranslationsController obtained: " + ctrl2);
                                if (ctrl2 != null && controllerCls.isInstance(ctrl2)) {
                                    Object st = null;
                                    try {
                                        java.lang.reflect.Method get = controllerCls.getMethod("get", GeckoSession.class);
                                        st = get.invoke(ctrl2, session);
                                        Log.d(TAG, "[Translate] SessionTranslation via controller.get(session): " + st);
                                    } catch (Throwable t1) {
                                        Log.d(TAG, "[Translate] controller.get(session) not available", t1);
                                        try {
                                            java.lang.reflect.Method getFor = controllerCls.getMethod("getForSession", GeckoSession.class);
                                            st = getFor.invoke(ctrl2, session);
                                            Log.d(TAG, "[Translate] SessionTranslation via controller.getForSession(session): " + st);
                                        } catch (Throwable t2) {
                                            Log.d(TAG, "[Translate] controller.getForSession(session) not available", t2);
                                        }
                                    }
                                    if (st != null) {
                                        sessionTranslationObj = st;
                                        gotViaController = true;
                                    }
                                }
                            } catch (Throwable t) {
                                Log.d(TAG, "[Translate] GeckoRuntime.getTranslationsController() not available", t);
                            }
                        }
                    }
                } catch (Throwable t) {
                    Log.d(TAG, "[Translate] Controller path not available", t);
                }
                if (!gotViaController) {
                    // Fallback: direct constructor on SessionTranslation(session)
                    Class<?> stCls = Class.forName("org.mozilla.geckoview.TranslationsController$SessionTranslation");
                    java.lang.reflect.Constructor<?> ctor = stCls.getConstructor(GeckoSession.class);
                    sessionTranslationObj = ctor.newInstance(session);
                    Log.d(TAG, "[Translate] SessionTranslation created via ctor: " + sessionTranslationObj);
                }
            } else {
                Log.d(TAG, "[Translate] Reusing existing SessionTranslation: " + sessionTranslationObj);
            }
            Log.d(TAG, "[Translate] SessionTranslation ready: " + sessionTranslationObj);
            sessionTranslationSession = session;
            // Attach delegate/handler to receive offers and state updates
            try {
                // First try Delegate API
                try {
                    Class<?> delegateCls = Class.forName("org.mozilla.geckoview.TranslationsController$SessionTranslation$Delegate");
                    if (translationDelegateProxy == null) {
                        translationDelegateProxy = java.lang.reflect.Proxy.newProxyInstance(
                                delegateCls.getClassLoader(),
                                new Class<?>[]{delegateCls},
                                (proxy, method, args) -> delegateInvocation(method, args)
                        );
                    }
                    java.lang.reflect.Method setDelegate = sessionTranslationObj.getClass().getMethod("setDelegate", delegateCls);
                    setDelegate.invoke(sessionTranslationObj, translationDelegateProxy);
                    Log.d(TAG, "[Translate] Delegate attached");
                } catch (Throwable noDelegate) {
                    // If Delegate API is not available, just log; Handler fallback is optional and complex.
                    Log.d(TAG, "[Translate] Delegate API not available; skipping Handler fallback", noDelegate);
                }
            } catch (Throwable td) {
                Log.w(TAG, "TranslationsDelegate not available", td);
            }
        } catch (Throwable t) {
            Log.w(TAG, "Translations not available in this GeckoView build", t);
        }
    }

    private Object delegateInvocation(java.lang.reflect.Method method, Object[] args) {
        String name = method.getName();
        try {
            Log.d(TAG, "[Translate][Delegate] callback: " + name + ", args=" + (args == null ? 0 : args.length));
            // If a GeckoSession argument is present and it's not the active session, ignore this callback
            try {
                GeckoSession active = getActiveSession();
                if (args != null && active != null) {
                    for (Object a : args) {
                        if (a instanceof GeckoSession) {
                            if (a != active) {
                                Log.d(TAG, "[Translate][Delegate] Ignoring callback for non-active session");
                                return null;
                            }
                            break;
                        }
                    }
                }
            } catch (Throwable ignored) {}
            // Introspect TranslationState if present in args (since getState() is unavailable on this GV)
            if (args != null) {
                for (Object a : args) {
                    if (a == null) continue;
                    Class<?> ac = a.getClass();
                    String cn = ac.getName();
                    if (cn.endsWith("TranslationsController$SessionTranslation$TranslationState")) {
                        try {
                            Object requestedPair = null;
                            Object detectedLanguages = null;
                            Object error = null;
                            Object isEngineReady = null;
                            Object hasVisibleChange = null;

                            try { java.lang.reflect.Field f = ac.getField("requestedTranslationPair"); requestedPair = f.get(a); } catch (Throwable ignored) {}
                            try { java.lang.reflect.Field f = ac.getField("detectedLanguages"); detectedLanguages = f.get(a); } catch (Throwable ignored) {}
                            try { java.lang.reflect.Field f = ac.getField("error"); error = f.get(a); } catch (Throwable ignored) {}
                            try { java.lang.reflect.Field f = ac.getField("isEngineReady"); isEngineReady = f.get(a); } catch (Throwable ignored) {}
                            try { java.lang.reflect.Field f = ac.getField("hasVisibleChange"); hasVisibleChange = f.get(a); } catch (Throwable ignored) {}

                            Log.d(TAG, "[Translate][State] requestedPair=" + requestedPair + ", error=" + error
                                    + ", isEngineReady=" + isEngineReady + ", hasVisibleChange=" + hasVisibleChange
                                    + ", detectedLanguages=" + (detectedLanguages == null ? null : detectedLanguages.toString()));

                            // Update progress indicator based on engine readiness and visible change
                            boolean engReady = false;
                            boolean visChange = false;
                            try { if (isEngineReady instanceof Boolean) engReady = (Boolean) isEngineReady; } catch (Throwable ignored) {}
                            try { if (hasVisibleChange instanceof Boolean) visChange = (Boolean) hasVisibleChange; } catch (Throwable ignored) {}
                            final boolean showProgress = !engReady && !visChange;
                            runOnUiThread(() -> {
                                if (translateProgress != null) translateProgress.setVisibility(showProgress ? View.VISIBLE : View.GONE);
                            });

                            // Extract detected doc language to resolve 'auto' into a concrete language for translate()
                            if (detectedLanguages != null) {
                                try {
                                    Class<?> dlCls = detectedLanguages.getClass();
                                    String doc = null;
                                    try { java.lang.reflect.Field f = dlCls.getField("docLangTag"); Object v = f.get(detectedLanguages); if (v != null) doc = String.valueOf(v); } catch (Throwable ignored) {}
                                    Boolean docSupported = null;
                                    try { java.lang.reflect.Field f2 = dlCls.getField("isDocLangTagSupported"); Object v2 = f2.get(detectedLanguages); if (v2 instanceof Boolean) docSupported = (Boolean) v2; } catch (Throwable ignored) {}
                                    if (doc == null || doc.isEmpty()) {
                                        // Older builds may expose getters; best-effort fallbacks
                                        try { java.lang.reflect.Method gm = dlCls.getMethod("getDocLangTag"); Object v = gm.invoke(detectedLanguages); if (v != null) doc = String.valueOf(v); } catch (Throwable ignored) {}
                                        if (doc == null || doc.isEmpty()) {
                                            try { java.lang.reflect.Method gm2 = dlCls.getMethod("getPageLanguage"); Object v = gm2.invoke(detectedLanguages); if (v != null) doc = String.valueOf(v); } catch (Throwable ignored) {}
                                        }
                                    }
                                    if (doc != null && !doc.isEmpty()) {
                                        currentDetectedFromLang = doc;
                                        Log.d(TAG, "[Translate][State] currentDetectedFromLang=" + currentDetectedFromLang);
                                        final String docFinal = doc;
                                        runOnUiThread(() -> {
                                            if (translateFromView != null) translateFromView.setText("From: " + langCodeToName(baseLang(docFinal)));
                                            // Auto show/hide pill: show only if page language differs from system language
                                            String sys = baseLang(getSystemLangTag());
                                            String page = baseLang(docFinal);
                                            boolean mismatch = (sys == null || page == null) ? false : !sys.equalsIgnoreCase(page);
                                            showTranslatePill(translateEnabledPref && mismatch);
                                        });
                                        // Show unsupported message if engine doesn't support detected source language
                                        if (docSupported != null && !docSupported) {
                                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Translation engine doesn't support this page language", Toast.LENGTH_SHORT).show());
                                        }
                                    }
                                } catch (Throwable ignored) { }
                            }
                            // Show unsupported message on explicit errors mentioning unsupported target/source
                            if (error != null) {
                                String err = String.valueOf(error).toLowerCase();
                                if (err.contains("unsupported")) {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Unsupported translation for selected language pair", Toast.LENGTH_SHORT).show());
                                }
                            }
                        } catch (Throwable t) {
                            Log.d(TAG, "[Translate][State] failed to introspect TranslationState", t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.d(TAG, "[Translate][Delegate] error dispatching callback: " + name, t);
        }
        return null;
    }

    private void performRestoreOriginal() {
        if (isTvDevice()) return;
        if (sessionTranslationObj == null) return;
        try {
            java.lang.reflect.Method m = sessionTranslationObj.getClass().getMethod("restoreOriginalPage");
            Object res = m.invoke(sessionTranslationObj);
            Log.d(TAG, "restoreOriginalPage invoked: " + res);
        } catch (Throwable t) {
            Log.w(TAG, "Failed to restoreOriginalPage", t);
        }
    }

    private void performTranslate(String fromLang, String toLang) {
        if (isTvDevice()) return;
        Log.d(TAG, "[Translate] performTranslate called with from=" + fromLang + ", to=" + toLang + ", detectedFrom=" + currentDetectedFromLang);
        // Probe engine support once
        probeTranslationsSupportOnce();
        // If user chose 'auto', prefer using detected docLangTag when available; only use null-from while detection hasn't arrived yet
        String fallbackFrom = fromLang;
        boolean isAuto = (fromLang == null || fromLang.isEmpty() || "auto".equalsIgnoreCase(fromLang));
        boolean tryNullFromFirst;
        if (isAuto && currentDetectedFromLang != null && !currentDetectedFromLang.isEmpty()) {
            fallbackFrom = currentDetectedFromLang;
            tryNullFromFirst = false;
        } else {
            tryNullFromFirst = isAuto;
        }
        if (toLang == null || toLang.isEmpty()) return;
        GeckoSession active = getActiveSession();
        ensureSessionTranslation(active);
        if (sessionTranslationObj == null) {
            Toast.makeText(this, "Translation not supported on this build", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            boolean done = false;
            if (tryNullFromFirst) {
                Log.d(TAG, "[Translate] Attempting auto-detected from (null-from)");
                // Try 3-arg null-from
                try {
                    Class<?> optionsCls = Class.forName("org.mozilla.geckoview.TranslationsController$SessionTranslation$TranslationOptions");
                    java.lang.reflect.Method translate3 = sessionTranslationObj.getClass()
                            .getMethod("translate", String.class, String.class, optionsCls);
                    Object options = buildTranslateOptionsWithDownload();
                    Object res = translate3.invoke(sessionTranslationObj, null, toLang, options);
                    Log.d(TAG, "[Translate] translate 3-arg (null-from) OK: " + res);
                    attachGeckoResultLogger(res, "3-arg null-from");
                    pollTranslationState("after 3-arg null-from");
                    try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+500ms"), 500); } catch (Throwable ignored) {}
                    try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+1500ms"), 1500); } catch (Throwable ignored) {}
                    done = true;
                } catch (Throwable t) {
                    Log.d(TAG, "[Translate] translate 3-arg (null-from) not available", t);
                }
                // Try 2-arg null-from if not done
                if (!done) {
                    try {
                        java.lang.reflect.Method translate2 = sessionTranslationObj.getClass()
                                .getMethod("translate", String.class, String.class);
                        Object res = translate2.invoke(sessionTranslationObj, null, toLang);
                        Log.d(TAG, "[Translate] translate 2-arg (null-from) OK: " + res);
                        attachGeckoResultLogger(res, "2-arg null-from");
                        pollTranslationState("after 2-arg null-from");
                        try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+500ms"), 500); } catch (Throwable ignored) {}
                        try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+1500ms"), 1500); } catch (Throwable ignored) {}
                        done = true;
                    } catch (Throwable t) {
                        Log.d(TAG, "[Translate] translate 2-arg (null-from) not available", t);
                    }
                }
            }
            if (!done) {
                // If we didn't try null, or it failed, try with a concrete from
                String effFrom = fallbackFrom;
                if (effFrom == null || effFrom.isEmpty() || "auto".equalsIgnoreCase(effFrom)) {
                    if (currentDetectedFromLang != null && !currentDetectedFromLang.isEmpty()) {
                        effFrom = currentDetectedFromLang;
                    }
                }
                if (effFrom != null && !effFrom.isEmpty() && !"auto".equalsIgnoreCase(effFrom)) {
                    Log.d(TAG, "[Translate] Attempting with-from=" + effFrom);
                    // Try 3-arg with-from
                    try {
                        Class<?> optionsCls = Class.forName("org.mozilla.geckoview.TranslationsController$SessionTranslation$TranslationOptions");
                        java.lang.reflect.Method translate3 = sessionTranslationObj.getClass()
                                .getMethod("translate", String.class, String.class, optionsCls);
                        Object options = buildTranslateOptionsWithDownload();
                        Object res = translate3.invoke(sessionTranslationObj, effFrom, toLang, options);
                        Log.d(TAG, "[Translate] translate 3-arg (with-from) OK: " + res);
                        attachGeckoResultLogger(res, "3-arg with-from");
                        pollTranslationState("after 3-arg with-from");
                        try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+500ms"), 500); } catch (Throwable ignored) {}
                        try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+1500ms"), 1500); } catch (Throwable ignored) {}
                        done = true;
                    } catch (Throwable t) {
                        Log.d(TAG, "[Translate] translate 3-arg (with-from) not available", t);
                    }
                    // Try 2-arg with-from if not done
                    if (!done) {
                        try {
                            java.lang.reflect.Method translate2 = sessionTranslationObj.getClass()
                                    .getMethod("translate", String.class, String.class);
                            Object res = translate2.invoke(sessionTranslationObj, effFrom, toLang);
                            Log.d(TAG, "[Translate] translate 2-arg (with-from) OK: " + res);
                            attachGeckoResultLogger(res, "2-arg with-from");
                            pollTranslationState("after 2-arg with-from");
                            try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+500ms"), 500); } catch (Throwable ignored) {}
                            try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> pollTranslationState("+1500ms"), 1500); } catch (Throwable ignored) {}
                            done = true;
                        } catch (Throwable t) {
                            Log.d(TAG, "[Translate] translate 2-arg (with-from) not available", t);
                        }
                    }
                }
            }
            if (done) {
                isPageTranslated = true;
                return;
            }
            throw new RuntimeException("No compatible translate() method succeeded");
        } catch (Throwable t) {
            Log.w(TAG, "Failed to call translate", t);
            Toast.makeText(this, "Couldn’t translate this page", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSystemLangTag() {
        java.util.Locale loc = java.util.Locale.getDefault();
        String tag = loc.toLanguageTag();
        if (tag == null || tag.isEmpty()) return loc.getLanguage();
        return tag;
    }

    // Return the base language code (strip region), e.g., en-US -> en
    private static String baseLang(String tag) {
        if (tag == null) return null;
        int i = tag.indexOf('-');
        if (i > 0) return tag.substring(0, i);
        i = tag.indexOf('_');
        if (i > 0) return tag.substring(0, i);
        return tag;
    }

    // Map language code to a human-readable English name for UI
    private static String langCodeToName(String code) {
        if (code == null || code.isEmpty()) return "Unknown";
        switch (code.toLowerCase(java.util.Locale.ROOT)) {
            case "auto": return "Auto";
            case "en": return "English";
            case "es": return "Spanish";
            case "fr": return "French";
            case "de": return "German";
            case "hi": return "Hindi";
            case "zh": return "Chinese";
            case "ja": return "Japanese";
            case "ru": return "Russian";
            default:
                try {
                    java.util.Locale l = new java.util.Locale(code);
                    String dn = l.getDisplayLanguage(java.util.Locale.ENGLISH);
                    if (dn != null && !dn.isEmpty()) return dn;
                } catch (Throwable ignored) {}
                return code;
        }
    }

    // Very simple best-effort language heuristic based on URL. Real detection should come from API delegate later.
    private @Nullable String detectPageLanguageHeuristic(@Nullable String url) {
        if (url == null) return null;
        try {
            String lower = url.toLowerCase(java.util.Locale.ROOT);
            if (lower.contains("/fr/") || lower.endsWith("/fr") || lower.contains(".fr")) return "fr";
            if (lower.contains("/de/") || lower.endsWith("/de") || lower.contains(".de")) return "de";
            if (lower.contains("/es/") || lower.endsWith("/es") || lower.contains(".es")) return "es";
            if (lower.contains("/ru/") || lower.endsWith("/ru") || lower.contains(".ru")) return "ru";
            if (lower.contains("/hi/") || lower.endsWith("/hi")) return "hi";
            if (lower.contains("/ja/") || lower.endsWith("/ja")) return "ja";
            if (lower.contains("/zh/") || lower.endsWith("/zh") || lower.contains(".cn") || lower.contains(".tw")) return "zh";
        } catch (Throwable ignored) {}
        return null;
    }

    private void showLanguagePicker(boolean isFrom) {
        if (isTvDevice()) return;
        // Supported codes (keep codes internally)
        final String[] langs = new String[]{"auto","en","es","fr","de","hi","zh","ja","ru"};
        final String[] names = new String[langs.length];
        for (int i = 0; i < langs.length; i++) {
            names[i] = langCodeToName(baseLang(langs[i]));
        }
        int checked = 0;
        String current = isFrom ? (currentDetectedFromLang == null ? "auto" : baseLang(currentDetectedFromLang))
                                 : (translateToLangPref == null ? baseLang(getSystemLangTag()) : baseLang(translateToLangPref));
        for (int i = 0; i < langs.length; i++) {
            if (langs[i].equalsIgnoreCase(current)) { checked = i; break; }
        }
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(isFrom ? "From language" : "To language")
            .setSingleChoiceItems(names, checked, (dlg, which) -> {
                String selCode = langs[which];
                String selName = names[which];
                if (isFrom) {
                    currentDetectedFromLang = selCode.equals("auto") ? null : selCode;
                    if (translateFromView != null) translateFromView.setText("From: " + selName);
                } else {
                    translateToLangPref = selCode;
                    saveTranslatePrefs();
                    if (translateToView != null) translateToView.setText("To: " + selName);
                    // Trigger translation immediately on selecting target
                    String from = currentDetectedFromLang != null ? currentDetectedFromLang : "auto";
                    String to = translateToLangPref != null ? translateToLangPref : getSystemLangTag();
                    Toast.makeText(MainActivity.this, "Translating…", Toast.LENGTH_SHORT).show();
                    performTranslate(from, to);
                }
                dlg.dismiss();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private volatile boolean isUiReady = false; // New flag to prevent UI updates before initialization
    private boolean isControlBarHidden = false; // New: true if both bars are GONE
    // Only allow hide when initiated from a scroll event on the active page
    private boolean scrollTriggeredHideRequest = false;
    private int lastScrollY = 0;
    // Suppress scroll-triggered hide immediately after navigation/back/forward
    private static final long SUPPRESS_AFTER_NAV_MS = 1000L;
    private long suppressScrollHideUntilMs = 0L;
    private static final int SCROLL_THRESHOLD = 50; // Pixels to scroll before triggering hide/show
    private static final int TAP_THRESHOLD_BOTTOM_EDGE_DP = 60; // DP for tap detection
    private int tapThresholdBottomEdgePx; // Will be calculated in onCreate
    // DP-based scroll thresholds and accumulators for mobile minimized control bar behavior
    private static final int HIDE_THRESHOLD_DP = 64;  // how far to scroll down before hiding
    private static final int SHOW_THRESHOLD_DP = 24;  // how far to scroll up before showing
    private int hideThresholdPx = 80; // init defaults; set properly in onCreate
    private int showThresholdPx = 30; // init defaults; set properly in onCreate
    private int accumulatedScrollDownPx = 0;
    private int accumulatedScrollUpPx = 0;

    private SharedPreferences prefs;
    private static final String PREF_COOKIES_ENABLED = "cookies_enabled";
    // --- Session Persistence Keys ---
    private static final String PREF_SAVED_URLS_JSON_ARRAY = "saved_urls_json_array"; // Changed key
    private static final String PREF_ACTIVE_INDEX = "active_index";
    // --- End Session Persistence Keys ---
    // --- Ad Blocker Key ---
    private static final String PREF_AD_BLOCKER_ENABLED = "ad_blocker_enabled"; // Standard blocker
    private static final String PREF_UBLOCK_ENABLED = "ublock_enabled"; // uBlock Origin
    // --- End Ad Blocker Key ---

    private View decorView; // To control system UI visibility
    // TV/UI state and input fields (restored)
    private boolean isUiFocused = false;
    private List<Rect> focusableRects = new ArrayList<>();
    // Scroll and UI state
    private int mLastScrollY = 0;
    private boolean mCanScrollUp = true;
    private String mLastValidUrl = "";
    private boolean isControlBarExpanded = true;
    // Cursor acceleration
    private int currentStepSize = 30; // base step size in px
    private static final int MAX_STEP_SIZE = 60;
    private static final int STEP_INCREMENT = 10;
    private static final long ACCELERATION_INTERVAL = 100L; // ms
    private long accelerationStartTime = 0L;
    // Gesture timing for simulated touch
    private long mGestureDownTime = 0L;

    // --- Tab Management --- 
    private List<GeckoSession> geckoSessionList = new ArrayList<>();
    private int activeSessionIndex = -1;
    private Map<GeckoSession, String> sessionUrlMap = new HashMap<>();
    // Track nested popup depth per session: 0 = main tab, 1 = popup, 2 = grandchild, ...

    // Back long-press confirmation dialog state
    private AlertDialog backConfirmDialog;
    private boolean backLongPressed = false;
    private final Map<GeckoSession, Integer> sessionPopupDepth = new HashMap<>();
    // Track last user gesture time per session (ms since epoch) to gate popups
    private final Map<GeckoSession, Long> sessionLastGestureMs = new HashMap<>();
    // Track last popup creation time per session for rate limiting
    private final Map<GeckoSession, Long> sessionLastPopupMs = new HashMap<>();
    private static final long POPUP_RATE_LIMIT_MS = 200L; // 1 popup per 200ms per session
    private static final long USER_GESTURE_WINDOW_MS = 5000L; // gesture considered fresh within 5s
    private static final int MAX_SNAPSHOTS = 10; // Limit snapshots stored
    private static final int SNAPSHOT_WIDTH = 220; // Reduced target width for resized snapshots to lower memory/IO
    private static final long SNAPSHOT_MIN_INTERVAL_MS = 30_000L; // Throttle: once per tab per 30s
    
    private Map<GeckoSession, Bitmap> sessionSnapshotMap = new LinkedHashMap<GeckoSession, Bitmap>(MAX_SNAPSHOTS + 1, .75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<GeckoSession, Bitmap> eldest) {
            boolean shouldRemove = size() > MAX_SNAPSHOTS;
            if (shouldRemove) {
                Bitmap bmp = eldest.getValue();
                if (bmp != null && !bmp.isRecycled()) bmp.recycle();
            }
            return shouldRemove;
        }
    };
    // Track which URL a session's in-memory snapshot corresponds to, to avoid mismatched overlays
    private Map<GeckoSession, String> sessionSnapshotUrlMap = new HashMap<>();
    // Last snapshot capture time per session for throttling
    private final java.util.concurrent.ConcurrentHashMap<GeckoSession, Long> sessionLastSnapshotMs = new java.util.concurrent.ConcurrentHashMap<>();
    private ActivityResultLauncher<Intent> tabSwitcherLauncher;

    // --- BACK key long-press handling ---
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK && isTvDevice()) {
        // Prepare to detect a long press
        event.startTracking();
        backLongPressed = false;
        return true; // consume; we'll decide on short vs long in onKeyUp/onKeyLongPress
      }
      return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK && isTvDevice()) {
        backLongPressed = true;
        showBackConfirmDialog();
        return true;
      }
      return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK && isTvDevice()) {
        if (backLongPressed) {
          // Long-press already handled by dialog; reset state and consume
          backLongPressed = false;
          return true;
        }
        // Short press -> delegate to existing back behavior
        onBackPressed();
        return true;
      }
      return super.onKeyUp(keyCode, event);
    }

    private void showBackConfirmDialog() {
      if (backConfirmDialog != null && backConfirmDialog.isShowing()) return;
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Close app?")
          .setCancelable(true)
          .setPositiveButton("OK", (dialog, which) -> {
            try { dialog.dismiss(); } catch (Exception ignored) {}
            // Close all activities and exit app
            finishAffinity();
          })
          .setNegativeButton("Cancel", (dialog, which) -> {
            try { dialog.dismiss(); } catch (Exception ignored) {}
          });
      backConfirmDialog = builder.create();
      backConfirmDialog.show();
    }
    // --- End Tab Management ---

    private LinearLayout settingsPanelLayout;
    private SwitchCompat panelCookieSwitch;
    private SwitchCompat panelAdBlockerSwitch;
    private SwitchCompat panelUBlockSwitch;
    private LinearLayout panelUBlockLayout; // New field for the layout
    private Button panelApplyButton;

    // --- TV Element-Targeted Scrolling (WebExtension-assisted) ---
    private WebExtension tvScrollExtension = null; // content.js under assets/webext
    private boolean tvElementScrollEnabled = true; // developer toggle can wire later
    private float tvDevicePixelRatio = 1.0f; // CSS px conversion cache
    private static final int TV_EXT_SCROLL_CSS_PX = 100; // element scroll step in CSS px
    private static final int TV_EXT_TIMEOUT_MS = 600; // timeout before fallback
    private Button panelCancelButton;

    private static final int REQUEST_CODE_WRITE_STORAGE = 1001;

    private static final String UBLOCK_EXTENSION_ID = "uBlock0@raymondhill.net";
    private static final String UBLOCK_ASSET_PATH = "resource://android/assets/extensions/uBlockOriginMV2/";

    private boolean uBlockInstallAttempted = false;
    private WebExtension ublockOriginExtension = null;

    private BroadcastReceiver downloadCompleteReceiver;
    private org.mozilla.geckoview.WebResponse pendingDownloadResponse = null;

    private ActivityResultLauncher<Intent> filePickerLauncher;
    // Note: pendingFilePrompt and pendingGeckoResultForFilePrompt are in the consolidated block above

    private List<File> tempUploadFiles = new ArrayList<>();
    // Note: pendingColorPrompt, pendingGeckoResultForColorPrompt, pendingDateTimePrompt, and pendingGeckoResultForDateTimePrompt are in the consolidated block above

    private GeckoView geckoView; // This line acts as a sentinel for where the replacement should roughly end before other major fields.
    private ImageView snapshotOverlay;

    private GeckoSession geckoSession;
    private static GeckoRuntime runtime; // Make GeckoRuntime a singleton

    // --- GeckoView Permission handling ---
    private static final int REQUEST_CODE_GV_ANDROID_PERMS = 2001;
    private static final String PREF_SAVED_CONTENT_PERMISSIONS = "saved_content_permissions_v1";
    private GeckoSession.PermissionDelegate.Callback pendingAndroidPermCallback;
    private String[] pendingAndroidPerms;
    // Per-origin content permission decisions: key = host + "|" + type, value = ContentPermission.VALUE_*
    private final java.util.Map<String, Integer> savedContentPermissions = new java.util.HashMap<>();
    private volatile boolean extensionsInitialized = false; // ensure uBO/TV ext init happens once, ASAP after first paint
    private EditText urlBar;
    private ProgressBar progressBar;
    private ImageButton backButton;
    private ImageButton forwardButton;
    private ImageButton refreshButton;
    private ImageButton settingsButton;
    private ImageButton downloadsButton;
    private ImageButton newTabButton;
    private ImageButton tabsButton;

    private static final String PREF_IMMERSIVE_MODE_ENABLED = "immersive_mode_enabled";
    
    // --- Debounce for element-scroll timeout fallback to root scroll ---
    // Prevents rapid repeated simulateScroll() calls when some sites (e.g., heavy player overlays)
    // neither scroll nor promptly respond to scrollAtPoint, causing multiple timeouts in quick succession.
    private static final long EXT_SCROLL_FALLBACK_MIN_GAP_MS = 220L; // minimal gap between root fallbacks
    private long lastExtScrollFallbackMs = 0L;
    
    private void runDebouncedRootScroll(boolean up) {
        long now = android.os.SystemClock.uptimeMillis();
        long since = now - lastExtScrollFallbackMs;
        if (since < EXT_SCROLL_FALLBACK_MIN_GAP_MS) {
            Log.d(TAG, "[TV][SCROLL] root fallback debounced (" + since + "ms since last)");
            return;
        }
        lastExtScrollFallbackMs = now;
        simulateScroll(up);
    }
    private SwitchCompat panelImmersiveSwitch;
    private SwitchCompat panelDesktopModeSwitch;
    private SwitchCompat panelFullDesktopModelSwitch; // New Switch field

    // User Agent configuration
    private static final String PREF_USER_AGENT_MODE = "user_agent_mode"; // "mobile", "desktop", "custom"
    private static final String DEFAULT_USER_AGENT_MODE = "mobile";
    private static final String PREF_FULL_DESKTOP_MODEL_ENABLED = "full_desktop_model_enabled"; // New preference key

    private boolean isInGeckoViewFullscreen = false; // To track if GeckoView requested fullscreen
    private Handler inputHandler = new Handler(Looper.getMainLooper());
    private static final long CURSOR_HIDE_DELAY = 3000; // 3 seconds
    private boolean isInPictureInPictureMode = false; // To track PiP state
    private boolean firstWindowFocus = true; // Add this new field

    private boolean isMediaActuallyPlaying = false; // New: To track if media is playing in the active session
    private GeckoSession mediaPlayingSession = null; // New: To track which session is playing media

    // Context menu fields
    private PopupWindow contextMenuPopup;
    private View contextMenuView;
    private GeckoSession.ContentDelegate.ContextElement currentContextElement;
    private String currentContextUrl;

    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean isTvDevice() {
        PackageManager pm = getPackageManager();
        // Check for leanback (Android TV standard)
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            return true;
        }
        // Fallback: check for FEATURE_TELEVISION (some TVs don't declare leanback)
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION)) {
            return true;
        }
        // Additional fallback: check UI mode for TV
        android.content.res.Configuration config = getResources().getConfiguration();
        return (config.uiMode & android.content.res.Configuration.UI_MODE_TYPE_MASK) 
                == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION;
    }

    private TvCursorView tvCursorView;

    private List<View> controlBarElements;
    private List<View> settingsPanelElements;
    private static final int CURSOR_STEP_SIZE = 20; // Pixels to move cursor per key press
    private static final int SCROLL_AMOUNT_PER_PRESS = 100; // Increased scroll amount for more noticeable scrolling
    private boolean isKeyPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display. This is required for modern Android versions
        // and is the default for apps targeting SDK 35.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Set screen orientation to landscape for TV devices
        if (isTvDevice()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_main);

        // Initialize TV cursor view
        tvCursorView = findViewById(R.id.tvCursorView);
        if (isTvDevice()) {
            tvCursorView.setVisibility(View.VISIBLE);
            // Center the cursor initially
            centerCursor();

        }

        tapThresholdBottomEdgePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                TAP_THRESHOLD_BOTTOM_EDGE_DP,
                getResources().getDisplayMetrics()
        );

        // Make status bar transparent and content immersive (initial setup)
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        // Get the decor view for fullscreen control
        decorView = getWindow().getDecorView();

        // Initialize SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loadSavedContentPermissions();

        // Initialize UI components
        geckoView = findViewById(R.id.geckoView);
        snapshotOverlay = findViewById(R.id.snapshotOverlay);

        // Wait for the initial layout to complete before calculating focusable areas.
        // This is crucial for the cursor to find the control bar on first load.
        if (isTvDevice()) {
            geckoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updateFocusableRects();
                    // Remove the listener to prevent it from being called repeatedly.
                    if (geckoView.getViewTreeObserver().isAlive()) {
                        geckoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        urlBar = findViewById(R.id.urlBar);
        progressBar = findViewById(R.id.progressBar);
        backButton = findViewById(R.id.backButton);
        forwardButton = findViewById(R.id.forwardButton);
        refreshButton = findViewById(R.id.refreshButton);
        settingsButton = findViewById(R.id.settingsButton);
        downloadsButton = findViewById(R.id.downloadsButton);
        newTabButton = findViewById(R.id.newTabButton);
        tabsButton = findViewById(R.id.tabsButton);

        // Control Bar Views
        controlBarContainer = findViewById(R.id.controlBarContainer);
        expandedControlBar = findViewById(R.id.expandedControlBar);
        minimizedControlBar = findViewById(R.id.minimizedControlBar);
        minimizedUrlBar = findViewById(R.id.minimizedUrlBar);
        minimizedBackButton = findViewById(R.id.minimizedBackButton);
        minimizedNewTabButton = findViewById(R.id.minimizedNewTabButton);
        minimizedRefreshButton = findViewById(R.id.minimizedRefreshButton);
        minimizedForwardButton = findViewById(R.id.minimizedForwardButton);
        
        // Initialize scroll thresholds (convert DP to PX)
        {
            final float density = getResources().getDisplayMetrics().density;
            hideThresholdPx = Math.max(1, Math.round(HIDE_THRESHOLD_DP * density));
            showThresholdPx = Math.max(1, Math.round(SHOW_THRESHOLD_DP * density));
            tapThresholdBottomEdgePx = Math.max(1, Math.round(TAP_THRESHOLD_BOTTOM_EDGE_DP * density));
            Log.d(TAG, "[SCROLL] thresholds: hide=" + hideThresholdPx + "px show=" + showThresholdPx + "px tapBottom=" + tapThresholdBottomEdgePx + "px");
        }

        // Initialize lists for focusable UI elements
        controlBarElements = Arrays.asList(
                settingsButton, urlBar, backButton, forwardButton, newTabButton, tabsButton, downloadsButton
        );
        
        // Initialize Settings Panel components
        settingsPanelLayout = findViewById(R.id.settingsPanelLayout);
        panelCookieSwitch = findViewById(R.id.panelCookieSwitch);
        panelTranslateSwitch = findViewById(R.id.panelTranslateSwitch);
        panelAdBlockerSwitch = findViewById(R.id.panelAdBlockerSwitch);
        panelUBlockSwitch = findViewById(R.id.panelUBlockSwitch);
        panelUBlockLayout = findViewById(R.id.panelUBlockLayout); // Initialize the layout
        panelImmersiveSwitch = findViewById(R.id.panelImmersiveSwitch);
        panelApplyButton = findViewById(R.id.panelApplyButton);
        panelCancelButton = findViewById(R.id.panelCancelButton);

        settingsPanelElements = Arrays.asList(
                panelCookieSwitch, panelAdBlockerSwitch, panelUBlockLayout, panelImmersiveSwitch,
                panelDesktopModeSwitch, panelFullDesktopModelSwitch,
                panelApplyButton, panelCancelButton
        );
        // Set Immersive Mode enabled by default (true)
        boolean immersiveDefault = prefs.getBoolean(PREF_IMMERSIVE_MODE_ENABLED, true);
        panelImmersiveSwitch.setChecked(immersiveDefault);
        panelImmersiveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_IMMERSIVE_MODE_ENABLED, isChecked).apply();
            applyImmersiveMode(isChecked);
        });

        // Initialize Desktop Mode Switch
        panelDesktopModeSwitch = findViewById(R.id.panelDesktopModeSwitch);
        setupDesktopModeSwitch();

        // Initialize Full Desktop Model Switch
        panelFullDesktopModelSwitch = findViewById(R.id.panelFullDesktopModelSwitch); // Assuming this ID will be in your XML
        if (panelFullDesktopModelSwitch != null) {
            boolean fullDesktopEnabledPref = prefs.getBoolean(PREF_FULL_DESKTOP_MODEL_ENABLED, false); // Default to false
            panelFullDesktopModelSwitch.setChecked(fullDesktopEnabledPref);
            
            panelFullDesktopModelSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) { // If user is trying to turn ON the Full Desktop Model switch
                    if (panelDesktopModeSwitch != null && !panelDesktopModeSwitch.isChecked()) {
                        // Desktop Mode is OFF, so prevent Full Desktop Model from being turned ON
                        Toast.makeText(MainActivity.this, "Please enable Desktop Mode first", Toast.LENGTH_SHORT).show();
                        panelFullDesktopModelSwitch.setChecked(false); // Revert the switch to OFF
                    } else {
                        // Desktop Mode is ON (or switch is null, though unlikely here), allow Full Desktop Model to be ON
                        Log.d(TAG, "Full Desktop Model switch in panel set to: " + (isChecked));
                    }
                } else {
                    // User is turning OFF the Full Desktop Model switch, always allow this
                    Log.d(TAG, "Full Desktop Model switch in panel set to: " + (isChecked));
                }
                // Actual saving and application of this state will happen in applySettingsFromPanel()
            });
        } else {
            Log.w(TAG, "panelFullDesktopModelSwitch is null. Please add it to your layout XML with ID 'panelFullDesktopModelSwitch'.");
        }

        // Initialize translation pill (mobile only)
        if (!isTvDevice()) {
            initTranslatePrefs();
            translatePill = findViewById(R.id.translatePill);
            translateFromView = findViewById(R.id.translateFrom);
            translateToView = findViewById(R.id.translateTo);
            translateCloseButton = findViewById(R.id.translateClose);
            translatePillTitle = findViewById(R.id.translatePillTitle);
            translateProgress = findViewById(R.id.translateProgress);

            if (panelTranslateSwitch != null) {
                panelTranslateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (updatingTranslateUi) return;
                    translateEnabledPref = isChecked;
                    saveTranslatePrefs();
                    // Only show if page language differs from system and we have a detection; otherwise keep hidden until detected
                    String sys = baseLang(getSystemLangTag());
                    String page = baseLang(currentDetectedFromLang);
                    boolean mismatch = (sys != null && page != null && !sys.equalsIgnoreCase(page));
                    if (isChecked) {
                        if (page == null) {
                            // Detection not available yet: tentatively show so user can interact; detection will re-evaluate
                            showTranslatePill(true);
                            if (translateFromView != null) {
                                translateFromView.setText("From: " + langCodeToName("auto"));
                            }
                            if (translateToView != null) {
                                String toInit = (translateToLangPref != null) ? translateToLangPref : getSystemLangTag();
                                translateToView.setText("To: " + langCodeToName(baseLang(toInit)));
                            }
                        } else {
                            showTranslatePill(mismatch);
                            if (translateFromView != null) {
                                translateFromView.setText("From: " + langCodeToName(baseLang(page)));
                            }
                            if (translateToView != null) {
                                String toInit = (translateToLangPref != null) ? translateToLangPref : getSystemLangTag();
                                translateToView.setText("To: " + langCodeToName(baseLang(toInit)));
                            }
                        }
                    } else {
                        showTranslatePill(false);
                    }
                });
                // Sync initial state without forcing visibility; detection callback will handle showing
                syncTranslateSwitch();
            }

            if (translateFromView != null) {
                translateFromView.setOnClickListener(v -> showLanguagePicker(true));
            }
            if (translateToView != null) {
                translateToView.setOnClickListener(v -> showLanguagePicker(false));
                // Initialize label from saved preference (show human-readable name)
                String toInit = (translateToLangPref != null) ? translateToLangPref : getSystemLangTag();
                translateToView.setText("To: " + langCodeToName(baseLang(toInit)));
            }
            // Leave translatePillTitle as a non-clickable label to avoid duplicate translate actions
            if (translateCloseButton != null) {
                translateCloseButton.setOnClickListener(v -> {
                    showTranslatePill(false);
                    if (isPageTranslated) {
                        performRestoreOriginal();
                        isPageTranslated = false;
                        Toast.makeText(MainActivity.this, "Showing original", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Ensure pill stays 5dp above the control bar in both expanded/minimized states
            final View controlContainer = findViewById(R.id.controlBarContainer);
            if (translatePill != null && controlContainer != null) {
                final View root = (View) controlContainer.getParent();
                final int dp5 = (int) (5 * getResources().getDisplayMetrics().density);
                final Runnable adjustPill = () -> {
                    if (root.getWidth() == 0) return;
                    // Ensure pill measured
                    if (translatePill.getWidth() == 0 || translatePill.getHeight() == 0) {
                        translatePill.measure(
                            View.MeasureSpec.makeMeasureSpec(root.getWidth(), View.MeasureSpec.AT_MOST),
                            View.MeasureSpec.makeMeasureSpec(root.getHeight(), View.MeasureSpec.AT_MOST));
                    }
                    if (translatePill.getMeasuredWidth() == 0 || translatePill.getMeasuredHeight() == 0) return;
                    // Anchor strictly to the overall control bar container top to stay above the entire panel
                    int[] anchorLoc = new int[2];
                    int[] rootLoc = new int[2];
                    controlContainer.getLocationOnScreen(anchorLoc);
                    root.getLocationOnScreen(rootLoc);
                    int anchorTopInRoot = anchorLoc[1] - rootLoc[1];

                    int newY = anchorTopInRoot - translatePill.getMeasuredHeight() - dp5;
                    int newX = (root.getWidth() - translatePill.getMeasuredWidth()) / 2;
                    translatePill.setX(newX);
                    translatePill.setY(newY);
                };
                // Adjust on first layout and whenever the control bar layout changes
                translatePill.post(adjustPill);
                controlContainer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> translatePill.post(adjustPill));
                // Also adjust when the pill's own size changes
                translatePill.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> translatePill.post(adjustPill));
                // And after a short delay to cover post-animation/layout passes
                try { new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> translatePill.post(adjustPill), 200); } catch (Throwable ignored) {}
                // Global layout to react to any other UI changes (e.g., address bar animations)
                root.getViewTreeObserver().addOnGlobalLayoutListener(() -> translatePill.post(adjustPill));
            }
        }

        // Initialize context menu
        initializeContextMenu();

        // Setup listener for the new layout
        if (panelUBlockLayout != null) {
            panelUBlockLayout.setOnClickListener(v -> {
                if (panelUBlockSwitch != null) {
                    panelUBlockSwitch.toggle();
                }
            });
        }

        // Initialize Gecko Runtime (only once)
        if (runtime == null) {
            Log.d(TAG, "Creating new GeckoRuntime.");

            // Copy GeckoView config from assets to a file that GeckoView can read
            File configFile = copyGeckoConfigFromAssets();

            GeckoRuntimeSettings.Builder runtimeSettingsBuilder = new GeckoRuntimeSettings.Builder();
            runtimeSettingsBuilder.consoleOutput(true); // Keep console output enabled

            if (configFile != null) {
                Log.d(TAG, "Using GeckoView config from: " + configFile.getAbsolutePath());
                runtimeSettingsBuilder.configFilePath(configFile.getAbsolutePath());
            } else {
                Log.w(TAG, "GeckoView config file not found or could not be copied.");
            }

            runtime = GeckoRuntime.create(getApplicationContext(), runtimeSettingsBuilder.build());
            Log.i(TAG, "GeckoRuntime created.");

            applyRuntimeSettings(); // Apply other dynamic settings
            if (!extensionsInitialized) {
                extensionsInitialized = true;
                initializeUBlockOrigin();
                if (isTvDevice()) { ensureTvScrollExtension(); }
            }
        } else {
            Log.d(TAG, "Reusing existing GeckoRuntime");
            // If reusing, maybe re-apply dynamic settings?
            applyRuntimeSettings(); // Consider if needed on reuse
            if (!extensionsInitialized) {
                extensionsInitialized = true;
                initializeUBlockOrigin();
                if (isTvDevice()) {
                    ensureTvScrollExtension();
                    scheduleEnsureTvScrollExtensionRetries(3, 500);
                }
            }
        }

        // --- Restore Session Tabs from prefs --- 
        if (geckoSessionList.isEmpty() && !restoreSessionState()) {
            // If restore failed or no saved state, create a single initial tab
            Log.d(TAG, "No saved state found or restore failed, creating initial tab.");
            createNewTab(getDefaultHomepageUrl(), true); // Create and make active
        }
        // --- End Restore ---

        // --- Register Activity Result Launcher --- 
        tabSwitcherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        if (data.hasExtra(TabSwitcherActivity.RESULT_SELECTED_TAB_INDEX)) {
                            int selectedIndex = data.getIntExtra(TabSwitcherActivity.RESULT_SELECTED_TAB_INDEX, -1);
                            Log.d(TAG, "Received result: Switch to tab " + selectedIndex);
                            if (selectedIndex != -1) {
                                switchToTab(selectedIndex);
                            }
                        } else if (data.hasExtra(TabSwitcherActivity.RESULT_CLOSED_TAB_INDEX)) {
                            int closedIndex = data.getIntExtra(TabSwitcherActivity.RESULT_CLOSED_TAB_INDEX, -1);
                            Log.d(TAG, "Received result: Close tab " + closedIndex);
                            if (closedIndex != -1) {
                                closeTab(closedIndex);
                            }
                        } else if (data.hasExtra(TabSwitcherActivity.RESULT_CREATE_NEW_TAB)) {
                            Log.d(TAG, "Received result: Create new tab");
                            createNewTab(true);
                        } else if (data.hasExtra(TabSwitcherActivity.RESULT_CLEAR_ALL_TABS)) { // New: Handle clear all tabs
                            Log.d(TAG, "Received result: Clear all tabs");
                            clearAllTabsAndCreateNew();
                        }
                    }
                }
            });

        // Register ActivityResultLauncher for file picking
        handleTorrentIntent(getIntent());
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Use MainActivity.this to explicitly access instance fields
                if (MainActivity.this.pendingFilePrompt == null) { 
                    Log.w(TAG, "File picker result received, but MainActivity.this.pendingFilePrompt is null.");
                    // If pendingGeckoResultForFilePrompt is also somehow non-null here, attempt to complete it.
                    if (MainActivity.this.pendingGeckoResultForFilePrompt != null) {
                        Log.w(TAG, "Completing MainActivity.this.pendingGeckoResultForFilePrompt with null as prompt was null.");
                        MainActivity.this.pendingGeckoResultForFilePrompt.complete(null);
                        MainActivity.this.pendingGeckoResultForFilePrompt = null; // Clean up the result field
                    }
                    return; // Nothing more to do if the prompt reference is gone.
                }

                PromptResponse response = null; // To hold the response from confirm/dismiss

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        List<Uri> urisToConfirm = new ArrayList<>();
                        List<File> tempFilesCreated = new ArrayList<>(); // For managing temp files created in this operation

                        if (data.getData() != null) { // Single file selected
                            Uri contentUri = data.getData();
                            Log.d(TAG, "LAUNCHER: Single file selected: " + contentUri.toString());
                            File tempFile = null;
                            try {
                                getContentResolver().takePersistableUriPermission(contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                InputStream inputStream = getContentResolver().openInputStream(contentUri);
                                if (inputStream != null) {
                                    String originalFileName = "upload.tmp";
                                    try (android.database.Cursor cursor = getContentResolver().query(contentUri, null, null, null, null)) {
                                        if (cursor != null && cursor.moveToFirst()) {
                                            int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                                            if (nameIndex != -1) { originalFileName = cursor.getString(nameIndex); }
                                        }
                                    }
                                    originalFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                                    tempFile = new File(getCacheDir(), originalFileName);
                                    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                                        byte[] buffer = new byte[4 * 1024]; int read;
                                        while ((read = inputStream.read(buffer)) != -1) { outputStream.write(buffer, 0, read); }
                                        outputStream.flush();
                                    }
                                    inputStream.close();
                                    urisToConfirm.add(Uri.fromFile(tempFile));
                                    tempFilesCreated.add(tempFile);
                                } else {
                                    throw new java.io.IOException("Failed to open input stream for content URI: " + contentUri.toString());
                                }
                                Log.d(TAG, "LAUNCHER: Temp file created. Confirming with URI.");
                                response = MainActivity.this.pendingFilePrompt.confirm(MainActivity.this, urisToConfirm.get(0));
                                tempUploadFiles.addAll(tempFilesCreated); // Add to global list for onDestroy cleanup
                            } catch (Exception e) {
                                Log.e(TAG, "LAUNCHER: Error processing single file for prompt", e);
                                if (tempFile != null && tempFile.exists()) { tempFile.delete(); }
                                // Ensure prompt is dismissed on error
                                if (MainActivity.this.pendingFilePrompt != null) { response = MainActivity.this.pendingFilePrompt.dismiss(); }
                            }
                        } else if (data.getClipData() != null) { // Multiple files selected
                            ClipData clipData = data.getClipData();
                            Log.d(TAG, "LAUNCHER: Multiple files selected: " + clipData.getItemCount() + " items.");
                            boolean anyFileProcessedSuccessfully = false;
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri contentUri = clipData.getItemAt(i).getUri();
                                File tempFile = null;
                                try {
                                    getContentResolver().takePersistableUriPermission(contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    InputStream inputStream = getContentResolver().openInputStream(contentUri);
                                    if (inputStream != null) {
                                        String originalFileName = "upload_" + i + ".tmp";
                                        try (android.database.Cursor cursor = getContentResolver().query(contentUri, null, null, null, null)) {
                                            if (cursor != null && cursor.moveToFirst()) {
                                                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                                                if (nameIndex != -1) { originalFileName = cursor.getString(nameIndex); }
                                            }
                                        }
                                        originalFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                                        tempFile = new File(getCacheDir(), originalFileName);
                                        // (Copy stream to tempFile - omitted for brevity, assume it's here)
                                        urisToConfirm.add(Uri.fromFile(tempFile));
                                        tempFilesCreated.add(tempFile);
                                        anyFileProcessedSuccessfully = true; // Mark if at least one file is processed
                                    } else { 
                                        throw new java.io.IOException("InputStream null for multi-file item " + i); 
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "LAUNCHER: Error processing a multi-file item", e);
                                    if (tempFile != null && tempFile.exists()) { tempFile.delete(); }
                                    // Continue to next file, or decide on partial failure strategy
                                }
                            }

                            if (anyFileProcessedSuccessfully && !urisToConfirm.isEmpty()) {
                                Log.d(TAG, "LAUNCHER: Confirming multiple files.");
                                response = MainActivity.this.pendingFilePrompt.confirm(MainActivity.this, urisToConfirm.toArray(new Uri[0]));
                                tempUploadFiles.addAll(tempFilesCreated);
                            } else {
                                Log.w(TAG, "LAUNCHER: No files processed successfully for multi-select, or list empty. Dismissing.");
                                // Clean up any temp files created before error if handling partial failure
                                for(File f : tempFilesCreated) { if(f.exists()) f.delete(); }
                                if (MainActivity.this.pendingFilePrompt != null) { response = MainActivity.this.pendingFilePrompt.dismiss(); }
                            }
                        } else { // No data.getData() and no data.getClipData()
                            Log.w(TAG, "LAUNCHER: File picker OK, but no actual URI data (single or multiple).");
                            if (MainActivity.this.pendingFilePrompt != null) { response = MainActivity.this.pendingFilePrompt.dismiss(); }
                        }
                    } else { // Data intent is null
                        Log.w(TAG, "LAUNCHER: File picker OK, but data intent was null.");
                        if (MainActivity.this.pendingFilePrompt != null) { response = MainActivity.this.pendingFilePrompt.dismiss(); }
                    }
                } else { // Result code is not OK (e.g., cancelled by user)
                    Log.d(TAG, "LAUNCHER: File picker cancelled by user.");
                    if (MainActivity.this.pendingFilePrompt != null) { response = MainActivity.this.pendingFilePrompt.dismiss(); }
                }

                // Complete the GeckoResult that the main onFilePrompt returned
                if (MainActivity.this.pendingGeckoResultForFilePrompt != null) {
                    Log.d(TAG, "LAUNCHER: Completing pendingGeckoResultForFilePrompt.");
                    MainActivity.this.pendingGeckoResultForFilePrompt.complete(response); 
                } else {
                    Log.w(TAG, "LAUNCHER: MainActivity.pendingGeckoResultForFilePrompt was null when trying to complete it.");
                }

                // Clean up MainActivity instance fields, regardless of path taken
                MainActivity.this.pendingFilePrompt = null;
                MainActivity.this.pendingGeckoResultForFilePrompt = null;
                Log.d(TAG, "LAUNCHER: Cleaned up pending prompt and result fields.");
            }
        );

        // --- Setup Listeners --- 
        setupButtonListeners();
        setupUrlBarListener();
        setupGeckoViewTouchListener();
        setupSettingsPanelListeners();
        setupGlobalLayoutListener(); // Listen for keyboard visibility changes
        showExpandedBar(); // New initial state call

        // Ensure GeckoView uses the correct session after potential restore
        if (getActiveSession() != null && geckoView.getSession() != getActiveSession()) {
             Log.d(TAG, "Setting GeckoView session after restore/create");
             geckoView.setSession(getActiveSession());
             updateUIForActiveSession();
             // Show snapshot overlay for perceived continuity on reopened app
             showSnapshotOverlayForActiveTab();
        } else if (getActiveSession() == null && !geckoSessionList.isEmpty()) {
             // Fallback if active index was invalid after restore
             Log.w(TAG, "Active session null after restore, defaulting to index 0");
             activeSessionIndex = 0;
             geckoView.setSession(getActiveSession());
             updateUIForActiveSession();
             showSnapshotOverlayForActiveTab();
        } else if (getActiveSession() == null && geckoSessionList.isEmpty()) {
             // This case should have been handled by creating an initial tab, but log just in case
             Log.e(TAG, "Error: No active session and session list is empty after onCreate logic.");
        }

        if (isTvDevice()) {
            mainHandler.postDelayed(() -> {
                try {
                    GeckoSession active = getActiveSession();
                    if (active == null || geckoView == null) return;
                    if (geckoView.getSession() != active) return;

                    String url = sessionUrlMap.get(active);
                    if (url == null) url = "";
                    String lower = url.toLowerCase();

                    if (lower.isEmpty() || lower.startsWith("about:blank")) {
                        String fallback = getDefaultHomepageUrl();
                        if (fallback == null || fallback.isEmpty()) fallback = "about:blank";
                        Log.w(TAG, "[TVRestoreFix] Active tab is blank after restore; loading fallback: " + fallback);
                        sessionUrlMap.put(active, fallback);
                        mLastValidUrl = fallback;
                        active.loadUri(fallback);
                        updateUIForActiveSession();
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "[TVRestoreFix] Failed applying restore fallback", t);
                }
            }, 800);
        }

        // Register download complete receiver
        downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    Toast.makeText(MainActivity.this, "Your Download is complete", Toast.LENGTH_SHORT).show();
                }
            }
        };
        // Register the receiver using ContextCompat to ensure compatibility and security.
        // RECEIVER_NOT_EXPORTED ensures the receiver only handles system broadcasts, not from other apps.
        ContextCompat.registerReceiver(this, downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED);

        // Defer storage scan to reduce startup contention
        mainHandler.postDelayed(this::checkAndShowStorageWarning, 5000);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Signal that all UI components are initialized and ready for updates.
        // This prevents race conditions where navigation events try to update the UI before it's ready.
        isUiReady = true;

        // After UI is ready, it's safe to do an initial UI update if needed.
        updateUIForActiveSession();
    }

    private void checkAndShowStorageWarning() {
        if (storageCheckInFlight) {
            Log.d(TAG, "[StorageDebug] Storage check already in flight; skipping");
            return;
        }
        storageCheckInFlight = true;
        try {
            ioExecutor.execute(() -> {
            long totalBytes = 0L;
            try {
                totalBytes = getAppUsedStorageBytes();
            } catch (Throwable t) {
                Log.w(TAG, "[StorageDebug] Failed to compute storage size", t);
            }
            final long resultBytes = totalBytes;
            runOnUiThread(() -> {
                try {
                    Log.d(TAG, "[StorageDebug] App used storage: " + resultBytes + " bytes (" + (resultBytes / (1024.0 * 1024.0)) + " MB)");
                    if (resultBytes >= 400L * 1024 * 1024) { // 400 MB
                        // Avoid showing a dialog when the activity is not in a good UI state
                        boolean safeToShow = !isFinishing() && !isDestroyed();
                        try {
                            View decor = getWindow() != null ? getWindow().getDecorView() : null;
                            safeToShow = safeToShow && decor != null && decor.isShown();
                        } catch (Throwable ignore) {}
                        if (safeToShow) {
                            Log.d(TAG, "[StorageDebug] Threshold exceeded, showing storage warning dialog.");
                            showStorageWarningDialog();
                        } else {
                            Log.d(TAG, "[StorageDebug] Threshold exceeded but window not focused/visible; deferring dialog.");
                        }
                    } else {
                        Log.d(TAG, "[StorageDebug] Threshold not reached, no dialog.");
                    }
                } finally {
                    storageCheckInFlight = false;
                }
            });
        });
        } catch (java.util.concurrent.RejectedExecutionException rex) {
            // Can occur in tests or during shutdown if executor has been terminated; skip gracefully.
            Log.w(TAG, "[StorageDebug] Executor rejected task (activity shutting down or test env). Skipping.");
            storageCheckInFlight = false;
        }
    }

    // --- Snapshot overlay helpers ---
    private void showSnapshotOverlayForActiveTab() {
        // Snapshot overlay disabled
        Log.d(TAG, "[SnapshotOverlay] Disabled");
    }

    private void hideSnapshotOverlayImmediate() { /* overlay disabled */ }

    // Focus the nearest scrollable element under the cursor to let Gecko handle Arrow key scrolling natively
    private void tryFocusScrollableAtCursor(GeckoSession session, @Nullable Runnable onFail) {
        if (session == null || geckoView == null || tvCursorView == null) {
            if (onFail != null) onFail.run();
            return;
        }
        int[] cursorLoc = new int[2];
        int[] viewLoc = new int[2];
        tvCursorView.getLocationOnScreen(cursorLoc);
        geckoView.getLocationOnScreen(viewLoc);
        float xDevice = (cursorLoc[0] - viewLoc[0]) + (tvCursorView.getWidth() / 2f);
        float yDevice = (cursorLoc[1] - viewLoc[1]) + (tvCursorView.getHeight() / 2f);

        org.mozilla.geckoview.WebExtension.Port port = tvPort;
        if (port == null) {
            if (isTvDevice()) ensureTvScrollExtension();
            if (onFail != null) onFail.run();
            return;
        }
        String reqId = "f" + (tvReqCounter++);
        org.json.JSONObject msg = new org.json.JSONObject();
        try {
            final float dpr = getResources().getDisplayMetrics().density;
            msg.put("id", reqId);
            msg.put("cmd", "focusScrollableAtPoint");
            msg.put("x", xDevice);
            msg.put("y", yDevice);
            msg.put("dpr", dpr);
        } catch (org.json.JSONException je) {
            if (onFail != null) onFail.run();
            return;
        }
        try {
            port.postMessage(msg);
            Log.d(TAG, "[TV][EXT] sent focusScrollableAtPoint reqId=" + reqId + " dev=(" + xDevice + "," + yDevice + ")");
        } catch (Exception e) {
            if (onFail != null) onFail.run();
        }
    }

    // Variant: apply a small downward CSS y-offset to keep hit-testing inside content when at the very top edge.
    private void tryFocusScrollableAtCursorWithYOffset(GeckoSession session, float yOffsetCss, @Nullable Runnable onFail) {
        if (session == null || geckoView == null || tvCursorView == null) {
            if (onFail != null) onFail.run();
            return;
        }
        int[] cursorLoc = new int[2];
        int[] viewLoc = new int[2];
        tvCursorView.getLocationOnScreen(cursorLoc);
        geckoView.getLocationOnScreen(viewLoc);
        float xDevice = (cursorLoc[0] - viewLoc[0]) + (tvCursorView.getWidth() / 2f);
        float yDevice = (cursorLoc[1] - viewLoc[1]) + (tvCursorView.getHeight() / 2f);

        org.mozilla.geckoview.WebExtension.Port port = tvPort;
        if (port == null) {
            if (isTvDevice()) ensureTvScrollExtension();
            if (onFail != null) onFail.run();
            return;
        }
        String reqId = "f" + (tvReqCounter++);
        org.json.JSONObject msg = new org.json.JSONObject();
        try {
            final float dpr = getResources().getDisplayMetrics().density;
            // push the hit-test point slightly down into the content area (CSS -> device)
            yDevice += (yOffsetCss * dpr);
            msg.put("id", reqId);
            msg.put("cmd", "focusScrollableAtPoint");
            msg.put("x", xDevice);
            msg.put("y", yDevice);
            msg.put("dpr", dpr);
        } catch (org.json.JSONException je) {
            if (onFail != null) onFail.run();
            return;
        }
        try {
            port.postMessage(msg);
            Log.d(TAG, "[TV][EXT] sent focusScrollableAtPoint (yOffsetCss=" + yOffsetCss + ") reqId=" + reqId + " dev=(" + xDevice + "," + yDevice + ")");
        } catch (Exception e) {
            if (onFail != null) onFail.run();
        }
    }

    // --- TV Scroll Extension Helpers ---
    private org.mozilla.geckoview.WebExtension.Port tvPort = null;
    private final Map<String, Runnable> tvPendingFail = new HashMap<>();
    private int tvReqCounter = 1;
    // Track requests initiated from top-edge UP so we can react to ok=false immediately
    private final Map<String, Boolean> tvPendingTopEdgeUp = new HashMap<>();
    private volatile String tvPendingMenuNavDir = null;
    private volatile long tvLastEnsureExtAtMs = 0L;
    private volatile boolean tvEnabledAnnounced = false;
    private volatile boolean tvMessageDelegateSet = false;

    // Send a tv-menu-nav command to the content script via background port
    private void sendTvMenuNav(String dir) {
        org.mozilla.geckoview.WebExtension.Port port = tvPort;
        if (port == null) {
            Log.w(TAG, "[TV][DPAD] tv-menu-nav not sent: BG port null");
            if (isTvDevice()) {
                tvPendingMenuNavDir = dir;
                long now = System.currentTimeMillis();
                if (now - tvLastEnsureExtAtMs > 1500) {
                    tvLastEnsureExtAtMs = now;
                    ensureTvScrollExtension();
                }
            }
            return;
        }
        try {
            JSONObject msg = new JSONObject();
            msg.put("type", "tv-menu-nav");
            msg.put("dir", dir);
            port.postMessage(msg);
            Log.d(TAG, "[TV][DPAD] sent tv-menu-nav dir=" + dir);
        } catch (Exception e) {
            Log.e(TAG, "[TV][DPAD] sendTvMenuNav error", e);
        }
    }

    // We now communicate with the background page's native port instead of content directly.
    // No session message delegate required.
    private void ensureTvScrollExtension() {
        if (!isTvDevice()) {
            Log.d(TAG, "[TV][EXT] ensureTvScrollExtension skipped (non-TV device)");
            return;
        }
        if (runtime == null) return;
        try {
            org.mozilla.geckoview.WebExtensionController controller = runtime.getWebExtensionController();
            if (controller == null) {
                Log.w(TAG, "[TV][EXT] ensureTvScrollExtension: controller is null (will rely on retry if scheduled)");
                return;
            }
            // Use folder path with trailing slash; no explicit ID for simple asset extension
            controller.ensureBuiltIn("resource://android/assets/webext/", "tv-scroll-helper@neew.browser")
                .accept(ext -> {
                    tvScrollExtension = ext;
                    Log.i(TAG, "[TV][EXT] ensured built-in extension: " + (ext != null ? ext.id : "null"));
                    if (ext != null) {
                        // Background messaging delegate (owns native port via background.js)
                        org.mozilla.geckoview.WebExtension.MessageDelegate bgDelegate = new org.mozilla.geckoview.WebExtension.MessageDelegate() {
                            @Override
                            public void onConnect(@NonNull org.mozilla.geckoview.WebExtension.Port port) {
                                Log.i(TAG, "[TV][EXT] onConnect (BG) port=" + port + " wasAnnounced=" + tvEnabledAnnounced);
                                org.mozilla.geckoview.WebExtension.PortDelegate portDelegate = new org.mozilla.geckoview.WebExtension.PortDelegate() {
                                    @Override
                                    public void onPortMessage(@NonNull Object message, @NonNull org.mozilla.geckoview.WebExtension.Port p) {
                                        Log.d(TAG, "[TV][EXT] onPortMessage BG msg=" + String.valueOf(message));
                                        try {
                                            String id = null;
                                            String type = null;
                                            Boolean ok = null;
                                            if (message instanceof JSONObject) {
                                                JSONObject jo = (JSONObject) message;
                                                id = jo.optString("id", null);
                                                type = jo.optString("type", null);
                                                if (jo.has("ok")) ok = jo.optBoolean("ok", false);
                                                if ("tv-menu-nav-ack".equals(type)) { return; }
                                                // Surface content-script diagnostics
                                                if ("tv-ext-log".equals(type)) {
                                                    String text = jo.optString("text", "");
                                                    if (!text.isEmpty()) {
                                                        Log.d(TAG, "[TV][EXT][APP] " + text);
                                                    }
                                                    return;
                                                }
                                                // Optional: content script warmup
                                                if ("content_ready".equals(type)) {
                                                    String url = jo.optString("url", "");
                                                    Log.d(TAG, "[TV][EXT][CT] content_ready url=" + url);
                                                    // Do not return; other fields may be relevant
                                                }
                                            } else if (message instanceof String) {
                                                try {
                                                    JSONObject jo = new JSONObject((String) message);
                                                    id = jo.optString("id", null);
                                                    type = jo.optString("type", null);
                                                    if (jo.has("ok")) ok = jo.optBoolean("ok", false);
                                                    if ("tv-menu-nav-ack".equals(type)) {
                                                        return;
                                                    }
                                                    if ("tv-ext-log".equals(type)) {
                                                        String text = jo.optString("text", "");
                                                        if (!text.isEmpty()) {
                                                            Log.d(TAG, "[TV][EXT][APP] " + text);
                                                        }
                                                        return;
                                                    }
                                                    if ("content_ready".equals(type)) {
                                                        String url = jo.optString("url", "");
                                                        Log.d(TAG, "[TV][EXT][CT] content_ready url=" + url);
                                                    }
                                                } catch (Exception ignored) {
                                                }
                                            } else if (message instanceof Map) {
                                                Map<?, ?> map = (Map<?, ?>) message;
                                                Object idObj = map.get("id");
                                                if (idObj != null) id = String.valueOf(idObj);
                                                Object typeObj = map.get("type");
                                                if (typeObj != null) type = String.valueOf(typeObj);
                                                Object okObj = map.get("ok");
                                                if (okObj instanceof Boolean) ok = (Boolean) okObj;
                                                if ("tv-menu-nav-ack".equals(type)) { return; }
                                                if ("tv-ext-log".equals(type)) {
                                                    Object textObj = map.get("text");
                                                    String text = textObj != null ? String.valueOf(textObj) : "";
                                                    if (!text.isEmpty()) {
                                                        Log.d(TAG, "[TV][EXT][APP] " + text);
                                                    }
                                                    return;
                                                }
                                                if ("content_ready".equals(type)) {
                                                    Object urlObj = map.get("url");
                                                    String url = urlObj != null ? String.valueOf(urlObj) : "";
                                                    Log.d(TAG, "[TV][EXT][CT] content_ready url=" + url);
                                                }
                                            }
                                            if (id != null && "scrollAtPoint:done".equals(type)) {
                                                String used = null;
                                                if (message instanceof String) {
                                                    try {
                                                        JSONObject jo = new JSONObject((String) message);
                                                        Object usedObj = jo.opt("used");
                                                        if (usedObj != null) used = String.valueOf(usedObj);
                                                    } catch (Exception ignored) {}
                                                } else if (message instanceof Map) {
                                                    Object usedObj = ((Map<?, ?>) message).get("used");
                                                    if (usedObj != null) used = String.valueOf(usedObj);
                                                }
                                                Runnable failCb;
                                                synchronized (tvPendingFail) {
                                                    failCb = tvPendingFail.remove(id);
                                                }
                                                if (failCb != null) {
                                                    Log.d(TAG, "[TV][EXT] cleared timeout for id=" + id);
                                                }
                                                boolean wasTopEdgeUp;
                                                synchronized (tvPendingTopEdgeUp) {
                                                    wasTopEdgeUp = tvPendingTopEdgeUp.remove(id) == Boolean.TRUE;
                                                }
                                                if (wasTopEdgeUp) {
                                                    final boolean moved = Boolean.TRUE.equals(ok);
                                                    Log.d(TAG, "[TV][EXT] topEdgeUp result ok=" + moved + ", used=" + used + " id=" + id);
                                                    if (!moved) {
                                                        // No element or root movement -> decide app behavior now
                                                        // If root can scroll up, do it; otherwise enter UI focus
                                                        runOnUiThread(() -> {
                                                            if (mCanScrollUp) {
                                                                Log.d(TAG, "[TV][SCROLL] topEdgeUp ok=false but root can scroll -> rootScrollUp (debounced)");
                                                                runDebouncedRootScroll(true);
                                                            } else {
                                                                Log.d(TAG, "[TV][FOCUS] topEdgeUp ok=false and root cannot scroll -> enterUiFocus");
                                                                setUiFocus(true);
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    // For non-top-edge requests (i.e., bottom-edge DOWN attempts), if content couldn't scroll (ok=false),
                                                    // fallback to root scroll down so the page still moves.
                                                    if (ok != null && !ok) {
                                                        Log.d(TAG, "[TV][SCROLL] nonTopEdge ok=false -> rootScrollDown (debounced), used=" + used);
                                                        runOnUiThread(() -> runDebouncedRootScroll(false));
                                                    }
                                                }
                                            }
                                        } catch (Throwable t) {
                                            Log.w(TAG, "[TV][EXT] BG onPortMessage parse", t);
                                        }
                                    }

                                    @Override
                                    public void onDisconnect(@NonNull org.mozilla.geckoview.WebExtension.Port p) {
                                        Log.d(TAG, "[TV][EXT] BG port disconnected");
                                        if (p == tvPort) {
                                            tvPort = null;
                                            tvEnabledAnnounced = false; // Reset so we re-announce on reconnect
                                        }
                                        if (isTvDevice()) {
                                            long now = System.currentTimeMillis();
                                            if (now - tvLastEnsureExtAtMs > 1500) {
                                                tvLastEnsureExtAtMs = now;
                                                ensureTvScrollExtension();
                                            }
                                        }
                                    }
                                };
                                port.setDelegate(portDelegate);
                                tvPort = port;
                                // Announce TV mode to background so it (un)registers content.js accordingly
                                // Send this every time we connect (including after process restarts)
                                if (!tvEnabledAnnounced) {
                                    try {
                                        org.json.JSONObject tvMsg = new org.json.JSONObject();
                                        tvMsg.put("type", "tv-enabled");
                                        tvMsg.put("enabled", isTvDevice());
                                        port.postMessage(tvMsg);
                                        tvEnabledAnnounced = true;
                                        Log.i(TAG, "[TV][EXT] announced tv-enabled=" + isTvDevice());
                                    } catch (Exception e) {
                                        Log.w(TAG, "[TV][EXT] failed to announce tv-enabled", e);
                                    }
                                } else {
                                    Log.d(TAG, "[TV][EXT] tv-enabled already announced for this connection");
                                }

                                try {
                                    final String pendingDir = tvPendingMenuNavDir;
                                    tvPendingMenuNavDir = null;
                                    if (pendingDir != null) {
                                        JSONObject msg = new JSONObject();
                                        msg.put("type", "tv-menu-nav");
                                        msg.put("dir", pendingDir);
                                        port.postMessage(msg);
                                        Log.d(TAG, "[TV][DPAD] flushed pending tv-menu-nav dir=" + pendingDir);
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "[TV][DPAD] failed to flush pending tv-menu-nav", e);
                                }
                            }
                        };
                        // Namespace must match background.js connectNative('neewbrowser')
                        // CRITICAL: Only set the delegate once to avoid breaking existing connections
                        if (!tvMessageDelegateSet) {
                            runOnUiThread(() -> {
                                try {
                                    Log.i(TAG, "[TV][EXT] Setting message delegate for namespace 'neewbrowser'");
                                    ext.setMessageDelegate(bgDelegate, "neewbrowser");
                                    tvMessageDelegateSet = true;
                                    Log.i(TAG, "[TV][EXT] Message delegate set successfully for namespace 'neewbrowser'");
                                } catch (Exception e) {
                                    Log.e(TAG, "[TV][EXT] setMessageDelegate failed", e);
                                }
                            });
                        } else {
                            Log.d(TAG, "[TV][EXT] Message delegate already set, skipping re-registration");
                        }
                        // No session delegate; background relays to content.
                    }
                });
        } catch (Exception e) {
            Log.e(TAG, "[TV][EXT] ensureTvScrollExtension exception", e);
        }
    }

    private void scheduleEnsureTvScrollExtensionRetries(int attempts, long delayMs) {
        if (attempts <= 0) return;
        if (!isTvDevice()) return;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isTvDevice()) return;
            Log.d(TAG, "[TV][EXT] retry ensureTvScrollExtension attemptsLeft=" + attempts);
            ensureTvScrollExtension();
            if (attempts - 1 > 0 && tvPort == null) {
                scheduleEnsureTvScrollExtensionRetries(attempts - 1, delayMs);
            }
        }, delayMs);
    }

    private void ensureTvScrollExtension(GeckoSession session) {
        if (!isTvDevice()) return;
        // No per-session attach needed for content scripts, but we ensure once globally
        ensureTvScrollExtension();
        // Optionally refresh DPR for this session
        if (tvElementScrollEnabled) {
            queryTvDpr(session);
        }
    }

    private void queryTvDpr(GeckoSession session) {
        // Fallback: use Android display density as DPR approximation
        try {
            float density = getResources().getDisplayMetrics().density;
            tvDevicePixelRatio = density > 0 ? density : 1.0f;
            Log.d(TAG, "[TV][EXT] DPR (approx) from Android density: " + tvDevicePixelRatio);
        } catch (Exception e) {
            tvDevicePixelRatio = 1.0f;
            Log.w(TAG, "[TV][EXT] DPR fallback to 1.0 due to exception", e);
        }
    }

    private void tryElementScrollAtCursor(GeckoSession session, int dyCss, @Nullable Runnable onFail) {
        tryElementScrollAtCursorWithRetryFlag(session, dyCss, onFail, true, false);
    }

    private void tryElementScrollAtCursorWithRetryFlag(GeckoSession session, int dyCss, @Nullable Runnable onFail, boolean allowRetry) {
        tryElementScrollAtCursorWithRetryFlag(session, dyCss, onFail, allowRetry, false);
    }

    // Internal: tagTopEdgeUp indicates this request originated from DPAD_UP at top edge
    private void tryElementScrollAtCursorWithRetryFlag(GeckoSession session, int dyCss, @Nullable Runnable onFail, boolean allowRetry, boolean tagTopEdgeUp) {
        if (session == null || geckoView == null || tvCursorView == null) {
            if (onFail != null) onFail.run();
            return;
        }

        // Compute cursor center relative to GeckoView (device pixels). Send device coords to content;
        // let content convert using its window.devicePixelRatio. This avoids mismatches.
        int[] cursorLoc = new int[2];
        int[] viewLoc = new int[2];
        tvCursorView.getLocationOnScreen(cursorLoc);
        geckoView.getLocationOnScreen(viewLoc);
        float xDevice = (cursorLoc[0] - viewLoc[0]) + (tvCursorView.getWidth() / 2f);
        float yDevice = (cursorLoc[1] - viewLoc[1]) + (tvCursorView.getHeight() / 2f);
        final float dpr = getResources().getDisplayMetrics().density;
        // If this is a top-edge UP request, nudge the hit-test point down by ~40 CSS px so we stay within the child container area.
        if (tagTopEdgeUp && dyCss < 0) {
            yDevice += (120f * dpr);
        }

        // Send message over native messaging Port; if missing, allow a one-shot delayed retry
        org.mozilla.geckoview.WebExtension.Port port = tvPort;
        if (port == null) {
            Log.w(TAG, "[TV][EXT] no Port for session");
            if (isTvDevice()) ensureTvScrollExtension();
            if (allowRetry) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                    tryElementScrollAtCursorWithRetryFlag(session, dyCss, onFail, false), 250);
            } else {
                if (onFail != null) onFail.run();
            }
            return;
        }
        String reqId = "r" + (tvReqCounter++);
        org.json.JSONObject msg = new org.json.JSONObject();
        try {
            msg.put("id", reqId);
            msg.put("cmd", "scrollAtPoint");
            msg.put("x", xDevice);
            msg.put("y", yDevice);
            msg.put("dy", dyCss);
            msg.put("dpr", dpr);
        } catch (org.json.JSONException je) {
            Log.e(TAG, "[TV][EXT] JSON build error", je);
            if (onFail != null) onFail.run();
            return;
        }

        if (tagTopEdgeUp) {
            synchronized (tvPendingTopEdgeUp) { tvPendingTopEdgeUp.put(reqId, Boolean.TRUE); }
        }

        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable timeout = () -> {
            Runnable failCb;
            synchronized (tvPendingFail) {
                failCb = tvPendingFail.remove(reqId);
            }
            if (failCb != null) {
                Log.w(TAG, "[TV][EXT] scrollAtPoint timeout -> fallback");
                failCb.run();
            }
        };
        synchronized (tvPendingFail) {
            tvPendingFail.put(reqId, onFail != null ? onFail : () -> {});
        }
        handler.postDelayed(timeout, TV_EXT_TIMEOUT_MS);
        try {
            port.postMessage(msg);
            Log.d(TAG, "[TV][EXT] sent scrollAtPoint reqId=" + reqId + " dyCss=" + dyCss + " dev=(" + xDevice + "," + yDevice + ")");
        } catch (Exception e) {
            synchronized (tvPendingFail) { tvPendingFail.remove(reqId); }
            synchronized (tvPendingTopEdgeUp) { tvPendingTopEdgeUp.remove(reqId); }
            handler.removeCallbacks(timeout);
            Log.e(TAG, "[TV][EXT] port.postMessage failed -> fallback", e);
            if (onFail != null) onFail.run();
        }
    }

    // Convenience overload to use default dy based on direction; currently unused outside DPAD paths
    private void tryElementScrollAtCursor(GeckoSession session) {
        tryElementScrollAtCursor(session, TV_EXT_SCROLL_CSS_PX, null);
    }

    private long getAppUsedStorageBytes() {
        long total = 0;
        total += getDirSize(getFilesDir());
        total += getDirSize(getCacheDir());
        // Add databases
        String[] dbList = databaseList();
        for (String db : dbList) {
            File dbFile = getDatabasePath(db);
            if (dbFile.exists()) total += dbFile.length();
        }
        // Add shared prefs
        File prefsDir = new File(getApplicationInfo().dataDir, "shared_prefs");
        if (prefsDir.exists()) total += getDirSize(prefsDir);
        return total;
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) size += f.length();
                    else size += getDirSize(f);
                }
            }
        }
        return size;
    }

    private void showStorageWarningDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_storage_warning);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Adjust dialog width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnCleanUp).setOnClickListener(v -> {
            // Run cleanup asynchronously to prevent ANRs
            Toast.makeText(MainActivity.this, "Cleaning cache...", Toast.LENGTH_SHORT).show();
            v.setEnabled(false);
            View btnDeep = dialog.findViewById(R.id.btnDeepClean);
            View btnClose = dialog.findViewById(R.id.btnClose);
            if (btnDeep != null) btnDeep.setEnabled(false);
            if (btnClose != null) btnClose.setEnabled(false);

            ioExecutor.execute(() -> {
                int filesCount = 0;
                long bytes = 0L;
                try {
                    File cache = getCacheDir();
                    filesCount = countFiles(cache);
                    bytes = getDirSize(cache);
                    deleteDir(cache);
                } catch (Throwable t) {
                    Log.e(TAG, "Cache cleanup failed", t);
                }
                final int fFiles = filesCount;
                final long fBytes = bytes;
                runOnUiThread(() -> {
                    try {
                        if (dialog.isShowing()) dialog.dismiss();
                    } catch (Throwable ignore) {}
                    showCleanupDoneDialog(fFiles, fBytes);
                });
            });
        });
        dialog.findViewById(R.id.btnDeepClean).setOnClickListener(v -> {
            dialog.dismiss();
            showDeepCleanWarningDialog();
        });
        dialog.show();
    }

    private int countFiles(File dir) {
        int count = 0;
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) count++;
                    else count += countFiles(f);
                }
            }
        }
        return count;
    }

    private void deleteDir(File dir) {
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) deleteDir(f);
                    else f.delete();
                }
            }
        }
    }

    private void showCleanupDoneDialog(int fileCount, long sizeBytes) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_storage_cleanup_done);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Adjust dialog width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        String sizeMb = new DecimalFormat("#").format(sizeBytes / (1024.0 * 1024.0));
        String desc = "Deleted " + fileCount + " files & reduced " + sizeMb + " MB storage. Now enjoy faster browser.";
        ((TextView) dialog.findViewById(R.id.tvDescription)).setText(desc);
        dialog.findViewById(R.id.btnOk).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeepCleanWarningDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_storage_deepclean_warning);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Adjust dialog width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.btnOk).setOnClickListener(v -> {
            dialog.dismiss();
            performDeepClean();
        });
        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void performDeepClean() {
        Log.d(TAG, "[StorageDebug] performDeepClean CALLED");
        // Delete user data: files, cache, prefs, dbs
        deleteDir(getFilesDir());
        deleteDir(getCacheDir());
        // Delete shared prefs
        File prefsDir = new File(getApplicationInfo().dataDir, "shared_prefs");
        deleteDir(prefsDir);
        // Delete databases
        String[] dbList = databaseList();
        for (String db : dbList) {
            File dbFile = getDatabasePath(db);
            if (dbFile.exists()) dbFile.delete();
        }
        showDeepCleanDoneDialog();
    }

    private void showDeepCleanDoneDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_storage_deepclean_done);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Adjust dialog width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.btnOk).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Helper to get the currently active session
    private @Nullable GeckoSession getActiveSession() {
        if (activeSessionIndex >= 0 && activeSessionIndex < geckoSessionList.size()) {
            return geckoSessionList.get(activeSessionIndex);
        }
        return null;
    }

    // Refactored method to set up common button listeners
    private void setupButtonListeners() {
        if (backButton != null) {
        backButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                // Only acts if there is history; UI updates will happen on navigation callbacks
                activeSession.goBack();
            }
        });
        }

        if (forwardButton != null) {
        forwardButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
                if (activeSession != null) {
                    activeSession.goForward();
                }
        });
        }

        if (refreshButton != null) {
        refreshButton.setOnClickListener(v -> {
            forceReloadActiveSession("refreshButton");
        });
        }

        if (settingsButton != null) {
        settingsButton.setOnClickListener(v -> toggleSettingsPanel());
        }
        
        if (downloadsButton != null) {
        downloadsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
            startActivity(intent);
        });
        }

        if (newTabButton != null) {
        newTabButton.setOnClickListener(v -> {
            Log.d(TAG, "Expanded New Tab button clicked.");
                if (!isTvDevice()) {
            showExpandedBar(); // Ensure expanded bar is shown
                }
            createNewTab(true); // Create and switch to the new tab
        });
        }

        if (tabsButton != null) {
        tabsButton.setOnClickListener(v -> {
            Log.d(TAG, "Tabs button clicked.");
                if (!isTvDevice()) {
                    showExpandedBar();
                }
            v.post(() -> {
                Log.d(TAG, "Executing launchTabSwitcher from post.");
            launchTabSwitcher();
            });
        });
        }

        // Add listeners for MINIMIZED buttons only if they exist
        if (minimizedBackButton != null) {
        minimizedBackButton.setOnClickListener(v -> {
             GeckoSession activeSession = getActiveSession();
             if (activeSession != null) {
                 activeSession.goBack();
             }
        });
        }

        if (minimizedForwardButton != null) {
        minimizedForwardButton.setOnClickListener(v -> {
             GeckoSession activeSession = getActiveSession();
             if (activeSession != null) {
                 activeSession.goForward();
             }
        });
        }

        if (minimizedRefreshButton != null) {
        minimizedRefreshButton.setOnClickListener(v -> {
             forceReloadActiveSession("minimizedRefreshButton");
        });
        }

        if (minimizedNewTabButton != null) {
        minimizedNewTabButton.setOnClickListener(v -> {
            Log.d(TAG, "Minimized New Tab button clicked.");
                if (!isTvDevice()) {
            showExpandedBar(); // Ensure expanded bar is shown
                }
              createNewTab(true); // Create and switch to the new tab
        });
        }

        if (minimizedUrlBar != null) {
        minimizedUrlBar.setOnClickListener(v -> showExpandedBar());
        }

        if (isTvDevice()) {
            // Create a single, reusable hover listener for our buttons.
            View.OnHoverListener buttonHoverListener = (v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        // Make the button slightly transparent to indicate hover
                        v.setAlpha(0.7f);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        // Restore original appearance
                        v.setAlpha(1.0f);
                        break;
                }
                return false; // Allow other listeners to process the event
            };

            // Apply the hover listener to all main control bar buttons
            if (backButton != null) backButton.setOnHoverListener(buttonHoverListener);
            if (forwardButton != null) forwardButton.setOnHoverListener(buttonHoverListener);
            if (refreshButton != null) refreshButton.setOnHoverListener(buttonHoverListener);
            if (settingsButton != null) settingsButton.setOnHoverListener(buttonHoverListener);
            if (downloadsButton != null) downloadsButton.setOnHoverListener(buttonHoverListener);
            if (newTabButton != null) newTabButton.setOnHoverListener(buttonHoverListener);
            if (tabsButton != null) tabsButton.setOnHoverListener(buttonHoverListener);
            // Also apply to minimized bar buttons
            if (minimizedBackButton != null) minimizedBackButton.setOnHoverListener(buttonHoverListener);
            if (minimizedForwardButton != null) minimizedForwardButton.setOnHoverListener(buttonHoverListener);
            if (minimizedRefreshButton != null) minimizedRefreshButton.setOnHoverListener(buttonHoverListener);
            if (minimizedNewTabButton != null) minimizedNewTabButton.setOnHoverListener(buttonHoverListener);
        }
    }

      // Refactored method to set up URL bar listener
    private void setupUrlBarListener() {
        // Common action listener for both platforms
        urlBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT ||
                (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                
                String rawInput = urlBar.getText().toString();
                String input = rawInput.trim(); 
                Log.d(TAG, "Input received: '" + input + "'"); 

                if (input.isEmpty()) {
                    // On TV, if input is empty when submitting, restore the last valid URL
                    if (isTvDevice()) {
                        urlBar.setText(mLastValidUrl);
                    }
                    return true; // Consume the action regardless
                }

                // Intercept Magnet Links
                if (input.startsWith("magnet:")) {
                    showTorrentDecisionDialog(input);
                    hideKeyboard();
                    urlBar.clearFocus();
                    return true;
                }

                String urlToLoad = processUrlInput(input);
                GeckoSession activeSession = getActiveSession();

                if (urlToLoad != null && activeSession != null) {
                    activeSession.loadUri(urlToLoad);
                    mLastValidUrl = urlToLoad; // Update last valid URL
                    Log.d(TAG, "Called loadUri with: " + urlToLoad);
                    hideKeyboard();
                    urlBar.clearFocus(); // Clear focus after submitting
                } else if (activeSession == null) {
                     Log.e(TAG, "activeSession is null, cannot load URI.");
                } else {
                    Log.e(TAG, "urlToLoad is null (likely encoding error), not loading.");
                }
                return true;
            }
            return false;
        });

        if (isTvDevice()) {
            // TV-specific focus logic
            urlBar.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // When focus is gained on TV, store current URL and clear the text
                    mLastValidUrl = urlBar.getText().toString();
                    urlBar.setText("");
                    showKeyboard(urlBar);
                } else {
                    // When focus is lost on TV, check if we need to restore the URL
                    String currentText = urlBar.getText().toString().trim();
                    if (currentText.isEmpty() && mLastValidUrl != null && !mLastValidUrl.isEmpty()) {
                        // If text is empty, restore the last valid URL
                        urlBar.setText(mLastValidUrl);
                    }
                }
            });
        } else {
            // Mobile-specific logic for robust text selection
            urlBar.setOnClickListener(v -> {
                urlBar.selectAll();
                Log.d(TAG, "urlBar OnClickListener: selected all text.");
            });
    
            urlBar.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // A small delay helps ensure the selection happens after the UI has settled
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                         urlBar.selectAll();
                        Log.d(TAG, "urlBar OnFocusChangeListener: selected all text after delay.");
                    }, 100); // 100ms delay
                     showKeyboard(urlBar);
                }
            });
        }
    }

    // Helper method to process URL input (search or direct URL)
    private String processUrlInput(String input) {
         // Respect special schemes immediately
         if (input.startsWith("magnet:") || input.startsWith("about:") || input.startsWith("file:") || input.startsWith("intent:")) {
             return input;
         }

         String urlToLoad;
         boolean isUrl = Patterns.WEB_URL.matcher(input).matches() || 
                         Patterns.IP_ADDRESS.matcher(input).matches() ||
                         (input.contains(".") && !input.contains(" "));

         if (isUrl) {
             if (!input.startsWith("http://") && !input.startsWith("https://")) {
                 urlToLoad = "https://" + input;
             } else {
                 urlToLoad = input;
             }
         } else {
             try {
                 String encodedQuery = URLEncoder.encode(input, "UTF-8");
                 urlToLoad = buildSearchUrl(encodedQuery);
             } catch (UnsupportedEncodingException e) {
                 Log.e(TAG, "Failed to encode search query", e);
                 urlToLoad = null;
             }
         }
         return urlToLoad;
    }

    // Locale helpers and search/homepage builders
    private boolean isRussianLocale() {
        try {
            String lang = java.util.Locale.getDefault().getLanguage();
            return lang != null && lang.toLowerCase(java.util.Locale.ROOT).startsWith("ru");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCzechLocale() {
        try {
            String lang = java.util.Locale.getDefault().getLanguage();
            return lang != null && lang.toLowerCase(java.util.Locale.ROOT).startsWith("cs");
        } catch (Exception e) {
            return false;
        }
    }

    private String getDefaultHomepageUrl() {
        if (isRussianLocale()) {
            // On TV devices, use yandex.com per requirement; otherwise use dzen.ru
            return isTvDevice() ? "https://yandex.com" : "https://dzen.ru";
        }
        if (isCzechLocale()) {
            // For Czech devices, use yandex.com
            return "https://yandex.com";
        }
        return "https://www.google.com";
    }

    private String buildSearchUrl(String encodedQuery) {
        if (isRussianLocale()) {
            // TV devices: yandex.com; Non-TV: yandex.ru with Dzen source marker
            if (isTvDevice()) {
                return "https://yandex.com/search/?text=" + encodedQuery;
            } else {
                return "https://yandex.ru/search/?text=" + encodedQuery + "&search_source=dzen_desktop_safe";
            }
        }
        if (isCzechLocale()) {
            // For Czech devices, use yandex.com
            return "https://yandex.com/search/?text=" + encodedQuery;
        }
        return "https://www.google.com/search?q=" + encodedQuery;
    }
    
    // Helper to hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // Optionally clear focus from URL bar if it had it, 
            // but avoid clearing focus generally as it might affect web content immediately
            // if (view == urlBar) { 
            //    urlBar.clearFocus();
            // }
            Log.d(TAG, "hideKeyboard() called.");
        } else {
            Log.w(TAG, "InputMethodManager is null, cannot hide keyboard.");
        }
    }

    // Method to create a new tab (GeckoSession) and optionally switch to it
    private void createNewTab(@Nullable String initialUrl, boolean switchToTab) {
        if (runtime == null) {
            Log.e(TAG, "Runtime not initialized, cannot create new tab.");
            Toast.makeText(this, "Error initializing browser engine.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Creating new tab. Initial URL: " + initialUrl + ", Switch: " + switchToTab);
        GeckoSession newSession = new GeckoSession();
        
        // Apply user agent settings before opening
        applyUserAgentToSession(newSession, initialUrl);
        
        newSession.open(runtime);
        Log.d(TAG, "Opened new GeckoSession");
        
        newSession.setMediaSessionDelegate(this); // Set MediaSession delegate

        // Handle Android and content permissions (mic, location, etc.)
        newSession.setPermissionDelegate(new GeckoSession.PermissionDelegate() {
            @Override
            public void onAndroidPermissionsRequest(@NonNull GeckoSession session,
                                                    @NonNull String[] permissions,
                                                    @NonNull Callback callback) {
                Log.d(TAG, "[Perm] onAndroidPermissionsRequest perms=" + java.util.Arrays.toString(permissions));

                // If everything is already granted, shortcut.
                boolean allGranted = true;
                for (String p : permissions) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, p) != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    Log.d(TAG, "[Perm] All Android permissions already granted; calling callback.grant()");
                    callback.grant();
                    return;
                }

                // Otherwise request them from Android and remember the callback.
                pendingAndroidPermCallback = callback;
                pendingAndroidPerms = permissions;
                ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_GV_ANDROID_PERMS);
            }

            @Override
            @NonNull
            public GeckoResult<Integer> onContentPermissionRequest(@NonNull GeckoSession session,
                                                                   @NonNull ContentPermission perm) {
                Log.d(TAG, "[Perm] onContentPermissionRequest permission=" + perm.permission + ", uri=" + perm.uri);

                final GeckoResult<Integer> result = new GeckoResult<>();

                // Handle less sensitive, noisy permissions without prompting the user.
                // We currently auto-allow these to avoid UX spam while not blocking sites.
                if (perm.permission == PermissionDelegate.PERMISSION_AUTOPLAY_INAUDIBLE ||
                        perm.permission == PermissionDelegate.PERMISSION_AUTOPLAY_AUDIBLE ||
                        perm.permission == PermissionDelegate.PERMISSION_TRACKING ||
                        perm.permission == PermissionDelegate.PERMISSION_XR ||
                        perm.permission == PermissionDelegate.PERMISSION_MEDIA_KEY_SYSTEM_ACCESS ||
                        perm.permission == PermissionDelegate.PERMISSION_STORAGE_ACCESS ||
                        perm.permission == PermissionDelegate.PERMISSION_LOCAL_DEVICE_ACCESS ||
                        perm.permission == PermissionDelegate.PERMISSION_LOCAL_NETWORK_ACCESS) {
                    Log.d(TAG, "[Perm] Auto-allowing non-sensitive content permission " + perm.permission + " for uri=" + perm.uri);
                    result.complete(ContentPermission.VALUE_ALLOW);
                    return result;
                }

                // If we have a saved decision for this origin+type, reuse it.
                final String origin = perm.uri;
                String host;
                try {
                    java.net.URI u = origin != null ? new java.net.URI(origin) : null;
                    host = (u != null && u.getHost() != null) ? u.getHost() : origin;
                } catch (Exception e) {
                    Log.w(TAG, "[Perm] Failed to parse origin URI: " + origin, e);
                    host = origin;
                }
                final int type = perm.permission;
                final String hostForUi = host; // must be effectively final inside lambda
                final String key = hostForUi + "|" + type;

                Integer saved = savedContentPermissions.get(key);
                if (saved != null && (saved == ContentPermission.VALUE_ALLOW || saved == ContentPermission.VALUE_DENY)) {
                    Log.d(TAG, "[Perm] Using saved decision for " + key + " -> " + saved);
                    result.complete(saved);
                    return result;
                }

                // Otherwise, prompt the user.
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        Log.w(TAG, "[Perm] Activity finishing/destroyed; defaulting to DENY for " + key);
                        result.complete(ContentPermission.VALUE_DENY);
                        return;
                    }

                    // This GeckoView version does not expose symbolic PERMISSION_* constants,
                    // so we fall back to a generic label that still surfaces the numeric code.
                    String permLabel = "special access (code " + type + ")";

                    String displayHost = (hostForUi == null || hostForUi.isEmpty()) ? "this site" : hostForUi;
                    String message = "Allow " + displayHost + " to access your " + permLabel + "?";

                    final android.widget.CheckBox rememberCheck = new android.widget.CheckBox(MainActivity.this);
                    rememberCheck.setText("Remember my choice for this site");

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Site permission request")
                            .setMessage(message)
                            .setView(rememberCheck)
                            .setPositiveButton("Allow", (dialog, which) -> {
                                if (rememberCheck.isChecked()) {
                                    savedContentPermissions.put(key, ContentPermission.VALUE_ALLOW);
                                    saveSavedContentPermissions();
                                }
                                result.complete(ContentPermission.VALUE_ALLOW);
                            })
                            .setNegativeButton("Deny", (dialog, which) -> {
                                if (rememberCheck.isChecked()) {
                                    savedContentPermissions.put(key, ContentPermission.VALUE_DENY);
                                    saveSavedContentPermissions();
                                }
                                result.complete(ContentPermission.VALUE_DENY);
                            })
                            .setOnCancelListener(dialog -> {
                                // Treat cancel as deny without remembering.
                                result.complete(ContentPermission.VALUE_DENY);
                            })
                            .show();
                });

                return result;
            }

            public void onMediaPermissionRequest(@NonNull GeckoSession session,
                                                 @Nullable String uri,
                                                 @Nullable MediaSource video,
                                                 @Nullable MediaSource audio,
                                                 @NonNull MediaCallback callback) {
                Log.d(TAG, "[Perm] onMediaPermissionRequest uri=" + uri + ", video=" + (video != null) + ", audio=" + (audio != null));

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        Log.w(TAG, "[Perm] Activity finishing/destroyed; rejecting media permission");
                        callback.reject();
                        return;
                    }

                    String host = uri;
                    try {
                        java.net.URI u = uri != null ? new java.net.URI(uri) : null;
                        if (u != null && u.getHost() != null) {
                            host = u.getHost();
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "[Perm] Failed to parse media permission URI: " + uri, e);
                    }

                    String deviceLabel;
                    if (video != null && audio != null) {
                        deviceLabel = "camera and microphone";
                    } else if (video != null) {
                        deviceLabel = "camera";
                    } else if (audio != null) {
                        deviceLabel = "microphone";
                    } else {
                        deviceLabel = "media devices";
                    }

                    String displayHost = (host == null || host.isEmpty()) ? "this site" : host;
                    String message = "Allow " + displayHost + " to access your " + deviceLabel + "?";

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Site wants to use media devices")
                            .setMessage(message)
                            .setPositiveButton("Allow", (dialog, which) -> callback.grant(video, audio))
                            .setNegativeButton("Deny", (dialog, which) -> callback.reject())
                            .setOnCancelListener(dialog -> callback.reject())
                            .show();
                });
            }
        });
        
        // Add delegates AFTER opening
        newSession.setProgressDelegate(new ProgressDelegate() {
            @Override
            public void onPageStart(@NonNull GeckoSession session, @NonNull String url) {
                Log.d(TAG, "ProgressDelegate: onPageStart called for session: " + session.hashCode() + ", URL: " + url);
                if (!isTvDevice()) {
                    // Attach translation callbacks only for the ACTIVE session to prevent
                    // background/restored tabs from driving the pill state.
                    if (session == getActiveSession()) {
                        ensureSessionTranslation(session);
                    }
                    // Reset translated state on navigation start
                    isPageTranslated = false;
                    // Clear detected language to avoid stale state and reset pill UI
                    currentDetectedFromLang = null;
                    // Hide pill until detection determines mismatch with system language and reset labels
                    runOnUiThread(() -> {
                        showTranslatePill(false);
                        if (translateFromView != null) {
                            translateFromView.setText("From: " + langCodeToName("auto"));
                        }
                        if (translateToView != null) {
                            String toInit = (translateToLangPref != null) ? translateToLangPref : getSystemLangTag();
                            translateToView.setText("To: " + langCodeToName(baseLang(toInit)));
                        }
                    });
                }
                // Fade out snapshot overlay on first real navigation of the ACTIVE tab
                if (session == getActiveSession()) {
                    if (snapshotOverlay != null && snapshotOverlay.getVisibility() == View.VISIBLE) {
                        boolean isAboutBlank = (url != null && url.startsWith("about:blank"));
                        if (!isAboutBlank) {
                            snapshotOverlay.animate().alpha(0f).setDuration(250).withEndAction(() -> {
                                snapshotOverlay.setVisibility(View.GONE);
                                snapshotOverlay.setImageDrawable(null);
                                snapshotOverlay.setAlpha(1f);
                                Log.d(TAG, "[SnapshotOverlay] Faded out and hidden on first real page start");
                            }).start();
                        }
                    }
                }
                if (session == getActiveSession()) {
                    markResumeProgressIfWithinWindow(session);
                    Log.d(TAG, "ProgressDelegate: onPageStart on ACTIVE session. Showing progress.");
                    progressBar.setVisibility(View.VISIBLE);
                    if (isTvDevice() && tvCursorView != null) {
                        tvCursorView.showProgress();
                    }
                    // Ensure a control bar is visible on navigation start
                    ensureControlBarVisibleOnNavigation();

                    if (isTvDevice()) {
                        try {
                            String saved = sessionUrlMap.get(session);
                            boolean urlIsBlank = (url == null || url.toLowerCase().startsWith("about:blank"));
                            if (urlIsBlank && saved != null && isHttpLike(saved) && !tvBlankBounceRecoveryAttempted) {
                                tvBlankBounceRecoveryAttempted = true;
                                final String target = saved;
                                mainHandler.postDelayed(() -> {
                                    try {
                                        GeckoSession active = getActiveSession();
                                        if (active != session || geckoView == null || geckoView.getSession() != session) return;
                                        Log.w(TAG, "[TVRestoreFix] Detected about:blank bounce; forcing load of saved URL: " + target);
                                        mLastValidUrl = target;
                                        sessionUrlMap.put(session, target);
                                        session.loadUri(target);
                                        updateUIForActiveSession();
                                    } catch (Throwable t) {
                                        Log.w(TAG, "[TVRestoreFix] about:blank bounce recovery failed", t);
                                    }
                                }, 600);
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }

            @Override
            public void onProgressChange(GeckoSession session, int progress) {
                // Log only for the active session to avoid spam
                if (session == getActiveSession()) {
                    markResumeProgressIfWithinWindow(session);
                    Log.d(TAG, "ProgressDelegate: onProgressChange on ACTIVE session: " + progress);
                    progressBar.setProgress(progress);
                    if (isTvDevice() && tvCursorView != null) {
                        tvCursorView.setProgress(progress);
                    }
                }
            }

            @Override
            public void onPageStop(GeckoSession session, boolean success) {
                Log.d(TAG, "ProgressDelegate: onPageStop called for session: " + session.hashCode() + ", Success: " + success);
                if (session == getActiveSession()) {
                    Log.d(TAG, "ProgressDelegate: onPageStop on ACTIVE session. Hiding progress.");
                    progressBar.setVisibility(View.GONE);
                    if (isTvDevice() && tvCursorView != null) {
                        tvCursorView.hideProgress();
                    }
                    // Update snapshot preview for tab switcher
                    if (success && geckoView.getSession() == session) {
                        Log.d(TAG, "MainTab ProgressDelegate: onPageStop for active session. Capturing snapshot for previews.");
                        captureSnapshot(session);
                        // Do not alter translate pill visibility here; detection callback will decide based on language mismatch
                    }
                }
            }
        });

        // This is the NavigationDelegate for the tab created by createNewTab()
        newSession.setNavigationDelegate(new NavigationDelegate() {
            @Override
             public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                String uriString = request.uri;
                Log.d(TAG, "MainTab NavDelegate onLoadRequest: URI="+uriString + " (Session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + ")");

                if (uriString != null && uriString.startsWith("magnet:")) {
                    Log.i(TAG, "MainTab NavDelegate: Intercepted magnet URI: " + uriString);
                    runOnUiThread(() -> showTorrentDecisionDialog(uriString));
                    return GeckoResult.fromValue(AllowOrDeny.DENY);
                }

                if (uriString != null && uriString.startsWith("intent://")) {
                    Log.i(TAG, "MainTab NavDelegate: Intercepted intent:// URI: " + uriString);
                    runOnUiThread(() -> {
                        try {
                            Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.d(TAG, "MainTab NavDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                            // For security, ensure that the intent has a component before starting it.
                            // This is a best practice for safer intents on Android 15.
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                Log.i(TAG, "MainTab NavDelegate: Activity found for intent. Attempting to start...");
                                // Ensure the intent is not targeting a non-exported component of another app.
                                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                startActivity(intent);
                                Log.i(TAG, "MainTab NavDelegate: startActivity called for main intent.");
                            } else {
                                Log.w(TAG, "MainTab NavDelegate: No activity found for main intent. Checking fallback.");
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                    Log.i(TAG, "MainTab NavDelegate: Fallback URL found: " + fallbackUrl);
                                    if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                        Log.d(TAG, "MainTab NavDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                        try {
                                            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            fallbackIntent.setPackage("com.android.vending");
                                            startActivity(fallbackIntent);
                                            Log.i(TAG, "MainTab NavDelegate: startActivity called for Play Store fallback.");
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            Log.w(TAG, "MainTab NavDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                            loadUriInGeckoView(session, fallbackUrl); // Fallback to loading in current session if Play Store app fails
                                        } catch (Exception ex) {
                                            Log.e(TAG, "MainTab NavDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                            loadUriInGeckoView(session, fallbackUrl);
                                        }
                                    } else {
                                        Log.i(TAG, "MainTab NavDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                        loadUriInGeckoView(session, fallbackUrl); // Fallback to loading in current session
                                    }
                                } else {
                                    Log.w(TAG, "MainTab NavDelegate: No activity found for intent and no browser_fallback_url.");
                                    Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (URISyntaxException e) {
                            Log.e(TAG, "MainTab NavDelegate: Invalid intent URI syntax: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "MainTab NavDelegate: General error handling intent URI: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return GeckoResult.fromValue(AllowOrDeny.DENY); // DENY further loading by GeckoView
                }
                // Allow other navigation requests for the main tab itself
                Log.d(TAG, "MainTab NavDelegate onLoadRequest: Allowing non-intent URI: " + uriString);
                return GeckoResult.fromValue(AllowOrDeny.ALLOW);
            }

            @Override
            public GeckoResult<String> onLoadError(GeckoSession session, String uri, WebRequestError error) {
                Log.e(TAG, "onLoadError: Uri=" + uri + ", Error=" + error.category + ": " + error.getMessage()); // Changed error.description to error.getMessage()
                if (session == getActiveSession()) { // Only show if it's the active tab
                    Toast.makeText(MainActivity.this, "Error: Could not load page. It might be blocked or unavailable.", Toast.LENGTH_LONG).show();
                    // Optionally, load a custom error page or clear the view
                    // For example, to load a blank page:
                    // session.loadUri("about:blank"); 
                    // Or a custom HTML error page from assets:
                    // session.loadUri("file:///android_asset/error_page.html");
                }
                // Ensure progress bar is hidden on error
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                return null; // Return null as per GeckoResult<String>
            }

            @Override
            public void onLocationChange(GeckoSession session, @Nullable String newUri, @NonNull List<GeckoSession.PermissionDelegate.ContentPermission> perms, @NonNull Boolean hasUserGesture) {
                Log.d(TAG, "NavDelegate: onLocationChange for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + " to URI: " + newUri +
                           " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                if (!isTvDevice()) {
                    // Track URL
                    if (newUri != null) {
                        sessionUrlMap.put(session, newUri);
                    }
                    // If previously translated, attempt to re-translate after in-page navs
                    if (isPageTranslated) {
                        String from = currentDetectedFromLang != null ? currentDetectedFromLang : "auto";
                        String to = (translateToLangPref != null) ? translateToLangPref : getSystemLangTag();
                        Toast.makeText(MainActivity.this, "Translating…", Toast.LENGTH_SHORT).show();
                        performTranslate(from, to);
                    }
                }

                if (newUri == null) {
                    return;
                }

                String lowerUri = newUri.toLowerCase();
                if (lowerUri.startsWith("about:blank")) {
                    Log.d(TAG, "NavDelegate: Ignoring about:blank top-level location change");
                    return;
                }

                // Track gestures for scroll focus heuristics
                if (Boolean.TRUE.equals(hasUserGesture)) {
                    synchronized (sessionLastGestureMs) {
                        sessionLastGestureMs.put(session, System.currentTimeMillis());
                    }
                }

                // Record latest URL per session
                sessionUrlMap.put(session, newUri);

                // Mark resume progress to avoid unnecessary resume recovery
                markResumeProgressIfWithinWindow(session);

                if (session == getActiveSession()) {
                    // CRITICAL: Update mLastValidUrl with the latest URL from the navigation event.
                    // This ensures that if the user focuses the URL bar on TV, it shows the correct, current URL.
                    mLastValidUrl = newUri;

                    runOnUiThread(() -> {
                        ensureControlBarVisibleOnNavigation();
                        MainActivity.this.updateUIForActiveSession();

                        if (isTvDevice()) {
                            if (geckoView != null) {
                                mCanScrollUp = geckoView.canScrollVertically(-1);
                            }
                            updateFocusableRects();
                            Log.d(TAG, "[TV][NAV] onLocationChange url=" + newUri + " mCanScrollUp=" + mCanScrollUp + " focusableRects=" + (focusableRects != null ? focusableRects.size() : -1));

                            if (tvElementScrollEnabled) {
                                queryTvDpr(session);
                            }
                        }
                    });

                    ensureTvScrollExtension(session);
                }

                saveSessionState();
            }

// This onNewSession is called when the main tab (newSession) tries to open a popup/new window
@Override
public GeckoResult<GeckoSession> onNewSession(GeckoSession originatingSession, String uri) {
Log.d(TAG, "createNewTab NavDelegate: onNewSession called for URI: " + uri + " from session: " + (sessionUrlMap.containsKey(originatingSession) ? sessionUrlMap.get(originatingSession) : "N/A"));

// 1. Create the GeckoSession object for the new tab/popup.
//    DO NOT CALL .open(runtime) on it here. GeckoView will do that.
final GeckoSession newTabSession = new GeckoSession();
// Depth: parent depth + 1 (default 0 if not found)
int parentDepth = 0;
synchronized (sessionPopupDepth) {
Integer d = sessionPopupDepth.get(originatingSession);
if (d != null) parentDepth = d;
}
final int newDepth = parentDepth + 1;
synchronized (sessionPopupDepth) { sessionPopupDepth.put(newTabSession, newDepth); }

// Enforce per-session rate limit
long now = System.currentTimeMillis();
boolean rateOk = true;
synchronized (sessionLastPopupMs) {
    Long last = sessionLastPopupMs.get(originatingSession);
    if (last != null && (now - last) < POPUP_RATE_LIMIT_MS) {
        rateOk = false;
    } else {
        sessionLastPopupMs.put(originatingSession, now);
    }
}
if (!rateOk) {
    Log.w(TAG, "Popup creation rate-limited for session: " + (sessionUrlMap.get(originatingSession)));
    return GeckoResult.fromValue(null);
}
        
// Apply user agent settings to the popup session
applyUserAgentToSession(newTabSession, uri);
        
newTabSession.setMediaSessionDelegate(MainActivity.this); // Use MainActivity.this

                // 2. Configure its delegates:
                newTabSession.setProgressDelegate(new ProgressDelegate() {
                    @Override
                    public void onPageStart(@NonNull GeckoSession session, @NonNull String url) {
                        if (session == getActiveSession()) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (isTvDevice() && tvCursorView != null) {
                                tvCursorView.showProgress();
                            }
                            // Ensure a control bar is visible on navigation start
                            ensureControlBarVisibleOnNavigation();
                        }
                    }

                    @Override
                    public void onProgressChange(GeckoSession session, int progress) {
                        if (session == getActiveSession()) {
                            progressBar.setProgress(progress);
                            if (isTvDevice() && tvCursorView != null) {
                                tvCursorView.setProgress(progress);
                            }
                        }
                        Log.v(TAG, "NewTab/Popup Progress: " + progress + "% for " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "Unknown URI"));
                    }

                    @Override
                    public void onPageStop(GeckoSession session, boolean success) {
                        if (session == getActiveSession()) {
                            progressBar.setVisibility(View.GONE);
                            if (isTvDevice() && tvCursorView != null) {
                                tvCursorView.hideProgress();
                            }

                            if (success && geckoView.getSession() == session) {
                                Log.d(TAG, "Popup ProgressDelegate: onPageStop for active session. Capturing snapshot.");
                                captureSnapshot(session);
                            }
                        }
                    }
                });

                newTabSession.setNavigationDelegate(new NavigationDelegate() {
                    @Override
                    public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                        String uriString = request.uri;
                        Log.d(TAG, "Popup NavDelegate onLoadRequest: URI="+uriString + " (Session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + ")");

                        if (uriString != null && uriString.toLowerCase().startsWith("magnet:")) {
                            Log.i(TAG, "Popup NavDelegate: Intercepted magnet URI: " + uriString);
                            runOnUiThread(() -> showTorrentDecisionDialog(uriString));
                            return GeckoResult.fromValue(AllowOrDeny.DENY);
                        }

                        if (uriString != null && uriString.startsWith("intent://")) {
                            Log.i(TAG, "Popup NavDelegate: Intercepted intent:// URI: " + uriString);
                            runOnUiThread(() -> {
                                try {
                                    Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Log.d(TAG, "Popup NavDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                        Log.i(TAG, "Popup NavDelegate: Activity found for intent. Attempting to start...");
                                        startActivity(intent);
                                        Log.i(TAG, "Popup NavDelegate: startActivity called for main intent.");
                                    } else {
                                        Log.w(TAG, "Popup NavDelegate: No activity found for main intent. Checking fallback.");
                                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                        if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                            Log.i(TAG, "Popup NavDelegate: Fallback URL found: " + fallbackUrl);
                                            if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                                Log.d(TAG, "Popup NavDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                                try {
                                                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    fallbackIntent.setPackage("com.android.vending");
                                                    startActivity(fallbackIntent);
                                                    Log.i(TAG, "Popup NavDelegate: startActivity called for Play Store fallback.");
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    Log.w(TAG, "Popup NavDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                                    loadUriInGeckoView(session, fallbackUrl);
                                                } catch (Exception ex) {
                                                    Log.e(TAG, "Popup NavDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                                    loadUriInGeckoView(session, fallbackUrl);
                                                }
                                            } else {
                                                Log.i(TAG, "Popup NavDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                                loadUriInGeckoView(session, fallbackUrl);
                                            }
                                        } else {
                                            Log.w(TAG, "Popup NavDelegate: No activity found for intent and no browser_fallback_url.");
                                            Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (URISyntaxException e) {
                                    Log.e(TAG, "Popup NavDelegate: Invalid intent URI syntax: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "Popup NavDelegate: General error handling intent URI: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return GeckoResult.fromValue(AllowOrDeny.DENY);
                        }
                        Log.d(TAG, "Popup NavDelegate onLoadRequest: Allowing non-intent URI: " + uriString);
                        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                    }

                    @Override
                    public void onLocationChange(GeckoSession session, @Nullable String newUri, @NonNull List<GeckoSession.PermissionDelegate.ContentPermission> perms, @NonNull Boolean hasUserGesture) {
                        Log.d(TAG, "Popup NavDelegate: onLocationChange for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + " to URI: " + newUri +
                                   " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                        if (newUri != null) {
                            String lu = newUri.toLowerCase();
                            if (lu.startsWith("about:blank")) {
                                Log.d(TAG, "Popup NavDelegate: Ignoring about:blank top-level location change");
                                return;
                            }
                            markResumeProgressIfWithinWindow(session);
                            sessionUrlMap.put(session, newUri);
                            mLastValidUrl = newUri; // Update last valid URL
                            // Promote ephemeral session if it navigated to a meaningful page
                            if (ephemeralSessions.contains(session) && isHttpLike(newUri) && !isBlankOrNullUri(newUri)) {
                                Log.i(TAG, "Promoting EPHEMERAL popup to real tab: " + newUri);
                                ephemeralSessions.remove(session);
                                cancelEphemeralTimeout(session);
                                if (!geckoSessionList.contains(session)) {
                                    synchronized (geckoSessionList) { geckoSessionList.add(session); }
                                }
                                final int idx = geckoSessionList.indexOf(session);
                                if (idx != -1) {
                                    runOnUiThread(() -> switchToTab(idx));
                                }
                            }
                            if (session == getActiveSession()) {
                        runOnUiThread(() -> {
                                    // Ensure control bar visible when location changes on active popup session
                                    ensureControlBarVisibleOnNavigation();
                                    urlBar.setText(newUri);
                                    if (!isControlBarExpanded) {
                                        minimizedUrlBar.setText(newUri);
                                    }
                                });
                            }
                            saveSessionState(); // TEST: Re-enabled
                        }
                    }

                    @Override
                    public GeckoResult<GeckoSession> onNewSession(GeckoSession currentPopupSession, String newUriFromPopup) {
                        Log.i(TAG, "Grandchild Popup NavDelegate: onNewSession for URI: " + newUriFromPopup + " from popup session: " + (sessionUrlMap.containsKey(currentPopupSession) ? sessionUrlMap.get(currentPopupSession) : "N/A"));
                        // Compute next depth (no cap, track for observability/UX)
                        int parentDepth2 = 0;
                        synchronized (sessionPopupDepth) {
                            Integer d2 = sessionPopupDepth.get(currentPopupSession);
                            if (d2 != null) parentDepth2 = d2;
                        }
                        final int nextDepth = parentDepth2 + 1;
                        final GeckoSession grandChildSession = new GeckoSession();
                        synchronized (sessionPopupDepth) { sessionPopupDepth.put(grandChildSession, nextDepth); }

                        // Rate limit and gesture check for nested popups
                        long now2 = System.currentTimeMillis();
                        boolean rateOk2 = true;
                        synchronized (sessionLastPopupMs) {
                            Long last2 = sessionLastPopupMs.get(currentPopupSession);
                            if (last2 != null && (now2 - last2) < POPUP_RATE_LIMIT_MS) {
                                rateOk2 = false;
                            } else {
                                sessionLastPopupMs.put(currentPopupSession, now2);
                            }
                        }
                        if (!rateOk2) {
                            Log.w(TAG, "Nested popup creation rate-limited for session: " + (sessionUrlMap.get(currentPopupSession)));
                            return GeckoResult.fromValue(null);
                        }
                        
                        // Apply user agent settings to the grandchild popup session
                        applyUserAgentToSession(grandChildSession, newUriFromPopup);

                        grandChildSession.setMediaSessionDelegate(MainActivity.this); // Use MainActivity.this

                        grandChildSession.setProgressDelegate(new ProgressDelegate() {
                            @Override
                            public void onPageStart(@NonNull GeckoSession session, @NonNull String url) {
                                if (session == getActiveSession()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    if (isTvDevice() && tvCursorView != null) {
                                        tvCursorView.showProgress();
                                    }
                                }
                            }

                            @Override
                            public void onProgressChange(GeckoSession session, int progress) {
                                if (session == getActiveSession()) {
                                    progressBar.setProgress(progress);
                                    if (isTvDevice() && tvCursorView != null) {
                                        tvCursorView.setProgress(progress);
                                    }
                                }
                                Log.v(TAG, "GrandChildPopup Progress: " + progress + "% for " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "Unknown URI"));
                            }

                            @Override
                            public void onPageStop(GeckoSession session, boolean success) {
                                if (session == getActiveSession()) {
                                    progressBar.setVisibility(View.GONE);
                                    if (isTvDevice() && tvCursorView != null) {
                                        tvCursorView.hideProgress();
                                    }

                                    if (success && geckoView.getSession() == session) {
                                        Log.d(TAG, "GrandChild ProgressDelegate: onPageStop for active session. Capturing snapshot.");
                                        captureSnapshot(session);
                                    }
                                }
                            }
                        });

                        grandChildSession.setNavigationDelegate(new NavigationDelegate() {
                    @Override
                            public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                                String uriString = request.uri;
                                Log.d(TAG, "GrandChild NavDelegate onLoadRequest: URI="+uriString + " (Session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + ")");

                                if (uriString != null && uriString.startsWith("intent://")) {
                                    Log.i(TAG, "GrandChild NavDelegate: Intercepted intent:// URI: " + uriString);
                                    runOnUiThread(() -> {
                                        try {
                                            Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Log.d(TAG, "GrandChild NavDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                Log.i(TAG, "GrandChild NavDelegate: Activity found for intent. Attempting to start...");
                                                startActivity(intent);
                                                Log.i(TAG, "GrandChild NavDelegate: startActivity called for main intent.");
                                            } else {
                                                Log.w(TAG, "GrandChild NavDelegate: No activity found for main intent. Checking fallback.");
                                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                                    Log.i(TAG, "GrandChild NavDelegate: Fallback URL found: " + fallbackUrl);
                                                    if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                                        Log.d(TAG, "GrandChild NavDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                                        try {
                                                            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                                            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            fallbackIntent.setPackage("com.android.vending");
                                                            startActivity(fallbackIntent);
                                                            Log.i(TAG, "GrandChild NavDelegate: startActivity called for Play Store fallback.");
                                                        } catch (android.content.ActivityNotFoundException anfe) {
                                                            Log.w(TAG, "GrandChild NavDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                                            loadUriInGeckoView(session, fallbackUrl);
                                                        } catch (Exception ex) {
                                                            Log.e(TAG, "GrandChild NavDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                                            loadUriInGeckoView(session, fallbackUrl);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "GrandChild NavDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                                        loadUriInGeckoView(session, fallbackUrl);
                                                    }
                                                } else {
                                                    Log.w(TAG, "GrandChild NavDelegate: No activity found for intent and no browser_fallback_url.");
                                                    Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } catch (URISyntaxException e) {
                                            Log.e(TAG, "GrandChild NavDelegate: Invalid intent URI syntax: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e(TAG, "GrandChild NavDelegate: General error handling intent URI: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return GeckoResult.fromValue(AllowOrDeny.DENY);
                                }
                                Log.d(TAG, "GrandChild NavDelegate onLoadRequest: Allowing non-intent URI: " + uriString);
                                return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                    }

                    @Override
                            public void onLocationChange(GeckoSession session, @Nullable String newUri, @NonNull List<GeckoSession.PermissionDelegate.ContentPermission> perms, @NonNull Boolean hasUserGesture) {
                                Log.d(TAG, "GrandChild NavDelegate: onLocationChange for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A") + " to URI: " + newUri +
                                           " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                                if (newUri != null) {
                                    String lu = newUri.toLowerCase();
                                    if (lu.startsWith("about:blank")) {
                                        Log.d(TAG, "GrandChild NavDelegate: Ignoring about:blank top-level location change");
                                        return;
                                    }
                                    markResumeProgressIfWithinWindow(session);
                                    sessionUrlMap.put(session, newUri);
                                    mLastValidUrl = newUri; // Update last valid URL
                                    // Promote ephemeral grandchild if navigated to a meaningful page
                                    if (ephemeralSessions.contains(session) && isHttpLike(newUri) && !isBlankOrNullUri(newUri)) {
                                        Log.i(TAG, "Promoting EPHEMERAL grandchild popup to real tab: " + newUri);
                                        ephemeralSessions.remove(session);
                                        cancelEphemeralTimeout(session);
                                        if (!geckoSessionList.contains(session)) {
                                            synchronized (geckoSessionList) { geckoSessionList.add(session); }
                                        }
                                        final int idx = geckoSessionList.indexOf(session);
                                        if (idx != -1) {
                                            runOnUiThread(() -> switchToTab(idx));
                                        }
                                    }
                                    if (session == getActiveSession()) {
                                runOnUiThread(() -> {
                                            urlBar.setText(newUri);
                                            if (!isControlBarExpanded) {
                                                minimizedUrlBar.setText(newUri);
                                            }
                                        });
                                    }
                                    saveSessionState(); // TEST: Re-enabled
                                }
                            }
         @Override
                            public GeckoResult<GeckoSession> onNewSession(GeckoSession session, String uri) {
                                // Fallback nested onNewSession: apply tracking, gesture and rate checks
                                int parentDepth3 = 0;
                                synchronized (sessionPopupDepth) {
                                    Integer d3 = sessionPopupDepth.get(session);
                                    if (d3 != null) parentDepth3 = d3;
                                }
                                final int nextDepth2 = parentDepth3 + 1;
                                long now3 = System.currentTimeMillis();
                                boolean rateOk3 = true;
                                synchronized (sessionLastPopupMs) {
                                    Long last3 = sessionLastPopupMs.get(session);
                                    if (last3 != null && (now3 - last3) < POPUP_RATE_LIMIT_MS) {
                                        rateOk3 = false;
                                    } else {
                                        sessionLastPopupMs.put(session, now3);
                                    }
                                }
                                if (!rateOk3) {
                                    Log.w(TAG, "Popup creation rate-limited (fallback) for session: " + (sessionUrlMap.get(session)));
                                    return GeckoResult.fromValue(null);
                                }
                                final GeckoSession s = new GeckoSession();
                                synchronized (sessionPopupDepth) { sessionPopupDepth.put(s, nextDepth2); }
                                return GeckoResult.fromValue(s);
                            }
                        });

                        grandChildSession.setContentDelegate(new ContentDelegate() {
                    @Override
                            public void onFullScreen(GeckoSession session, boolean fullScreen) {
                                // This is for the grandchild popup, PiP logic is primarily for the main session.
                                if (session == getActiveSession()) { runOnUiThread(() -> { if (fullScreen) enterFullScreen(); else exitFullScreen(); }); }
                            }
                    @Override
                            public void onExternalResponse(GeckoSession session, org.mozilla.geckoview.WebResponse response) {
                                String uriString = response.uri;
                                Log.d(TAG, "GrandChildPopup ContentDelegate onExternalResponse: URI received: " + uriString);

                                if (uriString == null) {
                                    Log.w(TAG, "GrandChildPopup ContentDelegate onExternalResponse: Null URI, falling back to download handler.");
                                    runOnUiThread(() -> {
                                        handleDownloadResponse(response);
                                        // After download, if session ends up blank-like, close it and return focus to parent
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            String cur = sessionUrlMap.get(session);
                                            if (isBlankOrNullUri(cur)) {
                                                GeckoSession parent = popupParentMap.get(session);
                                                int indexToClose = geckoSessionList.indexOf(session);
                                                if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                                                if (parent != null) {
                                                    int pIdx = geckoSessionList.indexOf(parent);
                                                    if (pIdx != -1) { switchToTab(pIdx); }
                                                }
                                            }
                                        }, 250);
                                        // Also clear ephemeral tracking if any
                                        if (ephemeralSessions.contains(session)) {
                                            ephemeralSessions.remove(session);
                                            cancelEphemeralTimeout(session);
                                        }
                                    });
                                    return;
                                }

                                Uri parsedUri = Uri.parse(uriString);
                                String scheme = parsedUri.getScheme();

                                if ("intent".equalsIgnoreCase(scheme)) {
                                    Log.d(TAG, "GrandChildPopup ContentDelegate: Handling intent scheme for URI: " + uriString);
                                    runOnUiThread(() -> {
                                        try {
                                            Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Log.d(TAG, "GrandChildPopup ContentDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                Log.i(TAG, "GrandChildPopup ContentDelegate: Activity found for intent. Attempting to start...");
                                                startActivity(intent);
                                                Log.i(TAG, "GrandChildPopup ContentDelegate: startActivity called for main intent.");
                                            } else {
                                                Log.w(TAG, "GrandChildPopup ContentDelegate: No activity found for main intent. Checking fallback.");
                                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                                    Log.i(TAG, "GrandChildPopup ContentDelegate: Fallback URL found: " + fallbackUrl);
                                                    if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                                        Log.d(TAG, "GrandChildPopup ContentDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                                        try {
                                                            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                                            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            fallbackIntent.setPackage("com.android.vending");
                                                            startActivity(fallbackIntent);
                                                            Log.i(TAG, "GrandChildPopup ContentDelegate: startActivity called for Play Store fallback.");
                                                        } catch (android.content.ActivityNotFoundException anfe) {
                                                            Log.w(TAG, "GrandChildPopup ContentDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                                            loadUriInGeckoView(session, fallbackUrl);
                                                        } catch (Exception ex) {
                                                            Log.e(TAG, "GrandChildPopup ContentDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                                            loadUriInGeckoView(session, fallbackUrl);
                                                        }
                                                    } else {
                                                        Log.i(TAG, "GrandChildPopup ContentDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                                        loadUriInGeckoView(session, fallbackUrl);
                                                    }
                                                } else {
                                                    Log.w(TAG, "GrandChildPopup ContentDelegate: No activity found for intent and no browser_fallback_url.");
                                                    Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } catch (URISyntaxException e) {
                                            Log.e(TAG, "GrandChildPopup ContentDelegate: Invalid intent URI syntax: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e(TAG, "GrandChildPopup ContentDelegate: General error handling intent URI: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                                    // Check if it's a Play Store link that should be opened externally
                                    if (uriString.startsWith("https://play.google.com/store/") || uriString.startsWith("http://play.google.com/store/")) {
                                        try {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setPackage("com.android.vending"); // Target Play Store app
                                            Log.d(TAG, "GrandChildPopup ContentDelegate: Attempting to launch Play Store with URI: " + uriString);
                                            startActivity(intent);
                                        } catch (android.content.ActivityNotFoundException e) {
                                            Log.e(TAG, "GrandChildPopup ContentDelegate: Play Store app not found for URI: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Play Store app not found. Trying to open in browser.", Toast.LENGTH_LONG).show();
                                            loadUriInGeckoView(session, uriString); // Fallback to loading in GeckoView
                                        } catch (Exception e) {
                                            Log.e(TAG, "GrandChildPopup ContentDelegate: Error launching Play Store with URI: " + uriString, e);
                                            Toast.makeText(MainActivity.this, "Could not open link in Play Store.", Toast.LENGTH_SHORT).show();
                                            loadUriInGeckoView(session, uriString);
                                        }
                                    } else {
                                        // For other HTTP/HTTPS links, assume it's a download if it reached onExternalResponse
                                        Log.d(TAG, "GrandChildPopup ContentDelegate: HTTP/HTTPS URI is not Play Store, falling back to download handling: " + uriString);
                                        runOnUiThread(() -> {
                                            handleDownloadResponse(response);
                                            // After download, if session ends up blank-like, close it and return focus to parent
                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                String cur = sessionUrlMap.get(session);
                                                if (isBlankOrNullUri(cur)) {
                                                    GeckoSession parent = popupParentMap.get(session);
                                                    int indexToClose = geckoSessionList.indexOf(session);
                                                    if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                                                    if (parent != null) {
                                                        int pIdx = geckoSessionList.indexOf(parent);
                                                        if (pIdx != -1) { switchToTab(pIdx); }
                                                    }
                                                }
                                            }, 250);
                                            // Also clear ephemeral tracking if any
                                            if (ephemeralSessions.contains(session)) {
                                                ephemeralSessions.remove(session);
                                                cancelEphemeralTimeout(session);
                                            }
                                        });
                                    }
                                } else {
                                    // For non-HTTP/HTTPS schemes (e.g., market://, mailto:, tel:, customscheme://)
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        // Do NOT set package here, let the system resolve it
                                        Log.d(TAG, "GrandChildPopup ContentDelegate: Attempting to launch generic Intent for scheme '" + scheme + "' with URI: " + uriString);
                                        startActivity(intent);
                                    } catch (android.content.ActivityNotFoundException e) {
                                        Log.e(TAG, "GrandChildPopup ContentDelegate: No activity found to handle URI: " + uriString, e);
                                        Toast.makeText(MainActivity.this, "No app found to open this link: " + scheme, Toast.LENGTH_LONG).show();
                                        // Optionally, could try to load about:blank or show an error page in GeckoView
                                    } catch (Exception e) {
                                        Log.e(TAG, "GrandChildPopup ContentDelegate: Error launching generic Intent for URI: " + uriString, e);
                                        Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    @Override
                            public void onCloseRequest(GeckoSession session) {
                                Log.d(TAG, "GrandChildPopup's ContentDelegate: onCloseRequest for " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
                                int indexToClose = geckoSessionList.indexOf(session);
                                if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                            }
                        });

                        grandChildSession.setPromptDelegate(MainActivity.this);
                        grandChildSession.setScrollDelegate(MainActivity.this);

                        // Add ConsoleDelegate for the grandchild popup session
                        // grandChildSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

                        // Ephemeral gating for grandchild popup: avoid adding blank/null to tabs
                        if (isBlankOrNullUri(newUriFromPopup)) {
                            Log.i(TAG, "Popup's NavDelegate: creating EPHEMERAL grandchild (blank/null URI). Not adding to tab list yet.");
                            ephemeralSessions.add(grandChildSession);
                            sessionUrlMap.put(grandChildSession, newUriFromPopup != null ? newUriFromPopup : "about:blank");
                            scheduleEphemeralTimeout(grandChildSession, 5000);
                            return GeckoResult.fromValue(grandChildSession);
                        } else {
                            synchronized (geckoSessionList) { geckoSessionList.add(grandChildSession); }
                            sessionUrlMap.put(grandChildSession, newUriFromPopup);

                            final int newGrandChildIndex = geckoSessionList.indexOf(grandChildSession);
                            if (newGrandChildIndex != -1) {
                            runOnUiThread(() -> {
                                    Log.d(TAG, "Popup's NavDelegate: Switching to grandchild tab: " + newGrandChildIndex + " for URI: " + newUriFromPopup);
                                    switchToTab(newGrandChildIndex);
                                });
                                } else {
                                Log.e(TAG, "Popup's NavDelegate: Grandchild session not found for URI: " + newUriFromPopup);
                                if (newUriFromPopup != null) grandChildSession.loadUri(newUriFromPopup);
                            }
                            return GeckoResult.fromValue(grandChildSession);
                        }
                    }
                });

                newTabSession.setContentDelegate(new ContentDelegate() {
                    @Override
                    public void onFullScreen(GeckoSession session, boolean fullScreen) {
                        // This is for a new tab/popup. PiP logic is primarily for the main session.
                        if (session == getActiveSession()) { runOnUiThread(() -> { if (fullScreen) enterFullScreen(); else exitFullScreen(); }); }
                    }
                    @Override
                    public void onExternalResponse(GeckoSession session, org.mozilla.geckoview.WebResponse response) {
                        String uriString = response.uri;
                        Log.d(TAG, "Popup ContentDelegate onExternalResponse: URI received: " + uriString);

                        if (uriString == null) {
                            Log.w(TAG, "Popup ContentDelegate onExternalResponse: Null URI, falling back to download handler.");
                            runOnUiThread(() -> {
                                handleDownloadResponse(response);
                                // Close ephemeral popup if this was only used to trigger a download
                                if (ephemeralSessions.contains(session)) {
                                    ephemeralSessions.remove(session);
                                    cancelEphemeralTimeout(session);
                                    int indexToClose = geckoSessionList.indexOf(session);
                                    if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                                }
                            });
                            return;
                        }

                        Uri parsedUri = Uri.parse(uriString);
                        String scheme = parsedUri.getScheme();

                        if ("intent".equalsIgnoreCase(scheme)) {
                            Log.d(TAG, "Popup ContentDelegate: Handling intent scheme for URI: " + uriString);
                            runOnUiThread(() -> {
                                try {
                                    Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Log.d(TAG, "Popup ContentDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                        Log.i(TAG, "Popup ContentDelegate: Activity found for intent. Attempting to start...");
                                        startActivity(intent);
                                        Log.i(TAG, "Popup ContentDelegate: startActivity called for main intent.");
                                    } else {
                                        Log.w(TAG, "Popup ContentDelegate: No activity found for main intent. Checking fallback.");
                                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                        if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                            Log.i(TAG, "Popup ContentDelegate: Fallback URL found: " + fallbackUrl);
                                            if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                                Log.d(TAG, "Popup ContentDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                                try {
                                                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                                    fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    fallbackIntent.setPackage("com.android.vending");
                                                    startActivity(fallbackIntent);
                                                    Log.i(TAG, "Popup ContentDelegate: startActivity called for Play Store fallback.");
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    Log.w(TAG, "Popup ContentDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                                    loadUriInGeckoView(session, fallbackUrl);
                                                } catch (Exception ex) {
                                                    Log.e(TAG, "Popup ContentDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                                    loadUriInGeckoView(session, fallbackUrl);
                                                }
                                            } else {
                                                Log.i(TAG, "Popup ContentDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                                loadUriInGeckoView(session, fallbackUrl);
                                            }
                                        } else {
                                            Log.w(TAG, "Popup ContentDelegate: No activity found for intent and no browser_fallback_url.");
                                            Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (URISyntaxException e) {
                                    Log.e(TAG, "Popup ContentDelegate: Invalid intent URI syntax: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e(TAG, "Popup ContentDelegate: General error handling intent URI: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                            if (uriString.startsWith("https://play.google.com/store/") || uriString.startsWith("http://play.google.com/store/")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.android.vending");
                                    Log.d(TAG, "Popup ContentDelegate: Attempting to launch Play Store with URI: " + uriString);
                                    startActivity(intent);
                                } catch (android.content.ActivityNotFoundException e) {
                                    Log.e(TAG, "Popup ContentDelegate: Play Store app not found for URI: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Play Store app not found. Trying to open in browser.", Toast.LENGTH_LONG).show();
                                    loadUriInGeckoView(session, uriString);
                                } catch (Exception e) {
                                    Log.e(TAG, "Popup ContentDelegate: Error launching Play Store with URI: " + uriString, e);
                                    Toast.makeText(MainActivity.this, "Could not open link in Play Store.", Toast.LENGTH_SHORT).show();
                                    loadUriInGeckoView(session, uriString);
                                }
                            } else {
                                Log.d(TAG, "Popup ContentDelegate: HTTP/HTTPS URI is not Play Store or Intent, falling back to download handling: " + uriString);
                                runOnUiThread(() -> {
                                    handleDownloadResponse(response);
                                    // After download, if session ends up blank-like, close it and return focus to parent
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        String cur = sessionUrlMap.get(session);
                                        if (isBlankOrNullUri(cur)) {
                                            GeckoSession parent = popupParentMap.get(session);
                                            int indexToClose = geckoSessionList.indexOf(session);
                                            if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                                            if (parent != null) {
                                                int pIdx = geckoSessionList.indexOf(parent);
                                                if (pIdx != -1) { switchToTab(pIdx); }
                                            }
                                        }
                                    }, 250);
                                    // Also clear ephemeral tracking if any
                                    if (ephemeralSessions.contains(session)) {
                                        ephemeralSessions.remove(session);
                                        cancelEphemeralTimeout(session);
                                    }
                                });
                            }
                        } else {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.d(TAG, "Popup ContentDelegate: Attempting to launch generic Intent for scheme '" + scheme + "' with URI: " + uriString);
                                startActivity(intent);
                            } catch (android.content.ActivityNotFoundException e) {
                                Log.e(TAG, "Popup ContentDelegate: No activity found to handle URI: " + uriString, e);
                                Toast.makeText(MainActivity.this, "No app found to open this link: " + scheme, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Popup ContentDelegate: Error launching generic Intent for URI: " + uriString, e);
                                Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCloseRequest(GeckoSession session) { // window.close() in the new tab
                        Log.d(TAG, "Popup's ContentDelegate: onCloseRequest for session " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
                        int indexToClose = geckoSessionList.indexOf(session);
                        if (indexToClose != -1) {
                            closeTab(indexToClose);
                        } else {
                            session.close();
                        }
                    }

                    @Override
                    public void onContextMenu(GeckoSession session, int screenX, int screenY, 
                                             GeckoSession.ContentDelegate.ContextElement element) {
                        // Filter out JavaScript and mailto URLs
                        String linkUri = element.linkUri;
                        if (linkUri != null && (linkUri.startsWith("javascript:") || linkUri.startsWith("mailto:"))) {
                            Log.d(TAG, "Popup Context menu blocked for " + linkUri.substring(0, Math.min(linkUri.length(), 20)) + "...");
                            return;
                        }

                        // Show context menu for supported elements with links
                        if (element.linkUri != null) {
                            Log.d(TAG, "Popup Context menu triggered for linkUri: " + element.linkUri + 
                                       ", type: " + element.type + " at (" + screenX + "," + screenY + ")");
                            showContextMenu(screenX, screenY, element);
                        }
                    }
                });

                // ***** THIS IS THE CRITICAL FIX *****
                newTabSession.setPromptDelegate(MainActivity.this); 
                newTabSession.setScrollDelegate(MainActivity.this); 
                // ***** END CRITICAL FIX *****

                // Add ConsoleDelegate for the new tab/popup session
                // newTabSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

                // Ephemeral gating: only blank-like initial URIs should be ephemeral; others become real tabs
                if (isBlankOrNullUri(uri)) {
                    Log.i(TAG, "onNewSession: creating EPHEMERAL popup (blank/null URI). Not adding to tab list yet.");
                    ephemeralSessions.add(newTabSession);
                    sessionUrlMap.put(newTabSession, uri != null ? uri : "about:blank");
                    if (originatingSession != null) { popupParentMap.put(newTabSession, originatingSession); }
                    scheduleEphemeralTimeout(newTabSession, 5000);
                    return GeckoResult.fromValue(newTabSession);
                } else {
                    synchronized (geckoSessionList) { geckoSessionList.add(newTabSession); }
                    sessionUrlMap.put(newTabSession, uri);
                    if (originatingSession != null) { popupParentMap.put(newTabSession, originatingSession); }
                    final int newTabIndex = geckoSessionList.indexOf(newTabSession);
                    if (newTabIndex != -1) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "createNewTab NavDelegate: Switching to new tab index: " + newTabIndex + " for URI: " + uri);
                            switchToTab(newTabIndex);
                        });
                    } else {
                        Log.e(TAG, "createNewTab NavDelegate: New tab session not found in list for URI: " + uri);
                        newTabSession.loadUri(uri);
                    }
                    Log.d(TAG, "createNewTab NavDelegate: Returning new GeckoSession to GeckoView for URI: " + uri);
                    return GeckoResult.fromValue(newTabSession);
                }
            } // End of onNewSession for the main tab's NavigationDelegate
        });

        newSession.setContentDelegate(new ContentDelegate() {
            @Override
            public void onFullScreen(GeckoSession session, boolean fullScreen) {
                // This is for a new tab/popup. PiP logic is primarily for the main session.
                if (session == getActiveSession()) { runOnUiThread(() -> { if (fullScreen) enterFullScreen(); else exitFullScreen(); }); }
            }
            @Override
            public void onExternalResponse(GeckoSession session, org.mozilla.geckoview.WebResponse response) {
                String uriString = response.uri;
                Log.d(TAG, "Main ContentDelegate onExternalResponse: URI received: " + uriString);

                if (uriString == null) {
                    Log.w(TAG, "Main ContentDelegate onExternalResponse: Null URI, falling back to download handler.");
                    runOnUiThread(() -> handleDownloadResponse(response));
                    return;
                }

                Uri parsedUri = Uri.parse(uriString);
                String scheme = parsedUri.getScheme();

                if ("intent".equalsIgnoreCase(scheme)) {
                    Log.d(TAG, "Main ContentDelegate: Handling intent scheme for URI: " + uriString);
                    runOnUiThread(() -> {
                        try {
                            Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.d(TAG, "Main ContentDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                            if (intent.resolveActivity(getPackageManager()) != null) {
                                Log.i(TAG, "Main ContentDelegate: Activity found for intent. Attempting to start...");
                                startActivity(intent);
                                Log.i(TAG, "Main ContentDelegate: startActivity called for main intent.");
                            } else {
                                Log.w(TAG, "Main ContentDelegate: No activity found for main intent. Checking fallback.");
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                                    Log.i(TAG, "Main ContentDelegate: Fallback URL found: " + fallbackUrl);
                                    if (fallbackUrl.startsWith("https://play.google.com/store/") || fallbackUrl.startsWith("http://play.google.com/store/")) {
                                        Log.d(TAG, "Main ContentDelegate: Fallback is Play Store link. Attempting to open in Play Store app.");
                                        try {
                                            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
                                            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            fallbackIntent.setPackage("com.android.vending");
                                            startActivity(fallbackIntent);
                                            Log.i(TAG, "Main ContentDelegate: startActivity called for Play Store fallback.");
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            Log.w(TAG, "Main ContentDelegate: Play Store app not found for fallback. Loading URL in browser: " + fallbackUrl, anfe);
                                            loadUriInGeckoView(session, fallbackUrl);
                                        } catch (Exception ex) {
                                            Log.e(TAG, "Main ContentDelegate: Error launching Play Store for fallback. Loading in browser.", ex);
                                            loadUriInGeckoView(session, fallbackUrl);
                                        }
                                    } else {
                                        Log.i(TAG, "Main ContentDelegate: Fallback URL is not Play Store. Loading in browser: " + fallbackUrl);
                                        loadUriInGeckoView(session, fallbackUrl);
                                    }
                                } else {
                                    Log.w(TAG, "Main ContentDelegate: No activity found for intent and no browser_fallback_url.");
                                    Toast.makeText(MainActivity.this, "No app found to open this link.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (URISyntaxException e) {
                            Log.e(TAG, "Main ContentDelegate: Invalid intent URI syntax: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Invalid link format.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Main ContentDelegate: General error handling intent URI: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    if (uriString.startsWith("https://play.google.com/store/") || uriString.startsWith("http://play.google.com/store/")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.android.vending");
                            Log.d(TAG, "Main ContentDelegate: Attempting to launch Play Store with URI: " + uriString);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Log.e(TAG, "Main ContentDelegate: Play Store app not found for URI: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Play Store app not found. Trying to open in browser.", Toast.LENGTH_LONG).show();
                            loadUriInGeckoView(session, uriString);
                        } catch (Exception e) {
                            Log.e(TAG, "Main ContentDelegate: Error launching Play Store with URI: " + uriString, e);
                            Toast.makeText(MainActivity.this, "Could not open link in Play Store.", Toast.LENGTH_SHORT).show();
                            loadUriInGeckoView(session, uriString);
                        }
                    } else {
                        Log.d(TAG, "Main ContentDelegate: HTTP/HTTPS URI is not Play Store or Intent, falling back to download handling: " + uriString);
                        runOnUiThread(() -> handleDownloadResponse(response));
                    }
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, parsedUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.d(TAG, "Main ContentDelegate: Attempting to launch generic Intent for scheme '" + scheme + "' with URI: " + uriString);
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        Log.e(TAG, "Main ContentDelegate: No activity found to handle URI: " + uriString, e);
                        Toast.makeText(MainActivity.this, "No app found to open this link: " + scheme, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Main ContentDelegate: Error launching generic Intent for URI: " + uriString, e);
                        Toast.makeText(MainActivity.this, "Could not open link.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Add onCloseRequest to the main session's ContentDelegate as well
            @Override
            public void onCloseRequest(GeckoSession session) {
                Log.d(TAG, "ContentDelegate: onCloseRequest received for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "Unknown URI"));
                // This is typically called by window.close() from JavaScript.
                // We should close the tab associated with this session.
                int indexToClose = geckoSessionList.indexOf(session);
                if (indexToClose != -1) {
                    closeTab(indexToClose);
                } else {
                    // If for some reason it's not in our main list, just close the session.
                    session.close();
                }
            }

            @Override
            public void onContextMenu(GeckoSession session, int screenX, int screenY, 
                                     GeckoSession.ContentDelegate.ContextElement element) {
                // Filter out JavaScript and mailto URLs
                String linkUri = element.linkUri;
                if (linkUri != null && (linkUri.startsWith("javascript:") || linkUri.startsWith("mailto:"))) {
                    Log.d(TAG, "Context menu blocked for " + linkUri.substring(0, Math.min(linkUri.length(), 20)) + "...");
                    return;
                }

                // Show context menu for supported elements with links
                if (element.linkUri != null) {
                    Log.d(TAG, "Context menu triggered for linkUri: " + element.linkUri + 
                               ", type: " + element.type + " at (" + screenX + "," + screenY + ")");
                    showContextMenu(screenX, screenY, element);
                }
            }
        });

        newSession.setScrollDelegate(this); // Set MainActivity as scroll delegate
        newSession.setPromptDelegate(this); // Added for regular new sessions

        // Add ConsoleDelegate to log web console messages
        // newSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

        geckoSessionList.add(newSession);
        String targetUrl = (initialUrl != null && !initialUrl.isEmpty()) ? initialUrl : "about:blank";
        sessionUrlMap.put(newSession, targetUrl); // Store initial URL

        newSession.loadUri(targetUrl);
        Log.d(TAG, "Loading initial URL in new session: " + targetUrl);

        if (switchToTab) {
             // No need to remove fragment again here as it was done at the start
            switchToTab(geckoSessionList.size() - 1); // Switch to the newly added tab
        } else {
            // If not switching, ensure the UI reflects the *current* active tab state
            updateUIForActiveSession(); 
             // saveSessionState(); // REMOVED: This was causing intermediate saves during restore
        }
    }

    // Overload for previous behavior (e.g., new tab button)
    private void createNewTab(boolean switchToTab) {
        createNewTab(getDefaultHomepageUrl(), switchToTab);
    }

    // Method to switch the active tab
    private void switchToTab(int index) {
        // --- AGGRESSIVE CLAMPING START ---
        int targetIndex = index;
        if (geckoSessionList.isEmpty()) {
            Log.e(TAG, "switchToTab called on an empty session list. Original index: " + index + ". Creating new tab as fallback.");
            this.activeSessionIndex = -1; // Ensure createNewTab knows no tab is active
            if (runtime != null) {
                createNewTab(true); // This will eventually call switchToTab with a valid state
            } else {
                Log.e(TAG, "Cannot create new tab in switchToTab fallback as runtime is null.");
            }
            return;
        }

        if (targetIndex >= geckoSessionList.size()) {
            Log.w(TAG, "switchToTab: Index " + targetIndex + " is out of bounds for list size " + 
                       geckoSessionList.size() + ". Clamping to last valid index: " + (geckoSessionList.size() - 1));
            targetIndex = geckoSessionList.size() - 1;
        }
        if (targetIndex < 0) {
            Log.w(TAG, "switchToTab: Index " + targetIndex + " is negative. Clamping to 0.");
            targetIndex = 0;
        }
        // --- AGGRESSIVE CLAMPING END ---

        GeckoSession targetSession = geckoSessionList.get(targetIndex);

        // If this tab (by targetIndex) is already the active one AND GeckoView is displaying its session
        if (this.activeSessionIndex == targetIndex && geckoView.getSession() == targetSession) {
            Log.d(TAG, "switchToTab: Tab " + targetIndex + " is already active and displayed. Updating UI only.");
            updateUIForActiveSession(); // Ensure UI is fresh (e.g. URL bar)
            // Also update mLastValidUrl in this edge case
            String currentUrl = sessionUrlMap.get(targetSession);
            if (currentUrl != null) {
                mLastValidUrl = currentUrl;
            }
            return;
        }

        // Proceed with the switch
        Log.d(TAG, "Switching to tab index: " + targetIndex + ". Current global activeSessionIndex before switch: " + this.activeSessionIndex);

        GeckoSession sessionCurrentlyInView = geckoView.getSession();
        if (sessionCurrentlyInView != null) {
            // Only capture snapshot if releasing a session that is different from the target session
            if (sessionCurrentlyInView != targetSession) {
                // Determine if the sessionCurrentlyInView was the 'logical' old active session
                GeckoSession logicalOldActiveSession = (this.activeSessionIndex >= 0 && 
                                                        this.activeSessionIndex < geckoSessionList.size() && // check if old index is still valid (list might have changed)
                                                        this.activeSessionIndex != targetIndex) // and not the target index
                                                       ? geckoSessionList.get(this.activeSessionIndex) 
                                                       : null;

                if (logicalOldActiveSession != null && sessionCurrentlyInView == logicalOldActiveSession) {
                     captureSnapshot(logicalOldActiveSession);
                } else {
                     // If the session in view is not the logical old active one, but it's being released.
                     Log.d(TAG, "Capturing snapshot of non-logical-active/unexpected session being released: " + (sessionUrlMap.containsKey(sessionCurrentlyInView) ? sessionUrlMap.get(sessionCurrentlyInView) : "N/A"));
                     captureSnapshot(sessionCurrentlyInView);
                }
            }
            
            // CRITICAL: Deactivate the old session to stop media playback before releasing
            try {
                sessionCurrentlyInView.setActive(false);
                Log.d(TAG, "Deactivated old session: " + (sessionUrlMap.containsKey(sessionCurrentlyInView) ? sessionUrlMap.get(sessionCurrentlyInView) : "N/A"));
            } catch (Throwable t) {
                Log.e(TAG, "Failed to deactivate old session", t);
            }
            
            geckoView.releaseSession();
            Log.d(TAG, "Released session from GeckoView: " + (sessionUrlMap.containsKey(sessionCurrentlyInView) ? sessionUrlMap.get(sessionCurrentlyInView) : "N/A"));
        }

        this.activeSessionIndex = targetIndex;
        this.geckoSession = targetSession; // Update the global 'geckoSession' convenience field

        // CRITICAL: Keep mLastValidUrl in sync with the active tab's URL.
        String currentUrl = sessionUrlMap.get(getActiveSession());
        if (currentUrl != null) {
            mLastValidUrl = currentUrl;
        }

        geckoView.setSession(targetSession);
        Log.d(TAG, "Attached session to GeckoView: " + (sessionUrlMap.containsKey(targetSession) ? sessionUrlMap.get(targetSession) : "N/A"));
        
        // CRITICAL: Activate the session so Gecko resumes rendering
        try {
            targetSession.setActive(true);
            Log.d(TAG, "Activated switched-to session index=" + targetIndex);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to activate session on switch", t);
        }
        
        // captureSnapshot(targetSession); // CAPTURE SNAPSHOT OF THE NEWLY ACTIVE TAB -> REMOVED, handled by onPageStop

        updateUIForActiveSession();
        saveSessionState();
        geckoView.requestFocus();
    }

    // Method to capture snapshot for a session (for tab switcher previews)
    private void captureSnapshot(GeckoSession session) {
        if (session == null || geckoView == null) {
            Log.w(TAG, "Cannot capture snapshot, session or geckoView is null.");
            return;
        }
        // Only capture for the session currently attached to the GeckoView
        if (geckoView.getSession() != session) {
            Log.d(TAG, "Snapshot skipped: Session index " + geckoSessionList.indexOf(session) + " is not the active session in GeckoView.");
            return;
        }
        // Throttle snapshots per session
        long now = System.currentTimeMillis();
        Long last = sessionLastSnapshotMs.get(session);
        if (last != null && (now - last) < SNAPSHOT_MIN_INTERVAL_MS) {
            Log.d(TAG, "Snapshot throttled for session index " + geckoSessionList.indexOf(session) + ": " + (now - last) + "ms since last < " + SNAPSHOT_MIN_INTERVAL_MS + "ms");
            return;
        }
        sessionLastSnapshotMs.put(session, now);

        Log.d(TAG, "Requesting snapshot for active session index: " + geckoSessionList.indexOf(session));

        GeckoResult<Bitmap> result = geckoView.capturePixels();
        result.accept(originalBitmap -> {
            if (originalBitmap != null) {
                Log.d(TAG, "Snapshot captured successfully for session index: " + geckoSessionList.indexOf(session) +
                        " (Original size: " + originalBitmap.getWidth() + "x" + originalBitmap.getHeight() + ")");

                // Resize to target width while preserving aspect ratio
                int originalWidth = originalBitmap.getWidth();
                int originalHeight = originalBitmap.getHeight();
                if (originalWidth == 0 || originalHeight == 0) {
                    Log.w(TAG, "Snapshot has zero dimensions, skipping resize/storage.");
                    originalBitmap.recycle();
                    return;
                }
                float aspectRatio = (float) originalHeight / originalWidth;
                int targetHeight = Math.round(SNAPSHOT_WIDTH * aspectRatio);
                if (targetHeight <= 0) targetHeight = 1;

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, SNAPSHOT_WIDTH, targetHeight, true);
                originalBitmap.recycle();

                // Skip saving/display if snapshot is mostly white (common when compositor paused)
                if (isBitmapMostlyWhite(resizedBitmap)) {
                    Log.w(TAG, "Snapshot mostly white; skipping store/display for session index: " + geckoSessionList.indexOf(session));
                    resizedBitmap.recycle();
                    return;
                }

                Log.d(TAG, "Snapshot resized to: " + resizedBitmap.getWidth() + "x" + resizedBitmap.getHeight());
                String url = sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : null;
                sessionSnapshotMap.put(session, resizedBitmap);
                sessionSnapshotUrlMap.put(session, url);
                if (url != null) {
                    saveSnapshotToDisk(resizedBitmap, url);
                }
            } else {
                Log.w(TAG, "Snapshot capture returned null bitmap for session index: " + geckoSessionList.indexOf(session));
            }
        }, e -> {
            Log.e(TAG, "Snapshot capture failed for session index: " + geckoSessionList.indexOf(session), e);
        });
    }

    // Heuristic: quickly check if bitmap is mostly white by sampling a small grid
    private boolean isBitmapMostlyWhite(Bitmap bmp) {
        try {
            if (bmp == null || bmp.isRecycled()) return true;
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            if (w <= 0 || h <= 0) return true;
            final int samples = 6; // 6x6 grid
            int whiteish = 0;
            int total = samples * samples;
            for (int yi = 0; yi < samples; yi++) {
                for (int xi = 0; xi < samples; xi++) {
                    int x = (int) ((xi + 0.5f) * w / samples);
                    int y = (int) ((yi + 0.5f) * h / samples);
                    if (x >= w) x = w - 1;
                    if (y >= h) y = h - 1;
                    int c = bmp.getPixel(x, y);
                    int a = (c >>> 24) & 0xFF;
                    int r = (c >>> 16) & 0xFF;
                    int g = (c >>> 8) & 0xFF;
                    int b = c & 0xFF;
                    if (a > 230 && r > 245 && g > 245 && b > 245) whiteish++;
                }
            }
            return whiteish >= (int) (total * 0.9f);
        } catch (Throwable t) {
            return false;
        }
    }

    // If overlay is up and we saw no early progress, try a gentle reload of the active tab
    private void maybeReloadActiveIfStuck() {
        try {
            // Do not intervene if we've already seen progress within the resume window
            if (resumeHadProgress) {
                Log.d(TAG, "[ResumeWatchdog] Skipping reload: progress observed");
                return;
            }
            GeckoSession active = getActiveSession();
            if (active == null) return;
            // Only intervene if the active session is currently attached to GeckoView
            if (geckoView == null || geckoView.getSession() != active) {
                Log.d(TAG, "[ResumeWatchdog] Skipping reload: active session not attached to GeckoView");
                return;
            }
            // Prefer reload(); if page is about:blank or URL known, loadUri fallback
            String url = sessionUrlMap.get(active);
            // Only consider http(s) navigations, never about:blank bootstrap
            if (url == null || !isHttpLike(url)) {
                Log.d(TAG, "[ResumeWatchdog] Skipping reload: not an http(s) URL (" + url + ")");
                return;
            }
            Log.w(TAG, "[ResumeWatchdog] Triggering reload on active session due to stalled overlay");
            forceReloadActiveSession("ResumeWatchdog");
        } catch (Throwable ignore) {}
    }

    // Heuristic: early after resume/startup, if no progress and GeckoView has no session, consider paint stalled
    private boolean isPagePaintLikelyStalled() {
        try {
            if (!isTvDevice()) return false;
            GeckoSession active = getActiveSession();
            if (geckoView == null) return true;
            boolean noSessionAttached = (geckoView.getSession() == null);
            long sinceResume = System.currentTimeMillis() - resumeAtMs;
            // Consider stalled if: nothing attached OR no progress within ~1.2s after resume
            return noSessionAttached || (!resumeHadProgress && sinceResume > 1200);
        } catch (Throwable t) {
            return false;
        }
    }

    // Updates URL bar and navigation buttons based on the active session state
    private void updateUIForActiveSession() {
        if (!isUiReady) {
            Log.w(TAG, "updateUIForActiveSession: UI not ready, skipping update.");
            return;
        }

        GeckoSession activeSession = getActiveSession();
        if (activeSession == null) {
            Log.w(TAG, "updateUIForActiveSession: Active session is null, skipping update.");
            return;
        }

        String url = sessionUrlMap.get(activeSession);
        if (url == null) {
            url = "";
        }

        final String finalUrl = url;
        runOnUiThread(() -> {
            if (urlBar != null) {
                urlBar.setText(finalUrl);
            }
            if (minimizedUrlBar != null && !isControlBarExpanded) {
                minimizedUrlBar.setText(finalUrl);
            }
            if (progressBar != null) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Handle back button press with special handling for fullscreen video.
     * - If in fullscreen video, exit fullscreen and show cursor
     * - If settings panel is visible, hide it
     * - Otherwise, perform default back navigation
     */
    @Override
    public void onBackPressed() {
        // Check if we're in fullscreen video mode
        if (isInGeckoViewFullscreen && isFullscreenVideo()) {
            Log.d(TAG, "onBackPressed: Exiting fullscreen video mode");
            // This will trigger exitFullScreen() which will show the cursor
            getActiveSession().exitFullScreen();
            return;
        }
        
        // Check if settings panel is visible
        if (settingsPanelLayout.getVisibility() == View.VISIBLE) {
            hideSettingsPanel();
            return;
        }
        
        // Default back navigation
        GeckoSession activeSession = getActiveSession();
        if (activeSession != null) {
            Log.d(TAG, "onBackPressed: Calling goBack() on active GeckoSession.");
            activeSession.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Method to close a tab
    private void closeTab(int indexToClose) {
        if (indexToClose < 0 || indexToClose >= geckoSessionList.size()) {
            Log.e(TAG, "Invalid index for closeTab: " + indexToClose + ", list size: " + geckoSessionList.size());
            return;
        }

        Log.d(TAG, "Closing tab index: " + indexToClose);
        GeckoSession sessionToClose = geckoSessionList.remove(indexToClose);
        String urlOfClosedTab = sessionUrlMap.remove(sessionToClose);
        Bitmap removedSnapshot = sessionSnapshotMap.remove(sessionToClose);
        sessionSnapshotUrlMap.remove(sessionToClose);
        if (removedSnapshot != null && !removedSnapshot.isRecycled()) {
            removedSnapshot.recycle();
        }
        // --- Delete snapshot from disk ---
        if (urlOfClosedTab != null) {
            deleteSnapshotFromDisk(urlOfClosedTab);
        }
        // --- End delete snapshot from disk ---
        sessionToClose.close();

        int oldGlobalActiveSessionIndex = this.activeSessionIndex; // Capture before any potential changes by switchToTab

        if (geckoSessionList.isEmpty()) {
            // No tabs left, create a new one.
            // activeSessionIndex should be -1 before calling createNewTab if list is empty.
            this.activeSessionIndex = -1;
            createNewTab(true); // This will set activeSessionIndex and call switchToTab.
        } else {
            int newTargetActiveIndex;
            if (oldGlobalActiveSessionIndex == indexToClose) {
                // The active tab was closed. Switch to the one before it, or 0 if it was the first.
                // Since indexToClose is removed, the list is smaller.
                // If indexToClose was 0, new target is 0.
                // If indexToClose was >0, new target is indexToClose-1.
                newTargetActiveIndex = Math.max(0, indexToClose - 1);
            } else if (oldGlobalActiveSessionIndex > indexToClose) {
                // Active tab was after the closed tab. Its index in the (now smaller) list shifts down by 1.
                newTargetActiveIndex = oldGlobalActiveSessionIndex - 1;
            } else { // oldGlobalActiveSessionIndex < indexToClose
                // Active tab was before the closed tab. Its index remains the same.
                newTargetActiveIndex = oldGlobalActiveSessionIndex;
            }

            // Ensure newTargetActiveIndex is valid for the current (modified) list size
            // It should already be valid if logic above is correct, but clamp as safety.
            newTargetActiveIndex = Math.min(newTargetActiveIndex, geckoSessionList.size() - 1);
            newTargetActiveIndex = Math.max(0, newTargetActiveIndex); // Should be redundant if list not empty

            switchToTab(newTargetActiveIndex); // Let switchToTab handle setting global activeSessionIndex
        }
        saveSessionState(); // Save state after all changes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clean up temporary upload files
        cleanupTempUploadFiles();

        // Close all sessions
        for (GeckoSession session : geckoSessionList) {
            if (session != null) {
                session.close();
            }
        }
        geckoSessionList.clear();
        sessionUrlMap.clear(); 
        sessionSnapshotMap.clear(); // Clear snapshot map
        sessionSnapshotUrlMap.clear();
        activeSessionIndex = -1;
        // Note: GeckoRuntime might persist beyond Activity onDestroy depending on its creation context
        // If using Application context for runtime, don't close it here.
        // If using Activity context (like current code), runtime should ideally be closed,
        // but GeckoView docs often show runtime persisting. Be mindful of leaks.
        // if (runtime != null) { runtime.shutdown(); runtime = null; }

        // Unregister download complete receiver
        if (downloadCompleteReceiver != null) {
            unregisterReceiver(downloadCompleteReceiver);
        }

        // Shutdown background executors
        try {
            if (ioExecutor != null) {
                ioExecutor.shutdownNow();
            }
        } catch (Throwable ignore) {}
    }

    // --- Control Bar State Logic ---

    private void showExpandedBar() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            // Cancel any ongoing animations
            try { minimizedControlBar.clearAnimation(); } catch (Throwable ignored) {}
            try { expandedControlBar.clearAnimation(); } catch (Throwable ignored) {}

            boolean wasVisible = expandedControlBar.getVisibility() == View.VISIBLE;
            expandedControlBar.setVisibility(View.VISIBLE);
            minimizedControlBar.setVisibility(View.GONE);
            // Ensure the main URL bar is interactive
            urlBar.setFocusableInTouchMode(true);
            urlBar.setFocusable(true);
            urlBar.setClickable(true);

            // Animate expanded bar in for consistency with minimized bar
            if (!wasVisible) {
                slideInFromBottom(expandedControlBar);
            }

            isControlBarExpanded = true;
            isControlBarHidden = false;
            scrollTriggeredHideRequest = false; // reset
            Log.d(TAG, "showExpandedBar: Expanded bar visible.");
        }
    }

    private void showMinimizedBar() {
        if (expandedControlBar != null && minimizedControlBar != null && getActiveSession() != null) {
            // Cancel any ongoing animations
            try { minimizedControlBar.clearAnimation(); } catch (Throwable ignored) {}
            try { expandedControlBar.clearAnimation(); } catch (Throwable ignored) {}

            expandedControlBar.setVisibility(View.GONE);
            if (minimizedControlBar.getVisibility() != View.VISIBLE) {
                minimizedControlBar.setVisibility(View.VISIBLE);
                slideInFromBottom(minimizedControlBar);
            }
            // Main URL bar should not be interactive when minimized bar is shown
            urlBar.setFocusable(false);
            urlBar.setClickable(false);
            
            isControlBarExpanded = false;
            isControlBarHidden = false;
            scrollTriggeredHideRequest = false; // reset any pending hide
            // Update the text of the minimized URL bar
            String currentUrl = sessionUrlMap.containsKey(getActiveSession()) ? sessionUrlMap.get(getActiveSession()) : "";
            if (currentUrl != null) minimizedUrlBar.setText(currentUrl);
            Log.d(TAG, "showMinimizedBar: Minimized bar visible.");
        } else if (getActiveSession() == null) {
            Log.w(TAG, "showMinimizedBar: Cannot show, active session is null. Hiding both bars.");
            hideBothBars(); // Fallback if no session
        }
    }

    private void hideBothBars() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            // Enforce: never hide unless a scroll was detected on the same page
            if (!scrollTriggeredHideRequest) {
                Log.d(TAG, "hideBothBars: ignored because no scrollTriggeredHideRequest");
                return;
            }
            // Cancel any ongoing animations on expanded bar
            try { expandedControlBar.clearAnimation(); } catch (Throwable ignored) {}
            if (expandedControlBar.getVisibility() == View.VISIBLE) {
                // Animate expanded bar out, then set to GONE
                slideOutToBottom(expandedControlBar, new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {
                        expandedControlBar.setVisibility(View.GONE);
                    }
                    @Override public void onAnimationRepeat(Animation animation) {}
                });
            } else {
                expandedControlBar.setVisibility(View.GONE);
            }
            if (minimizedControlBar.getVisibility() == View.VISIBLE) {
                // Animate minimized bar out, then set to GONE
                slideOutToBottom(minimizedControlBar, new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {
                        minimizedControlBar.setVisibility(View.GONE);
                    }
                    @Override public void onAnimationRepeat(Animation animation) {}
                });
            } else {
                minimizedControlBar.setVisibility(View.GONE);
            }

            isControlBarExpanded = false; // When hidden, it's not expanded
            isControlBarHidden = true;
            scrollTriggeredHideRequest = false; // consume the request
            Log.d(TAG, "hideBothBars: Both control bars hidden.");
        }
    }

    @Override
    public void onScrollChanged(GeckoSession session, int scrollX, int scrollY) {
        // Update our scroll tracking
        mLastScrollY = scrollY;
        mCanScrollUp = scrollY > 0;
        if (isTvDevice()) {
            Log.d(TAG, "[TV][SCROLL] onScrollChanged x=" + scrollX + " y=" + scrollY + " mCanScrollUp=" + mCanScrollUp);
        }

        // If we're in a brief suppression period after navigation/back/forward,
        // ignore this scroll to avoid auto-hiding due to restored scroll position
        long now = System.currentTimeMillis();
        if (now < suppressScrollHideUntilMs) {
            accumulatedScrollDownPx = 0;
            accumulatedScrollUpPx = 0;
            lastScrollY = scrollY; // sync baseline
            Log.d(TAG, "onScrollChanged: Suppressed due to recent navigation (" + (suppressScrollHideUntilMs - now) + "ms remaining)");
            // Still update focusable rects and return
            updateFocusableRects();
            return;
        }

        int dy = scrollY - lastScrollY;
        lastScrollY = scrollY;

        if (dy > 0) {
            // Scrolling down
            accumulatedScrollDownPx += dy;
            accumulatedScrollUpPx = 0;
            if (!isControlBarHidden && accumulatedScrollDownPx >= hideThresholdPx) {
                // Mark that a scroll triggered the hide on this page
                scrollTriggeredHideRequest = true;
                hideBothBars();
                Log.d(TAG, "onScrollChanged: Scrolled DOWN (" + accumulatedScrollDownPx + "px) - Hiding both bars.");
                accumulatedScrollDownPx = 0; // reset after action
            }
        } else if (dy < 0) {
            // Scrolling up
            int up = -dy;
            accumulatedScrollUpPx += up;
            accumulatedScrollDownPx = 0;
            if ((isControlBarHidden || isControlBarExpanded) && accumulatedScrollUpPx >= showThresholdPx) {
                showMinimizedBar();
                Log.d(TAG, "onScrollChanged: Scrolled UP (" + accumulatedScrollUpPx + "px) - Showing minimized bar.");
                accumulatedScrollUpPx = 0; // reset after action
            }
        }

        // Update focusable rectangles for the TV cursor
        updateFocusableRects();
    }

    // Ensure control bar visibility upon navigation or new tab activity
    private void ensureControlBarVisibleOnNavigation() {
        if (controlBarContainer == null) return;
        // Reset scroll/hide state so we don't accidentally hide on restored scroll
        resetControlBarScrollState();
        // Start suppression window so restored scroll doesn't hide the bar
        suppressScrollHideUntilMs = System.currentTimeMillis() + SUPPRESS_AFTER_NAV_MS;
        controlBarContainer.setVisibility(View.VISIBLE);
        // If previously expanded, keep expanded. Otherwise show minimized.
        if (isControlBarExpanded) {
            showExpandedBar();
        } else {
            showMinimizedBar();
        }
        // Do not allow a carry-over hidden state between pages
        isControlBarHidden = false;
        scrollTriggeredHideRequest = false;
    }

    // Reset scroll-related accumulators and flags
    private void resetControlBarScrollState() {
        accumulatedScrollDownPx = 0;
        accumulatedScrollUpPx = 0;
        lastScrollY = 0;
        mLastScrollY = 0;
        scrollTriggeredHideRequest = false;
    }

    // --- Simple slide animations for minimized control bar ---
    private void slideInFromBottom(View v) {
        int h = v.getHeight();
        if (h == 0) {
            // If height not measured yet, approximate with action bar size or 56dp fallback
            try {
                h = v.getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);
            } catch (Throwable t) {
                final float density = v.getResources().getDisplayMetrics().density;
                h = Math.max(1, Math.round(56 * density));
            }
        }
        TranslateAnimation anim = new TranslateAnimation(0, 0, h, 0);
        anim.setDuration(200);
        v.startAnimation(anim);
    }

    private void slideOutToBottom(View v, Animation.AnimationListener listener) {
        int h = v.getHeight();
        if (h == 0) {
            try {
                h = v.getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);
            } catch (Throwable t) {
                final float density = v.getResources().getDisplayMetrics().density;
                h = Math.max(1, Math.round(56 * density));
            }
        }
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, h);
        anim.setDuration(200);
        if (listener != null) anim.setAnimationListener(listener);
        v.startAnimation(anim);
    }

    // Method to apply runtime settings based on SharedPreferences
    private void setupDesktopModeSwitch() {
        // Set current state based on saved preference
        String currentMode = prefs.getString(PREF_USER_AGENT_MODE, DEFAULT_USER_AGENT_MODE);
        boolean isDesktopMode = currentMode.equals("desktop");
        panelDesktopModeSwitch.setChecked(isDesktopMode);
        
        // Set listener for changes (but don't apply immediately - wait for Apply button)
        panelDesktopModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Desktop mode switch changed to: " + isChecked);
            // Changes will be applied when user clicks Apply button
        });
    }

    private void initializeContextMenu() {
        LayoutInflater inflater = LayoutInflater.from(this);
        contextMenuView = inflater.inflate(R.layout.context_menu_layout, null);
        
        // Set up click listeners for menu items
        contextMenuView.findViewById(R.id.context_menu_open_new_tab).setOnClickListener(v -> {
            handleContextMenuAction("open_new_tab");
        });
        
        contextMenuView.findViewById(R.id.context_menu_open_background_tab).setOnClickListener(v -> {
            handleContextMenuAction("open_background_tab");
        });
        
        contextMenuView.findViewById(R.id.context_menu_copy_link).setOnClickListener(v -> {
            handleContextMenuAction("copy_link");
        });
        
        contextMenuView.findViewById(R.id.context_menu_share_link).setOnClickListener(v -> {
            handleContextMenuAction("share_link");
        });
        
        contextMenuView.findViewById(R.id.context_menu_download).setOnClickListener(v -> {
            handleContextMenuAction("download");
        });
        
        Log.d(TAG, "Context menu initialized");
    }

    private void showContextMenu(int screenX, int screenY, GeckoSession.ContentDelegate.ContextElement element) {
        if (contextMenuView == null) {
            Log.w(TAG, "Context menu view not initialized");
            return;
        }

        // Store the current context
        currentContextElement = element;
        currentContextUrl = element.linkUri;

        // Configure menu items based on element type
        View downloadOption = contextMenuView.findViewById(R.id.context_menu_download);
        if (element.type == GeckoSession.ContentDelegate.ContextElement.TYPE_IMAGE) {
            downloadOption.setVisibility(View.VISIBLE);
            currentContextUrl = element.srcUri != null ? element.srcUri : element.linkUri;
        } else {
            downloadOption.setVisibility(View.GONE);
        }

        // Create and show popup
        if (contextMenuPopup != null) {
            contextMenuPopup.dismiss();
        }

        contextMenuPopup = new PopupWindow(contextMenuView, 
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            true);
        
        contextMenuPopup.setOutsideTouchable(true);
        contextMenuPopup.setFocusable(true);

        // Convert screen coordinates to view coordinates
        int[] location = new int[2];
        geckoView.getLocationOnScreen(location);
        int x = screenX - location[0];
        int y = screenY - location[1];

        // Ensure menu doesn't go off screen
        contextMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int menuWidth = contextMenuView.getMeasuredWidth();
        int menuHeight = contextMenuView.getMeasuredHeight();
        
        if (x + menuWidth > geckoView.getWidth()) {
            x = geckoView.getWidth() - menuWidth;
        }
        if (y + menuHeight > geckoView.getHeight()) {
            y = y - menuHeight;
        }
        
        x = Math.max(0, x);
        y = Math.max(0, y);

        try {
            contextMenuPopup.showAtLocation(geckoView, Gravity.NO_GRAVITY, x, y);
            Log.d(TAG, "Context menu shown at (" + x + "," + y + ") for " + element.type + " element");
        } catch (Exception e) {
            Log.e(TAG, "Failed to show context menu", e);
        }
    }

    private void hideContextMenu() {
        if (contextMenuPopup != null && contextMenuPopup.isShowing()) {
            contextMenuPopup.dismiss();
            contextMenuPopup = null;
        }
        currentContextElement = null;
        currentContextUrl = null;
    }

    private void handleContextMenuAction(String action) {
        // Store URL before hiding menu to prevent race condition
        String urlToProcess = currentContextUrl;
        GeckoSession.ContentDelegate.ContextElement elementToProcess = currentContextElement;
        
        // Hide the context menu first
        hideContextMenu();
        
        if (urlToProcess == null) {
            Log.w(TAG, "No context URL available for action: " + action);
            return;
        }

        Log.d(TAG, "Handling context menu action: " + action + " for URL: " + urlToProcess);

        switch (action) {
            case "open_new_tab":
                createNewTab(urlToProcess, true); // Create and switch to new tab
                break;
                
            case "open_background_tab":
                createNewTab(urlToProcess, false); // Create but don't switch
                break;
                
            case "copy_link":
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Link URL", urlToProcess);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
                
            case "share_link":
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, urlToProcess);
                startActivity(Intent.createChooser(shareIntent, "Share link"));
                break;
                
            case "download":
                if (elementToProcess != null && 
                    elementToProcess.type == GeckoSession.ContentDelegate.ContextElement.TYPE_IMAGE) {
                    // Handle image download
                    handleImageDownload(urlToProcess);
                }
                break;
                
            default:
                Log.w(TAG, "Unknown context menu action: " + action);
        }
    }

    private void handleImageDownload(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(this, "Unable to download: Invalid image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a simple download using the existing download infrastructure
        try {
            // Create a WebResponse for the image
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                // Load the image URL in a way that triggers download
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                
                try {
                    startActivity(downloadIntent);
                } catch (Exception e) {
                    // If no app can handle it, show a message
                    Toast.makeText(this, "No app available to download image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error downloading image", e);
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserAgent(String mode) {
        String version = BuildConfig.VERSION_NAME;
        String geckoVersion = "129.0"; // Keep synchronized with GeckoView version
        
        switch (mode) {
            case "desktop":
                return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 IndicBrowser/" + version;
            case "mobile":
            default:
                // Use Android-style mobile user agent that Google recognizes
                return "Mozilla/5.0 (Linux; Android 14; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 IndicBrowser/" + version;
        }
    }

    private void applyUserAgentToSession(GeckoSession session, @Nullable String urlContext) { // urlContext can be used for very specific future overrides if needed
        if (session == null) return;

        String userAgentModeFromPrefs = prefs.getString(PREF_USER_AGENT_MODE, DEFAULT_USER_AGENT_MODE);
        
        String userAgent = getUserAgent(userAgentModeFromPrefs); // getUserAgent("mobile") or getUserAgent("desktop")
            session.getSettings().setUserAgentOverride(userAgent);
        Log.d(TAG, "Applied user agent string: [" + userAgent + "] (derived from mode: " + userAgentModeFromPrefs + ") to session for URL context: [" + (urlContext != null ? urlContext : "N/A") + "]");

        // Viewport logic:
        if (userAgentModeFromPrefs.equals("desktop")) {
            boolean fullDesktopModelEnabled = prefs.getBoolean(PREF_FULL_DESKTOP_MODEL_ENABLED, false);
            if (fullDesktopModelEnabled) {
                session.getSettings().setViewportMode(GeckoSessionSettings.VIEWPORT_MODE_DESKTOP);
                Log.d(TAG, "Viewport Setting: DESKTOP (Reason: UA mode 'desktop' AND Full Desktop Model is ON)");
            } else {
                // Desktop UA is set, but Full Desktop Model is OFF.
                // Per user feedback, DO NOT set viewport to mobile here. Let GeckoView/site handle it.
                // No explicit session.getSettings().setViewportMode(...) call in this sub-branch.
                Log.d(TAG, "Viewport Setting: NOT SET EXPLICITLY (Reason: UA mode 'desktop' AND Full Desktop Model is OFF - allowing auto-adjustment or site default)");
            }
        } else { // Mobile UA mode
            session.getSettings().setViewportMode(GeckoSessionSettings.VIEWPORT_MODE_MOBILE);
            Log.d(TAG, "Viewport Setting: MOBILE (Reason: UA mode 'mobile')");
        }
    }

    private void applyRuntimeSettings() {
        if (runtime == null) {
            Log.e(TAG, "applyRuntimeSettings: Runtime is null!");
            return;
        }
        Log.d(TAG, "Applying dynamic runtime settings...");

        GeckoRuntimeSettings settings = runtime.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setRemoteDebuggingEnabled(true); // Enable remote debugging

        // --- Apply User Agent Settings ---
        String userAgentMode = prefs.getString(PREF_USER_AGENT_MODE, DEFAULT_USER_AGENT_MODE);
        String userAgent = getUserAgent(userAgentMode);
        // Note: User agent is set on GeckoSessionSettings, not GeckoRuntimeSettings
        Log.d(TAG, "User agent prepared: " + userAgent + " (mode: " + userAgentMode + ")");

        ContentBlocking.Settings cbSettings = settings.getContentBlocking();

        // --- Apply Cookie Settings ---
        boolean cookiesEnabled = prefs.getBoolean(PREF_COOKIES_ENABLED, true); // Default true
        Log.d(TAG, "Applying dynamic Cookies enabled: " + cookiesEnabled);
        cbSettings.setCookieBehavior(
            cookiesEnabled ? ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS // Corrected constant
                           : ContentBlocking.CookieBehavior.ACCEPT_NONE);

        // --- Apply Ad Blocker Settings (Default/Implicit) ---
        // TrackingProtectionLevel is not explicitly set here to avoid potential API issues encountered before.
        // The default level or the one from a loaded config (if any) will be used.

        // --- Attempt to disable WebM/VP9 for MSE (Removed as setPreference is not available) ---
        // try {
        //     Log.d(TAG, "Attempting to set media.mediasource.webm.enabled to false");
        //     settings.setPreference("media.mediasource.webm.enabled", false); 
        //     Log.i(TAG, "Successfully called setPreference for media.mediasource.webm.enabled");
        // } catch (Exception e) {
        //     Log.e(TAG, "Failed to set media.mediasource.webm.enabled preference", e);
        // }

        // Apply the modified settings (ContentBlocking and JavaScript are set on the 'settings' object directly)
    }

    // --- Settings Panel Logic ---
    private void setupSettingsPanelListeners() {
        panelApplyButton.setOnClickListener(v -> applySettingsFromPanel());
        panelCancelButton.setOnClickListener(v -> hideSettingsPanel());

        // Optional: Hide panel if touch occurs outside of it (on GeckoView)
        geckoView.setOnTouchListener((view, motionEvent) -> {
            if (settingsPanelLayout.getVisibility() == View.VISIBLE) {
                // Check if touch is outside the panel bounds
                Rect panelRect = new Rect();
                settingsPanelLayout.getGlobalVisibleRect(panelRect);
                if (!panelRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
                    hideSettingsPanel(); // Hide without applying if touched outside
                }
            } 
            
            // Hide context menu if visible and user taps elsewhere
            if (contextMenuPopup != null && contextMenuPopup.isShowing() && 
                motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                hideContextMenu();
            }
            
            // Logic to hide keyboard on touch has been removed to prevent interfering with web content interactions.
            // GeckoView handles keyboard management natively.
            
            return false; // Allow event propagation
        });

        if (isTvDevice()) {
            // Create a single, reusable hover listener for our buttons.
            View.OnHoverListener hoverListener = (v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        v.setAlpha(0.7f);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        v.setAlpha(1.0f);
                        break;
                }
                return false;
            };

            // Apply the hover listener to all settings panel elements
            if (panelCookieSwitch != null) panelCookieSwitch.setOnHoverListener(hoverListener);
            if (panelAdBlockerSwitch != null) panelAdBlockerSwitch.setOnHoverListener(hoverListener);
            if (panelUBlockLayout != null) panelUBlockLayout.setOnHoverListener(hoverListener);
            if (panelImmersiveSwitch != null) panelImmersiveSwitch.setOnHoverListener(hoverListener);
            if (panelDesktopModeSwitch != null) panelDesktopModeSwitch.setOnHoverListener(hoverListener);
            if (panelFullDesktopModelSwitch != null) panelFullDesktopModelSwitch.setOnHoverListener(hoverListener);
            if (panelApplyButton != null) panelApplyButton.setOnHoverListener(hoverListener);
            if (panelCancelButton != null) panelCancelButton.setOnHoverListener(hoverListener);
        }
    }

    private void toggleSettingsPanel() {
        if (settingsPanelLayout.getVisibility() == View.GONE) {
            showSettingsPanel();
        } else {
            hideSettingsPanel(); // Hide without applying changes if toggled off
        }
    }

    // Method to show the settings panel
    private void showSettingsPanel() {
        settingsPanelLayout.setVisibility(View.VISIBLE);
        settingsPanelLayout.bringToFront();
        // Optionally, focus the first element for D-pad navigation
        if (!settingsPanelElements.isEmpty()) {
            settingsPanelElements.get(0).requestFocus();
        }
    }

    // Method to hide the settings panel
    private void hideSettingsPanel() {
                settingsPanelLayout.setVisibility(View.GONE);
        geckoView.requestFocus(); // Return focus to GeckoView
    }

    private void applySettingsFromPanel() {
        Log.d(TAG, "applySettingsFromPanel called");
        SharedPreferences.Editor editor = prefs.edit();

        boolean cookiesEnabled = panelCookieSwitch.isChecked();
        boolean adBlockerEnabled = panelAdBlockerSwitch.isChecked();
        boolean uBlockEnabled = panelUBlockSwitch.isChecked();
        boolean immersiveModeEnabled = panelImmersiveSwitch.isChecked();
        // Get the NEW desktop mode state from the switch
        boolean newDesktopModeStateFromSwitch = panelDesktopModeSwitch.isChecked(); // true for desktop, false for mobile
        boolean fullDesktopModelEnabled = panelFullDesktopModelSwitch.isChecked();

        // Determine the old user agent mode to see if it changed
        String oldUserAgentModeFromPrefs = prefs.getString(PREF_USER_AGENT_MODE, DEFAULT_USER_AGENT_MODE);
        boolean oldDesktopModeState = oldUserAgentModeFromPrefs.equals("desktop");


        String newUserAgentMode = newDesktopModeStateFromSwitch ? "desktop" : "mobile";
        editor.putString(PREF_USER_AGENT_MODE, newUserAgentMode); // Save the new mode ("desktop" or "mobile")
        // Also update the boolean pref "desktop_mode_enabled" if it's used elsewhere for quick checks, for consistency.
        editor.putBoolean("desktop_mode_enabled", newDesktopModeStateFromSwitch);


        editor.putBoolean(PREF_COOKIES_ENABLED, cookiesEnabled);
        editor.putBoolean(PREF_AD_BLOCKER_ENABLED, adBlockerEnabled); // Standard blocker
        editor.putBoolean(PREF_UBLOCK_ENABLED, uBlockEnabled);         // uBlock Origin
        editor.putBoolean(PREF_IMMERSIVE_MODE_ENABLED, immersiveModeEnabled);
        // editor.putBoolean("desktop_mode_enabled", newDesktopModeStateFromSwitch); // Already set above
        editor.putBoolean(PREF_FULL_DESKTOP_MODEL_ENABLED, fullDesktopModelEnabled);

        editor.apply();

        // Apply runtime settings that don't require session recreation
        applyRuntimeSettings(); // This handles cookie settings, content blocking

        // Apply immersive mode
        applyImmersiveMode(immersiveModeEnabled);

        // Apply User Agent to all existing sessions
        Log.d(TAG, "Settings Panel: Applying UA based on new mode: " + newUserAgentMode + " to all sessions.");
        for (GeckoSession existingSession : geckoSessionList) {
            applyUserAgentToSession(existingSession, sessionUrlMap.containsKey(existingSession) ? sessionUrlMap.get(existingSession) : "N/A"); // Pass URL for context
        }

        // Reload the active session to apply changes.
        GeckoSession activeSession = getActiveSession();
        if (activeSession != null) {
            Log.d(TAG, "Settings Panel: Reloading active session to apply settings.");
            activeSession.reload();
        } else {
            Log.w(TAG, "Settings Panel: No active session to reload after applying settings.");
        }

        hideSettingsPanel();
    }

    // --- uBlock Origin WebExtension Logic ---

    private void initializeUBlockOrigin() {
        Log.i(TAG, "initializeUBlockOrigin called.");
        if (runtime == null) {
            Log.e(TAG, "GeckoRuntime not initialized. Cannot install uBlock Origin.");
            return;
        }
        Log.i(TAG, "GeckoRuntime is available for uBlock Origin initialization.");

        WebExtensionController controller = runtime.getWebExtensionController();
        if (controller == null) {
            Log.e(TAG, "WebExtensionController is null. Cannot install uBlock Origin.");
            return;
        }
        Log.i(TAG, "WebExtensionController is available for uBlock Origin initialization.");

        if (ublockOriginExtension != null) {
            boolean currentKnownState = prefs.getBoolean(PREF_UBLOCK_ENABLED, true); // Default to true
            Log.i(TAG, "uBlock Origin extension object already exists. ID: " + ublockOriginExtension.id + ", Known Enabled State: " + currentKnownState + ". Ensuring state matches preference.");
            // setUBlockOriginEnabled(currentKnownState); // This will also update the switch via its own callbacks.
            // Call directly to set the switch and potentially enable/disable if needed,
            // but avoid re-install if ublockOriginExtension is already present.
            if (panelUBlockSwitch != null) {
                panelUBlockSwitch.setChecked(currentKnownState);
            }
            // If the current state of the extension object (if we could reliably get it) differs from preference,
            // then call setUBlockOriginEnabled. For now, assume ensureBuiltIn + set will align it.
            // The main purpose here is to initialize the switch and ensure the extension is loaded if already known.
            // Actual enable/disable based on pref happens after installation below if object is null, or if user toggles switch.
            return;
        }

        // If uBlockInstallAttempted is true AND ublockOriginExtension is null, it means a previous attempt failed.
        // We might only want to retry if this method is called due to a direct user action (like toggling the switch).
        // For onCreate, if it failed once, it might keep failing.
        // For now, always attempt if ublockOriginExtension is null, as the call might come from a settings change.
        Log.i(TAG, "uBlock Origin extension object is null. Attempting installation. Prior attempt: " + uBlockInstallAttempted);

        // Validate assets to avoid JSON.parse failures when Git LFS pointers are present
        if (!isUblockAssetsValid()) {
            Log.e(TAG, "uBlock Origin assets look invalid (likely Git LFS pointer files). Skipping installation. Please fetch real assets.");
            Toast.makeText(MainActivity.this, "uBlock Origin assets missing. Run 'git lfs pull' and rebuild.", Toast.LENGTH_LONG).show();
            if (panelUBlockSwitch != null) panelUBlockSwitch.setChecked(false);
            uBlockInstallAttempted = true;
            return;
        }

        Log.i(TAG, "Attempting to ensureBuiltIn uBlock Origin from: " + UBLOCK_ASSET_PATH + " with ID: " + UBLOCK_EXTENSION_ID);

        controller.ensureBuiltIn(UBLOCK_ASSET_PATH, UBLOCK_EXTENSION_ID)
            .accept(
                extension -> {
                    uBlockInstallAttempted = true; // Mark that an attempt has been made
                    if (extension != null) {
                        this.ublockOriginExtension = extension;
                        Log.i(TAG, "uBlock Origin (" + extension.id + ") installed/ensured successfully. Manifest version: " +
                                   (extension.metaData != null ? " (Not directly queryable)" : "N/A") + // manifestVersion not directly available
                                   ", Initializing based on preference.");

                        boolean enableUBlockPreference = prefs.getBoolean(PREF_UBLOCK_ENABLED, true);
                        Log.i(TAG, "uBlock Origin preference is: " + (enableUBlockPreference ? "ENABLED" : "DISABLED") + ". Aligning extension state.");
                        setUBlockOriginEnabled(enableUBlockPreference); // This will handle enabling/disabling and updating the switch
                    } else {
                        Log.e(TAG, "ensureBuiltIn for uBlock Origin returned NULL WebExtension object. Path: " + UBLOCK_ASSET_PATH + ", ID: " + UBLOCK_EXTENSION_ID);
                        Toast.makeText(MainActivity.this, "Failed to install uBlock Origin (returned null)", Toast.LENGTH_LONG).show();
                        if (panelUBlockSwitch != null) {
                            runOnUiThread(() -> panelUBlockSwitch.setChecked(false)); // Reflect failure
                        }
                    }
                },
                e -> {
                    uBlockInstallAttempted = true; // Mark that an attempt has been made
                    Log.e(TAG, "ensureBuiltIn for uBlock Origin FAILED. Path: " + UBLOCK_ASSET_PATH + ", ID: " + UBLOCK_EXTENSION_ID, e);
                    Toast.makeText(MainActivity.this, "Failed to install uBlock Origin (exception)", Toast.LENGTH_LONG).show();
                    if (panelUBlockSwitch != null) {
                        runOnUiThread(() -> panelUBlockSwitch.setChecked(false)); // Reflect failure
                    }
                }
            );
    }

    private void setUBlockOriginEnabled(boolean enabled) {
        Log.i(TAG, "setUBlockOriginEnabled called with requested state: " + (enabled ? "ENABLE" : "DISABLE"));

        if (runtime == null || runtime.getWebExtensionController() == null) {
            Log.e(TAG, "GeckoRuntime or WebExtensionController not available in setUBlockOriginEnabled.");
            Toast.makeText(MainActivity.this, "Ad blocking service unavailable.", Toast.LENGTH_SHORT).show();
            if (panelUBlockSwitch != null) {
                panelUBlockSwitch.setChecked(false); // Cannot change state
            }
            return;
        }

        if (ublockOriginExtension == null) {
            Log.w(TAG, "uBlock Origin extension object is null in setUBlockOriginEnabled.");
            if (enabled) {
                Log.i(TAG, "Attempting to initialize uBlock Origin as it's being enabled but object is null.");
                // This will call initializeUBlockOrigin, which in turn will call setUBlockOriginEnabled again after installation.
                // Need to be careful about potential loops if installation fails repeatedly.
                // initializeUBlockOrigin has uBlockInstallAttempted to somewhat mitigate this.
                // Also, ensure the switch reflects the desired 'true' state if we are trying to enable.
                if (panelUBlockSwitch != null) {
                    panelUBlockSwitch.setChecked(true);
                }
                initializeUBlockOrigin();
            } else {
                // If trying to disable and it's null, it's effectively disabled.
                Log.i(TAG, "uBlock Origin is already effectively disabled (extension object is null).");
                if (panelUBlockSwitch != null) {
                    panelUBlockSwitch.setChecked(false);
                }
            }
            return;
        }

        // Log based on the 'enabled' parameter which is the *target* state for this operation.
        // We cannot reliably call ublockOriginExtension.isEnabled() directly.
        Log.i(TAG, "uBlock Origin (" + ublockOriginExtension.id + ") current known/intended state before change: " + prefs.getBoolean(PREF_UBLOCK_ENABLED, enabled) + ", Attempting to set to: " + enabled);

        WebExtensionController controller = runtime.getWebExtensionController();
        GeckoResult<WebExtension> result;

        if (enabled) {
            Log.i(TAG, "Calling WebExtensionController.enable for uBlock Origin ID: " + ublockOriginExtension.id);
            result = controller.enable(ublockOriginExtension, WebExtensionController.EnableSource.USER);
        } else {
            Log.i(TAG, "Calling WebExtensionController.disable for uBlock Origin ID: " + ublockOriginExtension.id);
            result = controller.disable(ublockOriginExtension, WebExtensionController.EnableSource.USER);
        }

        result.accept(
            updatedExtension -> {
                if (updatedExtension != null) {
                    this.ublockOriginExtension = updatedExtension; // Update with the returned object
                    // Log based on the 'enabled' parameter which was the target of the operation
                    Log.i(TAG, "uBlock Origin (" + updatedExtension.id + ") enable/disable call successful. Attempted state: " + (enabled ? "enabled" : "disabled") +
                               ". Actual state after call should reflect this."); // Cannot call updatedExtension.isEnabled()
                    if (panelUBlockSwitch != null) {
                        runOnUiThread(() -> {
                            panelUBlockSwitch.setChecked(enabled); // Set switch to the state we attempted
                            Log.d(TAG, "uBlock switch updated to: " + enabled);
                        });
                    }
                     // Persist the successful state change
                    prefs.edit().putBoolean(PREF_UBLOCK_ENABLED, enabled).apply();
                } else {
                     Log.w(TAG, "uBlock Origin enable/disable call returned null WebExtension object. Intended state: " + (enabled ? "enabled" : "disabled"));
                     // Attempt to reflect the last known good state of the switch if available
                    if (panelUBlockSwitch != null) {
                        runOnUiThread(() -> panelUBlockSwitch.setChecked(prefs.getBoolean(PREF_UBLOCK_ENABLED, false))); // Revert to preference
                    }
                }
            },
            e -> {
                Log.e(TAG, "Failed to set uBlock Origin (" + ublockOriginExtension.id + ") target enabled state to " + enabled, e);
                Toast.makeText(MainActivity.this, "Failed to " + (enabled ? "enable" : "disable") + " uBlock Origin", Toast.LENGTH_SHORT).show();
                // Revert switch to previous known state (from prefs)
                if (panelUBlockSwitch != null) {
                    runOnUiThread(() -> {
                        panelUBlockSwitch.setChecked(prefs.getBoolean(PREF_UBLOCK_ENABLED, false)); // Revert to preference
                        Log.d(TAG, "uBlock switch reverted to: " + prefs.getBoolean(PREF_UBLOCK_ENABLED, false) + " after failure.");
                    });
                }
            }
        );
    }

    // --- End uBlock Origin WebExtension Logic ---

    // --- Video Detection ---
    /**
     * Checks if the current fullscreen content is a video.
     * This is a basic implementation - may need refinement based on actual content.
     */
    private boolean isFullscreenVideo() {
        // Check if we're in fullscreen and have an active media session
        boolean isVideo = isInGeckoViewFullscreen && mediaPlayingSession != null;
        Log.d(TAG, "isFullscreenVideo: " + isVideo);
        return isVideo;
    }

    // --- Cursor Auto-hide Integration ---
    private void updateCursorAutoHide() {
        if (tvCursorView == null) {
            Log.w(TAG, "updateCursorAutoHide: tvCursorView is null");
            return;
        }
        
        boolean shouldAutoHide = isFullscreenVideo();
        tvCursorView.setAutoHideEnabled(shouldAutoHide);
        
        if (shouldAutoHide) {
            tvCursorView.showCursor(); // Show cursor when entering video mode
        } else {
            tvCursorView.showCursor(); // Ensure cursor is visible when not in video mode
        }
        Log.d(TAG, "Cursor auto-hide " + (shouldAutoHide ? "enabled" : "disabled"));
    }

    // --- Fullscreen Helper Methods ---
    private void enterFullScreen() {
        Log.d(TAG, "Entering fullscreen (enterFullScreen called)");
        isInGeckoViewFullscreen = true; // GeckoView has requested fullscreen

        // Keep the screen on to prevent video from pausing
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View decorView = getWindow().getDecorView();
        // Hide navigation and status bars with stable layout to prevent surface recreation
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // Hide app-specific UI
        if (controlBarContainer != null) {
            controlBarContainer.setVisibility(View.GONE);
        }
        
        // Update cursor auto-hide state
        updateCursorAutoHide();

        // On mobile, lock orientation
        if (!isTvDevice()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void exitFullScreen() {
        Log.d(TAG, "Exiting fullscreen (exitFullScreen called)");
        isInGeckoViewFullscreen = false; // GeckoView no longer desires fullscreen

        // Allow the screen to turn off again
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View decorView = getWindow().getDecorView();
        // Show navigation and status bars
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Show app-specific UI
        if (controlBarContainer != null) {
            controlBarContainer.setVisibility(View.VISIBLE);
        }
        
        // Update cursor auto-hide state
        updateCursorAutoHide();

        // On mobile, unlock orientation
        if (!isTvDevice()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            if (isControlBarExpanded) {
                showExpandedBar();
            } else {
                showMinimizedBar();
            }
        }
    }

    private File copyGeckoConfigFromAssets() {
        File configFile = new File(getCacheDir(), "geckoview-config.yaml");
        try (InputStream inputStream = getAssets().open("geckoview-config.yaml");
             OutputStream outputStream = new FileOutputStream(configFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            Log.i(TAG, "Successfully copied geckoview-config.yaml to cache.");
            return configFile;
        } catch (Exception e) {
            Log.e(TAG, "Error copying geckoview-config.yaml from assets", e);
            return null;
        }
    }
    // --- End Fullscreen Helper Methods ---

    // Ensure system UI visibility is reset if the user manually leaves fullscreen (e.g., back button)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Re-apply the immersive mode setting every time the window gains focus,
            // as system might change UI flags when app is backgrounded/foregrounded.
            // This is especially important for maintaining the desired immersive state.
            Log.d(TAG, "onWindowFocusChanged(true): Re-applying immersive mode. Current switch state: " + panelImmersiveSwitch.isChecked());
            applyImmersiveMode(panelImmersiveSwitch.isChecked());
            
            // The firstWindowFocus logic can be removed if the above handles it, 
            // but keeping it for one initial explicit call after focus might still be beneficial.
            if (firstWindowFocus) {
                Log.d(TAG, "onWindowFocusChanged(true): First window focus, explicitly re-applying immersive mode.");
                applyImmersiveMode(panelImmersiveSwitch.isChecked());
                firstWindowFocus = false;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called with intent: " + intent);
        setIntent(intent); // Important to update the activity's intent
        handleTorrentIntent(intent);
        String action = intent.getAction();
        Uri data = intent.getData();
        String type = intent.getType(); // For ACTION_SEND

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String url = data.toString();
            Log.i(TAG, "Received VIEW intent in onNewIntent with URL: " + url);
            if (url != null && !url.isEmpty()) {
                createNewTab(url, true);
            }
        } else if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.i(TAG, "Received SEND intent with text/plain: " + sharedText);
                if (sharedText != null) {
                    if (sharedText.startsWith("http://") || sharedText.startsWith("https://")) {
                        createNewTab(sharedText, true);
                    } else {
                        // Not a URL, treat as search query
                        try {
                            String searchQuery = buildSearchUrl(java.net.URLEncoder.encode(sharedText, "UTF-8"));
                            createNewTab(searchQuery, true);
                        } catch (java.io.UnsupportedEncodingException e) {
                            Log.e(TAG, "Failed to encode search query: " + sharedText, e);
                            // Fallback or show error
                        }
                    }
                }
            }
        } else if (Intent.ACTION_WEB_SEARCH.equals(action)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received WEB_SEARCH intent with query: " + query);
            if (query != null && !query.isEmpty()) {
                try {
                    String searchQuery = buildSearchUrl(java.net.URLEncoder.encode(query, "UTF-8"));
                    createNewTab(searchQuery, true);
                } catch (java.io.UnsupportedEncodingException e) {
                    Log.e(TAG, "Failed to encode web search query: " + query, e);
                }
            }
        }
        
        // Bring the task to the front to ensure the user sees the browser
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                final List<ActivityManager.AppTask> tasks = am.getAppTasks();
                if (tasks != null && !tasks.isEmpty()) {
                    for (ActivityManager.AppTask task : tasks) {
                        if (task.getTaskInfo().baseIntent != null && task.getTaskInfo().baseIntent.getComponent() != null && 
                            task.getTaskInfo().baseIntent.getComponent().getClassName().equals(this.getClass().getName())) {
                            Log.d(TAG, "Bringing task to front: " + task.getTaskInfo().id);
                            task.moveToFront();
                            break;
                        }
                    }
                }
            }
        }
    }

    // Method to launch the Tab Switcher
    private void launchTabSwitcher() {
        // --- Capture snapshot of the CURRENT tab before launching switcher --- (REMOVED - should be up-to-date from onPageStop)
        // GeckoSession currentSession = getActiveSession();
        // if (currentSession != null) {
        //      captureSnapshot(currentSession); // Ensure latest snapshot is taken
        // }
        // --- End Snapshot Capture ---
        
        Intent intent = new Intent(this, TabSwitcherActivity.class);
        
        // Prepare data to send
        ArrayList<String> urls = new ArrayList<>();
        ArrayList<String> snapshotBase64Strings = new ArrayList<>(); // Send as Base64 strings (still potentially large!)
        
        for (GeckoSession session : geckoSessionList) {
            urls.add(sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "Loading..."); 
            Bitmap snapshot = sessionSnapshotMap.get(session);
            if (snapshot != null) {
                 // Convert Bitmap to Base64 String (Example - adjust quality/size)
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 snapshot.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Compress heavily
                 byte[] byteArray = baos.toByteArray();
                 snapshotBase64Strings.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
            } else {
                 snapshotBase64Strings.add(null); // Add null placeholder if no snapshot
            }
        }
        intent.putStringArrayListExtra(TabSwitcherActivity.EXTRA_TAB_URLS, urls);
        intent.putExtra(TabSwitcherActivity.EXTRA_ACTIVE_TAB_INDEX, activeSessionIndex);
        intent.putStringArrayListExtra("EXTRA_TAB_SNAPSHOTS", snapshotBase64Strings); // Add snapshot data
        
        // ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
        //     this,
        //     Pair.create(findViewById(R.id.tabsButton), "fab_transition")
        // );
        // tabSwitcherLauncher.launch(intent, options); // Launch with Compat options
        tabSwitcherLauncher.launch(intent); // Launch without transition options for testing
    }

    // Add pause/resume handling:
    // @Override // REMOVE
    // protected void onPause() {
    //     super.onPause();
    //     if (isFinishing()) {
    //         clearAllSnapshots();
    //     }
    // }

    // private void clearAllSnapshots() { // REMOVE
    //     synchronized (geckoSessionList) { // Synchronize access if modifying map concurrently
    //         for (SoftReference<Bitmap> bitmapRef : sessionSnapshotMap.values()) {
    //             if (bitmapRef != null) {
    //                 Bitmap bitmap = bitmapRef.get();
    //                 if (bitmap != null && !bitmap.isRecycled()) { // Check if bitmap exists and isn't already recycled
    //                     bitmap.recycle();
    //                 }
    //             }
    //         }
    //         sessionSnapshotMap.clear();
    //         totalSnapshotMemory = 0; // Reset memory count
    //     }
    // }

    // --- Session Persistence Methods ---
    @Override
    protected void onStart() {
        super.onStart();
        // Ensure the active GeckoSession is attached and active when Activity starts
        GeckoSession active = getActiveSession();
        if (active != null) {
            if (geckoView.getSession() != active) {
                Log.d(TAG, "onStart: Re-attaching active session to GeckoView");
                geckoView.setSession(active);
            }
            try {
                Log.d(TAG, "onStart: Setting active session active=true");
                active.setActive(true);
            } catch (Throwable t) {
                Log.e(TAG, "Failed to activate GeckoSession onStart", t);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On resume, ensure the active session is attached and active (no pixel probing)
        resumeAtMs = System.currentTimeMillis();
        resumeHadProgress = false;
        resumeRecoveryAttempted = false;
        tvBlankBounceRecoveryAttempted = false;
        boolean isFirstResume = !hasEverResumed;
        hasEverResumed = true;
        // Keep screen awake during browsing sessions
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        GeckoSession active = getActiveSession();
        if (active != null) {
            // For TV devices after sleep/wake, always re-attach to recover from compositor detachment
            if (isTvDevice() && !isFirstResume) {
                Log.d(TAG, "onResume: TV device - forcing session re-attachment to recover from sleep");
                try {
                    geckoView.releaseSession();
                } catch (Throwable t) {
                    Log.w(TAG, "onResume: Failed to release session", t);
                }
                geckoView.setSession(active);
                updateUIForActiveSession();
            } else if (geckoView.getSession() != active) {
                Log.d(TAG, "onResume: Re-attaching active session to GeckoView");
                geckoView.setSession(active);
                updateUIForActiveSession();
            }
            try {
                Log.d(TAG, "onResume: Ensuring active session active=true");
                active.setActive(true);
            } catch (Throwable t) {
                Log.e(TAG, "Failed to activate GeckoSession onResume", t);
            }
        }
        // TV-only: after a short delay, if we still have no progress and the page paint looks stalled,
        // attempt a gentle reload of the active session using the last known http(s) URL.
        if (isTvDevice() && !isFirstResume) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isFinishing()) return;
                        if (!isTvDevice()) return;
                        if (resumeRecoveryAttempted) {
                            Log.d(TAG, "[ResumeWatchdog] Skipping: recovery already attempted for this resume");
                            return;
                        }
                        resumeRecoveryAttempted = true;
                        if (isPagePaintLikelyStalled()) {
                            Log.w(TAG, "[ResumeWatchdog] TV resume stall detected; attempting gentle reload");
                            maybeReloadActiveIfStuck();
                        } else {
                            Log.d(TAG, "[ResumeWatchdog] TV resume: no stall detected; no reload needed");
                        }
                    } catch (Throwable t) {
                        Log.w(TAG, "[ResumeWatchdog] TV resume recovery threw", t);
                    }
                }
            }, 1500);
        }
    }

    // performSingleResumeRecovery() removed; no resume-time probing or forced recovery

    // Removed verifyAndRecoverRenderingAfterResume(): not needed with minimal lifecycle handling.

    @Override
    protected void onPause() {
        super.onPause();
        // Allow the screen to turn off when app not in foreground
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Capture snapshot to have an up-to-date overlay on next launch
        GeckoSession active = getActiveSession();
        if (active != null) {
            try {
                Log.d(TAG, "onPause: Capturing snapshot of active session before deactivation");
                captureSnapshot(active);
            } catch (Throwable t) {
                Log.w(TAG, "onPause: Failed to capture snapshot", t);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFinishing()) {
            saveSessionState();
        }
        // Deactivate the active GeckoSession when Activity stops to free resources
        GeckoSession active = getActiveSession();
        if (active != null) {
            try {
                Log.d(TAG, "onStop: Setting active session active=false");
                active.setActive(false);
            } catch (Throwable t) {
                Log.e(TAG, "Failed to deactivate GeckoSession onStop", t);
            }
        }
    }

    private void saveSessionState() {
        Log.d(TAG, "[StorageDebug] saveSessionState CALLED");
        SharedPreferences.Editor editor = prefs.edit();

        // Save URLs as a JSON array string
        JSONArray urlsJsonArray = new JSONArray();
        for (GeckoSession session : geckoSessionList) {
            urlsJsonArray.put(sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "about:blank");
        }
        String urlsJsonString = urlsJsonArray.toString();
        Log.d(TAG, "[StorageDebug] Saving URLs JSON: " + urlsJsonString);
        editor.putString(PREF_SAVED_URLS_JSON_ARRAY, urlsJsonString);

        // Clamp activeSessionIndex to valid range before saving
        int clampedActiveIndex = activeSessionIndex;
        if (clampedActiveIndex < 0 || clampedActiveIndex >= geckoSessionList.size()) {
            clampedActiveIndex = geckoSessionList.isEmpty() ? -1 : 0;
        }
        editor.putInt(PREF_ACTIVE_INDEX, clampedActiveIndex);
        Log.d(TAG, "[StorageDebug] Saving active index: " + clampedActiveIndex);

        editor.apply(); // Use apply() for asynchronous saving
        Log.d(TAG, "[StorageDebug] Session state saved. Tabs: " + urlsJsonArray.length() + ", Active Index: " + clampedActiveIndex);
    }

    private boolean restoreSessionState() {
        Log.d(TAG, "[StorageDebug] restoreSessionState CALLED");
        String savedUrlsJsonString = prefs.getString(PREF_SAVED_URLS_JSON_ARRAY, null); // Changed key
        int savedActiveIndex = prefs.getInt(PREF_ACTIVE_INDEX, -1);

        Log.d(TAG, "[StorageDebug] Found saved URLs JSON: " + savedUrlsJsonString + ", Saved Active Index: " + savedActiveIndex);

        if (savedUrlsJsonString == null || savedUrlsJsonString.isEmpty()) {
            Log.d(TAG, "[StorageDebug] No saved URLs JSON found.");
            return false;
        }

        List<String> savedUrlsList = new ArrayList<>();
        try {
            JSONArray urlsJsonArray = new JSONArray(savedUrlsJsonString);
            for (int i = 0; i < urlsJsonArray.length(); i++) {
                savedUrlsList.add(urlsJsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(TAG, "[StorageDebug] Failed to parse saved URLs JSON", e);
            return false;
        }

        if (savedUrlsList.isEmpty()) {
            Log.d(TAG, "[StorageDebug] Parsed URLs list is empty.");
            return false;
        }

        Log.d(TAG, "[StorageDebug] Found saved state. URLs: " + savedUrlsList.size() + ", Active Index: " + savedActiveIndex);

        // Clear any potentially existing (should be empty) sessions before restoring
        geckoSessionList.clear();
        sessionUrlMap.clear();
        sessionSnapshotMap.clear(); // Snapshots are not persisted
        sessionSnapshotUrlMap.clear();
        activeSessionIndex = -1;

        // Recreate sessions - NOTE: Order might not be preserved perfectly with Set
        // If order is critical, saving URLs as a JSON array string would be better.
        int restoredIndex = 0;
        for (String url : savedUrlsList) { // Iterate over the parsed list
            // Create tabs but don't switch view yet
            Log.d(TAG, "[StorageDebug] Restoring tab: " + url);
            createNewTab(url, false); // This will add the session to geckoSessionList and sessionUrlMap
            
            // --- Attempt to load snapshot from disk ---
            GeckoSession justCreatedSession = geckoSessionList.get(geckoSessionList.size() - 1); // Get the session that was just added
            Bitmap loadedSnapshot = loadSnapshotFromDisk(url);
            if (loadedSnapshot != null) {
                sessionSnapshotMap.put(justCreatedSession, loadedSnapshot);
                sessionSnapshotUrlMap.put(justCreatedSession, url);
                Log.d(TAG, "[StorageDebug] Loaded snapshot from disk for: " + url);
            } else {
                Log.d(TAG, "[StorageDebug] No snapshot found on disk for: " + url);
            }
            // --- End attempt to load snapshot ---

            restoredIndex++;
        }

        // Validate and set the active index
        if (savedActiveIndex >= 0 && savedActiveIndex < geckoSessionList.size()) {
            activeSessionIndex = savedActiveIndex;
        } else if (!geckoSessionList.isEmpty()) {
            Log.w(TAG, "[StorageDebug] Saved active index invalid, defaulting to 0.");
            activeSessionIndex = 0; // Default to first tab if index invalid
        } else {
             Log.e(TAG, "[StorageDebug] Error: Restored sessions but list is empty?");
             return false; // Indicate failure
        }
        Log.d(TAG, "[StorageDebug] Session state restored. Active index set to: " + activeSessionIndex);
        // The session will be set to GeckoView later in onCreate
        return true;
    }
    // --- End Session Persistence Methods ---

    // Cache-first helpers removed; standard loads are used exclusively

    // Add this method to setup the listener
    private void setupGeckoViewTouchListener() {
        geckoView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 1. Tap-to-show minimized bar logic
                if (isControlBarHidden) {
                    // Check if the tap is near the bottom edge of the GeckoView
                    // (remembering the control bar is at the bottom of the screen)
                    float tapY = event.getY(); // Y-coordinate relative to GeckoView
                    int geckoViewHeight = geckoView.getHeight();

                    // If GeckoView height is 0, we can't make a reliable calculation yet.
                    if (geckoViewHeight > 0 && (geckoViewHeight - tapY) < tapThresholdBottomEdgePx) {
                        showMinimizedBar();
                        Log.d(TAG, "GeckoView tap near bottom edge detected while hidden, showing minimized bar.");
                        // Potentially consume this touch so it doesn't also interact with web content immediately
                        // return true; 
                    }
                }

                // 2. Existing keyboard hiding logic
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus != urlBar) {
                    Log.d(TAG, "Touch on GeckoView detected, hiding keyboard (current focus: " + currentFocus.getClass().getSimpleName() + ")");
                    hideKeyboard();
                } else if (currentFocus == null) {
                    Log.d(TAG, "Touch on GeckoView detected (no focus), hiding keyboard.");
                    hideKeyboard();
                }
            }
            // Return false so GeckoView still processes the touch for scrolling, clicking links etc.
            // if the tap-to-show was not triggered and consumed.
            return false;
        });
    }

    // --- Download Handling ---
    private void handleDownloadResponse(org.mozilla.geckoview.WebResponse response) {
        // Check storage permission (legacy behavior for SDK <= 28). On Android 10+ scoped storage, no runtime write permission is required for DownloadManager to public Downloads.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Storage permission (legacy) not granted. Requesting...");
                // Store the response to retry after permission grant
                pendingDownloadResponse = response;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
                // Don't proceed with download logic yet
                return;
            }
        }
        
        Log.i(TAG, "Storage permission granted. Proceeding with download.");
        
        // Clear any pending response as we are proceeding now
        pendingDownloadResponse = null;

        String url = response.uri;
        // Case-insensitive header lookup
        String contentDisposition = null;
        String mimeType = null;
        if (response.headers != null) {
            for (Map.Entry<String, String> e : response.headers.entrySet()) {
                final String k = e.getKey();
                if (k == null) continue;
                if ("content-type".equalsIgnoreCase(k)) mimeType = e.getValue();
                else if ("content-disposition".equalsIgnoreCase(k)) contentDisposition = e.getValue();
            }
        }
        if (mimeType == null || mimeType.isEmpty()) {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (fileExtension != null && !fileExtension.isEmpty()) {
                String byExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                if (byExt != null && !byExt.isEmpty()) mimeType = byExt;
            }
        }

        // If Content-Disposition provides a filename, prefer it
        String fileNameFromCd = null;
        if (contentDisposition != null) {
            try {
                // Common patterns: filename="name.mp4"; filename*=UTF-8''name.mp4
                java.util.regex.Matcher mStar = java.util.regex.Pattern.compile("filename\\*\\s*=\\s*([^']*)''([^;]+)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(contentDisposition);
                if (mStar.find()) {
                    fileNameFromCd = java.net.URLDecoder.decode(mStar.group(2), java.nio.charset.StandardCharsets.UTF_8.name());
                } else {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("filename\\s*=\\s*\"?([^\";]+)\"?", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(contentDisposition);
                    if (m.find()) fileNameFromCd = m.group(1);
                }
            } catch (Exception ignore) {}
        }

        String fileName = (fileNameFromCd != null && !fileNameFromCd.isEmpty())
                ? fileNameFromCd
                : URLUtil.guessFileName(url, contentDisposition, mimeType);

        // Intercept .torrent files
        if ("application/x-bittorrent".equalsIgnoreCase(mimeType) || 
            (fileName != null && fileName.toLowerCase().endsWith(".torrent"))) {
             Log.i(TAG, "Intercepted .torrent file: " + fileName);
             final String finalFileName = fileName;
             runOnUiThread(() -> showTorrentFileDecisionDialog(url, finalFileName, response.headers));
             return;
        }

        // Fallback if guessFileName returns something generic or empty
        if (fileName == null || fileName.isEmpty() || fileName.equals("downloadfile") || fileName.equals("dat") || fileName.endsWith(".bin")) {
            Log.w(TAG, "URLUtil.guessFileName returned a generic/empty name: " + fileName + ". Attempting to derive from URL path.");
            if (url != null) {
                Uri parsedUri = Uri.parse(url);
                String path = parsedUri.getPath();
                if (path != null && !path.isEmpty()) {
                    String lastSegment = parsedUri.getLastPathSegment();
                    if (lastSegment != null && !lastSegment.isEmpty()) {
                        fileName = lastSegment;
                    } else {
                        fileName = "downloaded_file"; // Ultimate fallback
                    }
                } else {
                    fileName = "downloaded_file"; // Ultimate fallback
                }
            } else {
                fileName = "downloaded_file"; // Ultimate fallback
            }
             Log.d(TAG, "Fallback filename derived: " + fileName);
        }

        // If the filename has no extension or a generic one, fix it based on MIME
        if (mimeType != null && !mimeType.isEmpty()) {
            String fixed = ensureExtensionForMime(fileName, mimeType);
            if (fixed != null) fileName = fixed;
        }

        // Consider using ContentResolver and MediaStore for more robust filename/path generation on newer Android versions
        String destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filePath = destDir + "/" + fileName;

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName);
            request.setDescription("Downloading file...");
            // Use setDestinationInExternalPublicDir for standard download location
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner(); // Let media scanner pick it up
            if (mimeType != null && !mimeType.isEmpty()) {
                try { request.setMimeType(mimeType); } catch (Throwable t) { /* ignore */ }
            }
            
            // Set headers from the response if needed (e.g., cookies)
            if (response.headers != null) {
                 for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                     // Example: Add Cookie header if present in response headers
                     if (entry.getKey().equalsIgnoreCase("cookie")) { 
                          request.addRequestHeader(entry.getKey(), entry.getValue());
                     } // Add other relevant headers if necessary
                 }
            }

            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (dm != null) {
                long downloadId = dm.enqueue(request); // Get the download ID
                Log.i(TAG, "Download enqueued with ID: " + downloadId + ", File: " + fileName);
                // Add to downloads list using the DownloadsActivity static helper
                DownloadsActivity.addDownload(this, new DownloadsActivity.DownloadItem(
                    fileName,
                    filePath,
                    url,
                    downloadId
                ));
                Toast.makeText(this, "Download started: " + fileName, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "DownloadManager service not available.");
                Toast.makeText(this, "DownloadManager not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initiating download for " + url, e);
            Toast.makeText(this, "Error starting download", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ensure the filename has an appropriate extension for the given MIME type.
     * Returns a possibly-updated filename; if no change is needed, returns the original name.
     */
    private String ensureExtensionForMime(String fileName, String mimeType) {
        if (fileName == null || fileName.isEmpty() || mimeType == null || mimeType.isEmpty()) return fileName;
        String name = fileName;
        // Strip any stray query parameters mistakenly included in filename
        int q = name.indexOf('?');
        if (q >= 0) name = name.substring(0, q);
        // Current extension
        String currentExt = null;
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            currentExt = name.substring(dot + 1).toLowerCase();
        }
        // If we already have a plausible extension (not bin/dat), keep it
        if (currentExt != null && !currentExt.isEmpty() && !currentExt.equals("bin") && !currentExt.equals("dat")) {
            return name;
        }
        // Guess extension from MIME
        String want = null;
        try {
            want = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType.toLowerCase());
        } catch (Throwable ignored) {}
        if (want == null || want.isEmpty()) {
            // Manual common mappings
            if (mimeType.startsWith("video/")) {
                if (mimeType.equalsIgnoreCase("video/mp4") || mimeType.equalsIgnoreCase("video/h264")) want = "mp4";
                else if (mimeType.equalsIgnoreCase("video/webm")) want = "webm";
                else if (mimeType.equalsIgnoreCase("video/x-matroska") || mimeType.equalsIgnoreCase("video/mkv")) want = "mkv";
                else if (mimeType.equalsIgnoreCase("video/MP2T") || mimeType.equalsIgnoreCase("video/mp2t")) want = "ts";
            } else if (mimeType.startsWith("audio/")) {
                if (mimeType.equalsIgnoreCase("audio/mpeg")) want = "mp3";
                else if (mimeType.equalsIgnoreCase("audio/aac")) want = "aac";
                else if (mimeType.equalsIgnoreCase("audio/ogg")) want = "ogg";
            } else if (mimeType.equalsIgnoreCase("application/vnd.android.package-archive")) {
                want = "apk";
            }
        }
        if (want == null || want.isEmpty()) {
            return name; // no change
        }
        // Apply extension
        String base = (dot > 0) ? name.substring(0, dot) : name;
        return base + "." + want;
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "WRITE_EXTERNAL_STORAGE permission granted after request.");
                if (pendingDownloadResponse != null) {
                    handleDownloadResponse(pendingDownloadResponse); // Retry download
                    pendingDownloadResponse = null;
                }
            } else {
                Log.w(TAG, "WRITE_EXTERNAL_STORAGE permission denied after request.");
                // Only show toast on legacy devices; on newer devices we shouldn't have requested it.
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    Toast.makeText(this, "Storage permission is required to download files.", Toast.LENGTH_LONG).show();
                }
                pendingDownloadResponse = null; // Clear pending download as permission was denied
            }
        } else if (requestCode == REQUEST_CODE_GV_ANDROID_PERMS) {
            // Forward result back to GeckoView's PermissionDelegate.Callback
            GeckoSession.PermissionDelegate.Callback cb = pendingAndroidPermCallback;
            pendingAndroidPermCallback = null;
            pendingAndroidPerms = null;

            if (cb == null) {
                Log.w(TAG, "[Perm] REQUEST_CODE_GV_ANDROID_PERMS but callback is null");
                return;
            }

            boolean allGranted = true;
            if (grantResults.length == 0) {
                allGranted = false;
            } else {
                for (int r : grantResults) {
                    if (r != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
            }

            if (allGranted) {
                Log.d(TAG, "[Perm] All requested Android permissions granted; calling cb.grant()");
                cb.grant();
            } else {
                Log.w(TAG, "[Perm] Some Android permissions denied; calling cb.reject()");
                cb.reject();
            }
        }
    }

    // --- PromptDelegate Implementation ---
    @Override
    public GeckoResult<PromptResponse> onSharePrompt(GeckoSession session, SharePrompt prompt) {
        // Using prompt.uri and prompt.text directly as per common practice for SharePrompt
        Log.d(TAG, "onSharePrompt: URI: " + prompt.uri + ", Title: " + prompt.title + ", Text: " + prompt.text);

        final GeckoResult<PromptResponse> result = new GeckoResult<>(); // Introduce GeckoResult

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String shareText = prompt.text;
        if (prompt.uri != null && !prompt.uri.isEmpty()) {
            shareText = (prompt.text != null ? prompt.text : "") + "\n" + prompt.uri;
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        if (prompt.title != null && !prompt.title.isEmpty()) {
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, prompt.title);
        }
        
        sendIntent.setType("text/plain");

        try {
            startActivity(Intent.createChooser(sendIntent, "Share via"));
            result.complete(prompt.confirm(SharePrompt.Result.SUCCESS)); // Complete result on success
        } catch (Exception e) {
            Log.e(TAG, "Error starting share activity", e);
            result.complete(prompt.confirm(SharePrompt.Result.FAILURE)); // Complete result on failure
        }
        return result; // Return the GeckoResult
    }

    // 2. AlertPrompt (User Provided)
    @Override
    public GeckoResult<PromptResponse> onAlertPrompt(
        GeckoSession session, 
        AlertPrompt prompt
    ) {
        Log.d(TAG, "MAIN ACTIVITY onAlertPrompt: Title=" + prompt.title + ", Message=" + prompt.message);
        
        final GeckoResult<PromptResponse> result = new GeckoResult<>();
        final AlertPrompt currentPrompt = prompt;
        final boolean[] handled = {false}; // Prevent double-completion

        runOnUiThread(() -> {
            if (MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                if (!handled[0]) {
                    handled[0] = true;
                    result.complete(currentPrompt.dismiss());
                }
                return;
            }

            new AlertDialog.Builder(MainActivity.this)
                .setTitle(currentPrompt.title)
                .setMessage(currentPrompt.message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (!handled[0]) {
                        handled[0] = true;
                        result.complete(currentPrompt.dismiss()); // Use dismiss() for OK
                    }
                })
                .setOnDismissListener(dialog -> {
                    if (!handled[0]) {
                        handled[0] = true;
                        result.complete(currentPrompt.dismiss()); // Use dismiss() for cancel/dismiss
                    }
                })
                .setCancelable(true)
                .show();
        });
        return result;
    }

    // 3. AuthPrompt (User Provided)
    @Override
    public GeckoResult<PromptResponse> onAuthPrompt(
        GeckoSession session, 
        AuthPrompt prompt
    ) {
        Log.d(TAG, "onAuthPrompt: Title: " + prompt.title + ", Message: " + prompt.message + ", Options: " + prompt.authOptions);

        final GeckoResult<PromptResponse> result = new GeckoResult<>();

        runOnUiThread(() -> {
            // Create layout for username and password input
            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 0, 50, 0); // Add some padding

            final EditText usernameInput = new EditText(MainActivity.this);
            usernameInput.setHint("Username");
            // Removed usernameInput.setText(prompt.username); as prompt.username is not a valid field
            layout.addView(usernameInput);

            final EditText passwordInput = new EditText(MainActivity.this);
            passwordInput.setHint("Password");
            passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(passwordInput);

            new AlertDialog.Builder(MainActivity.this)
                .setTitle(prompt.title != null && !prompt.title.isEmpty() ? prompt.title : "Authentication Required")
                .setMessage(prompt.message)
                .setView(layout) // Set the custom layout
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String username = usernameInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    result.complete(prompt.confirm(username, password)); // Confirm with entered credentials
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    result.complete(prompt.dismiss()); // Use prompt.dismiss() for AuthPrompt cancellation
                })
                .setOnCancelListener(dialog -> {
                     // Removed !result.isDone() check as it's not a valid method
                     result.complete(prompt.dismiss()); // Use prompt.dismiss() on dialog dismissal
                })
                .show();
        });

        return result;
    }

    // 1. BeforeUnloadPrompt (User Provided)
    @Override
    public GeckoResult<PromptResponse> onBeforeUnloadPrompt(
        GeckoSession session, 
        BeforeUnloadPrompt prompt
    ) {
        Log.d(TAG, "ANON_PD_POPUP: BeforeUnload prompt. Title: " + prompt.title);
        return GeckoResult.fromValue(prompt.confirm(AllowOrDeny.ALLOW)); 
    }

    @Override
    public GeckoResult<PromptResponse> onButtonPrompt(GeckoSession session, ButtonPrompt prompt) {
        Log.d(TAG, "onButtonPrompt: Title: " + prompt.title + ", Message: " + prompt.message);
        // ButtonPrompt does not have a 'choices' field.
        // It's typically for confirm/cancel style dialogs.

        final GeckoResult<PromptResponse> result = new GeckoResult<>();
        new AlertDialog.Builder(MainActivity.this)
            .setTitle(prompt.title)
            .setMessage(prompt.message)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                result.complete(prompt.confirm(GeckoSession.PromptDelegate.ButtonPrompt.Type.POSITIVE));
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                result.complete(prompt.confirm(GeckoSession.PromptDelegate.ButtonPrompt.Type.NEGATIVE));
            })
            .setOnCancelListener(dialog -> {
                // Removed !result.isDone() check as it's not a valid method
                result.complete(prompt.dismiss());
            })
            .show();
        return result;
    }

    @Override
    public GeckoResult<PromptResponse> onColorPrompt(GeckoSession session, ColorPrompt prompt) {
        Log.d(TAG, "MAIN ACTIVITY onColorPrompt: Title=" + prompt.title + ", DefaultValue=" + prompt.defaultValue);

        this.pendingColorPrompt = prompt;
        this.pendingGeckoResultForColorPrompt = new GeckoResult<>();

        runOnUiThread(() -> {
            if (this.pendingColorPrompt == null) {
                Log.e(TAG, "onColorPrompt: pendingColorPrompt is null in runOnUiThread before showing dialog.");
                if (this.pendingGeckoResultForColorPrompt != null) {
                    this.pendingGeckoResultForColorPrompt.complete(null); // Or some error response
                    this.pendingGeckoResultForColorPrompt = null;
                }
                return;
            }

            // For displaying the color, convert int to hex string
            String hexColor = String.format("#%08X", pendingColorPrompt.defaultValue); // ARGB

            new AlertDialog.Builder(MainActivity.this)
                .setTitle(pendingColorPrompt.title != null ? pendingColorPrompt.title : "Choose Color")
                .setMessage("Suggested color: " + hexColor)
                // Optionally, add a small View with the background color set, if desired for better UX later.
                .setPositiveButton("OK", (dialog, which) -> {
                    PromptResponse response = null;
                    if (MainActivity.this.pendingColorPrompt != null) {
                        Log.d(TAG, "onColorPrompt: OK clicked, confirming with default value: " + hexColor);
                        response = MainActivity.this.pendingColorPrompt.confirm(MainActivity.this.pendingColorPrompt.defaultValue);
                    }
                    if (MainActivity.this.pendingGeckoResultForColorPrompt != null) {
                        MainActivity.this.pendingGeckoResultForColorPrompt.complete(response);
                    }
                    MainActivity.this.pendingColorPrompt = null;
                    MainActivity.this.pendingGeckoResultForColorPrompt = null;
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    PromptResponse response = null;
                    if (MainActivity.this.pendingColorPrompt != null) {
                        Log.d(TAG, "onColorPrompt: Cancel clicked, dismissing.");
                        response = MainActivity.this.pendingColorPrompt.dismiss();
                    }
                    if (MainActivity.this.pendingGeckoResultForColorPrompt != null) {
                        MainActivity.this.pendingGeckoResultForColorPrompt.complete(response);
                    }
                    MainActivity.this.pendingColorPrompt = null;
                    MainActivity.this.pendingGeckoResultForColorPrompt = null;
                })
                .setOnCancelListener(dialog -> { // Handles back button or touch outside
                    PromptResponse response = null;
                    if (MainActivity.this.pendingColorPrompt != null) {
                        Log.d(TAG, "onColorPrompt: Dialog cancelled, dismissing.");
                        response = MainActivity.this.pendingColorPrompt.dismiss();
                    }
                    if (MainActivity.this.pendingGeckoResultForColorPrompt != null) {
                        MainActivity.this.pendingGeckoResultForColorPrompt.complete(response);
                    }
                    MainActivity.this.pendingColorPrompt = null;
                    MainActivity.this.pendingGeckoResultForColorPrompt = null;
                })
                .show();
        });

        return this.pendingGeckoResultForColorPrompt;
    }

    @Override
    public GeckoResult<PromptResponse> onDateTimePrompt(GeckoSession session, DateTimePrompt prompt) {
        Log.d(TAG, "MAIN ACTIVITY onDateTimePrompt: Title=" + prompt.title +
                   ", TypeInt=" + prompt.type + // Log the int type
                   ", DefaultValue=" + prompt.defaultValue);

        this.pendingDateTimePrompt = prompt;
        this.pendingGeckoResultForDateTimePrompt = new GeckoResult<>();

        runOnUiThread(() -> {
            if (this.pendingDateTimePrompt == null) {
                Log.e(TAG, "onDateTimePrompt: pendingDateTimePrompt is null in runOnUiThread.");
                if (this.pendingGeckoResultForDateTimePrompt != null) {
                    this.pendingGeckoResultForDateTimePrompt.complete(null);
                    this.pendingGeckoResultForDateTimePrompt = null;
                }
                return;
            }

            final int type = this.pendingDateTimePrompt.type; // Use int
            final String defaultValue = this.pendingDateTimePrompt.defaultValue;
            java.util.Calendar calendar = java.util.Calendar.getInstance();

            // Try to parse default value
            if (defaultValue != null && !defaultValue.isEmpty()) {
                try {
                    if (type == DateTimePrompt.Type.DATE || type == DateTimePrompt.Type.DATETIME_LOCAL || type == DateTimePrompt.Type.MONTH || type == DateTimePrompt.Type.WEEK) {
                        java.text.SimpleDateFormat sdf = null;
                        if (type == DateTimePrompt.Type.MONTH) sdf = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US);
                        else if (type == DateTimePrompt.Type.WEEK) sdf = new java.text.SimpleDateFormat("yyyy-'W'ww", java.util.Locale.US); // Corrected Week format
                        else sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);

                        if (type == DateTimePrompt.Type.DATETIME_LOCAL && defaultValue.contains("T")) {
                             String datePart = defaultValue.split("T")[0];
                             calendar.setTime(new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(datePart));
                        } else if (type != DateTimePrompt.Type.TIME) { // Don't parse date for time-only
                             calendar.setTime(sdf.parse(defaultValue.split("T")[0]));
                        }
                    }
                    if ((type == DateTimePrompt.Type.TIME || type == DateTimePrompt.Type.DATETIME_LOCAL) && defaultValue.contains(":")) {
                        String timePart = defaultValue;
                        if (defaultValue.contains("T")) {
                           timePart = defaultValue.split("T")[1];
                        }
                        String[] timeComponents = timePart.split(":");
                        if (timeComponents.length >= 2) {
                            calendar.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(timeComponents[0]));
                            calendar.set(java.util.Calendar.MINUTE, Integer.parseInt(timeComponents[1]));
                        }
                         if (timeComponents.length == 3) { 
                            calendar.set(java.util.Calendar.SECOND, Integer.parseInt(timeComponents[2].substring(0,2)));
                        }
                    }
                } catch (java.text.ParseException e) {
                    Log.w(TAG, "onDateTimePrompt: Could not parse defaultValue '" + defaultValue + "' for type code '" + type + "'", e);
                    calendar = java.util.Calendar.getInstance(); 
                }
            }

            final int year = calendar.get(java.util.Calendar.YEAR);
            final int month = calendar.get(java.util.Calendar.MONTH);
            final int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            final int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            final int minute = calendar.get(java.util.Calendar.MINUTE);

            if (type == DateTimePrompt.Type.DATE) {
                android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedDate = String.format(java.util.Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                        completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.confirm(selectedDate));
                    }, year, month, day);
                datePickerDialog.setOnCancelListener(dialog -> completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss()));
                datePickerDialog.setTitle(this.pendingDateTimePrompt.title);
                datePickerDialog.show();
            } else if (type == DateTimePrompt.Type.TIME) {
                android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(MainActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute);
                        completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.confirm(selectedTime));
                    }, hour, minute, android.text.format.DateFormat.is24HourFormat(MainActivity.this));
                timePickerDialog.setOnCancelListener(dialog -> completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss()));
                timePickerDialog.setTitle(this.pendingDateTimePrompt.title);
                timePickerDialog.show();
            } else if (type == DateTimePrompt.Type.DATETIME_LOCAL) {
                android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(MainActivity.this,
                            (timeView, selectedHour, selectedMinute) -> {
                                String selectedDateTime = String.format(java.util.Locale.US, "%04d-%02d-%02dT%02d:%02d",
                                    selectedYear, selectedMonth + 1, selectedDayOfMonth, selectedHour, selectedMinute);
                                completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.confirm(selectedDateTime));
                            }, hour, minute, android.text.format.DateFormat.is24HourFormat(MainActivity.this));
                        timePickerDialog.setOnCancelListener(dialog -> completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss()));
                        timePickerDialog.setTitle(this.pendingDateTimePrompt.title != null && !this.pendingDateTimePrompt.title.isEmpty() ? this.pendingDateTimePrompt.title : "Select Time");
                        timePickerDialog.show();
                    }, year, month, day);
                datePickerDialog.setOnCancelListener(dialog -> completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss()));
                datePickerDialog.setTitle(this.pendingDateTimePrompt.title != null && !this.pendingDateTimePrompt.title.isEmpty() ? this.pendingDateTimePrompt.title : "Select Date");
                datePickerDialog.show();
            } else if (type == DateTimePrompt.Type.MONTH) {
                 android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        String selectedMonthStr = String.format(java.util.Locale.US, "%04d-%02d", selectedYear, selectedMonth + 1);
                        completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.confirm(selectedMonthStr));
                    }, year, month, day);
                datePickerDialog.setOnCancelListener(dialog -> completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss()));
                datePickerDialog.setTitle(this.pendingDateTimePrompt.title);
                datePickerDialog.show();
            } else if (type == DateTimePrompt.Type.WEEK) {
                Log.w(TAG, "DateTimePrompt.Type.WEEK is not fully supported with standard pickers. Dismissing.");
                android.widget.Toast.makeText(MainActivity.this, "Week selection is not fully supported.", android.widget.Toast.LENGTH_SHORT).show();
                completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss());
            } else {
                Log.w(TAG, "Unsupported DateTimePrompt type code: " + type + ". Dismissing.");
                android.widget.Toast.makeText(MainActivity.this, "Unsupported date/time input type.", android.widget.Toast.LENGTH_SHORT).show();
                completeDateTimePrompt(MainActivity.this.pendingDateTimePrompt.dismiss());
            }
        });

        return this.pendingGeckoResultForDateTimePrompt;
    }

    // Helper method to complete DateTimePrompt and clean up
    private void completeDateTimePrompt(PromptResponse response) {
        if (this.pendingGeckoResultForDateTimePrompt != null) {
            this.pendingGeckoResultForDateTimePrompt.complete(response);
        }
        this.pendingDateTimePrompt = null;
        this.pendingGeckoResultForDateTimePrompt = null;
        Log.d(TAG, "DateTimePrompt completed and fields cleaned up.");
    }

    @Override
    public GeckoResult<PromptResponse> onFilePrompt(GeckoSession session, FilePrompt prompt) {
        Log.d(TAG, "MAIN ACTIVITY onFilePrompt: Title=" + prompt.title + ", Type=" + prompt.type);

        // Assign to MainActivity's instance fields
        this.pendingFilePrompt = prompt; 
        this.pendingGeckoResultForFilePrompt = new GeckoResult<>();

        runOnUiThread(() -> {
            try {
                // Ensure instance fields are used within the Runnable
                if (this.pendingFilePrompt == null) {
                    Log.e(TAG, "Error: pendingFilePrompt is null in runOnUiThread for onFilePrompt.");
                    if (this.pendingGeckoResultForFilePrompt != null) {
                        this.pendingGeckoResultForFilePrompt.complete(null); // Or appropriate error response
                        this.pendingGeckoResultForFilePrompt = null; // Clean up
                    }
                    return;
                }

                Log.d(TAG, "FilePrompt Details (MAIN ACTIVITY): Title=" + this.pendingFilePrompt.title + 
                           ", Type=" + this.pendingFilePrompt.type + 
                           ", MimeTypes=" + Arrays.toString(this.pendingFilePrompt.mimeTypes));

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                if (this.pendingFilePrompt.mimeTypes != null && this.pendingFilePrompt.mimeTypes.length > 0) {
                    if (this.pendingFilePrompt.mimeTypes.length > 1) {
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, this.pendingFilePrompt.mimeTypes);
                    } else {
                        intent.setType(this.pendingFilePrompt.mimeTypes[0]);
                    }
                } else {
                    intent.setType("*/*");
                }

                if (this.pendingFilePrompt.type == FilePrompt.Type.MULTIPLE) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                filePickerLauncher.launch(intent);
                Log.d(TAG, "Launched system file picker intent (MAIN ACTIVITY).");

            } catch (Exception e) {
                Log.e(TAG, "Error launching file picker (MAIN ACTIVITY)", e);
                PromptResponse errorResponse = null;
                // Ensure fields are checked before use, especially if an early error occurred
                if (this.pendingFilePrompt != null) { 
                    errorResponse = this.pendingFilePrompt.dismiss();
                }
                if (this.pendingGeckoResultForFilePrompt != null) { 
                    this.pendingGeckoResultForFilePrompt.complete(errorResponse);
                }
                // Clean up instance fields on error path to prevent inconsistent state
                this.pendingFilePrompt = null;
                this.pendingGeckoResultForFilePrompt = null;
            }
        });

        // Return the stored GeckoResult from MainActivity's instance field
        return this.pendingGeckoResultForFilePrompt; 
    }

    // 4. SelectPrompt (User Provided) -> Renamed to onChoicePrompt
    @Override
    public GeckoResult<PromptResponse> onChoicePrompt( // Corrected method name and parameter type
        GeckoSession session,
        ChoicePrompt prompt // Corrected parameter type to ChoicePrompt
    ) {
        Log.d(TAG, "MAIN ACTIVITY onChoicePrompt: Title=" + prompt.title +
                   ", Message=" + prompt.message +
                   ", Type=" + prompt.type +
                   ", Choices Count=" + (prompt.choices != null ? prompt.choices.length : 0));

        this.pendingChoicePrompt = prompt;
        this.pendingGeckoResultForChoicePrompt = new GeckoResult<>();

        runOnUiThread(() -> {
            if (this.pendingChoicePrompt == null || this.pendingChoicePrompt.choices == null || this.pendingChoicePrompt.choices.length == 0) {
                Log.e(TAG, "onChoicePrompt: pendingChoicePrompt or its choices are null/empty in runOnUiThread.");
                if (this.pendingGeckoResultForChoicePrompt != null) {
                    PromptResponse dismissResponse = null;
                    if(this.pendingChoicePrompt != null) dismissResponse = this.pendingChoicePrompt.dismiss();
                    this.pendingGeckoResultForChoicePrompt.complete(dismissResponse);
                }
                this.pendingChoicePrompt = null;
                this.pendingGeckoResultForChoicePrompt = null;
                return;
            }

            final Choice[] choices = this.pendingChoicePrompt.choices;
            final String[] displayItems = new String[choices.length];
            final boolean[] checkedItems = new boolean[choices.length]; // For multi-choice
            final int[] selectedItem = {-1}; // For single-choice, wrapped in array to be final

            for (int i = 0; i < choices.length; i++) {
                displayItems[i] = choices[i].label;
                if (this.pendingChoicePrompt.type == ChoicePrompt.Type.MULTIPLE) {
                    checkedItems[i] = choices[i].selected;
                } else if (choices[i].selected) { // Single choice, find the first selected
                    selectedItem[0] = i;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(this.pendingChoicePrompt.title);
            if (this.pendingChoicePrompt.message != null && !this.pendingChoicePrompt.message.isEmpty()) {
                builder.setMessage(this.pendingChoicePrompt.message);
            }

            if (this.pendingChoicePrompt.type == ChoicePrompt.Type.MULTIPLE) {
                builder.setMultiChoiceItems(displayItems, checkedItems, (dialog, which, isChecked) -> {
                    checkedItems[which] = isChecked; // Update the state
                });
            } else { // SINGLE or MENU (treat MENU as SINGLE for now)
                builder.setSingleChoiceItems(displayItems, selectedItem[0], (dialog, which) -> {
                    selectedItem[0] = which; // Update selected item
                });
            }

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                PromptResponse response = null;
                if (MainActivity.this.pendingChoicePrompt != null) {
                    ArrayList<String> selectedIds = new ArrayList<>();
                    if (MainActivity.this.pendingChoicePrompt.type == ChoicePrompt.Type.MULTIPLE) {
                        for (int i = 0; i < choices.length; i++) {
                            if (checkedItems[i] && choices[i].id != null) {
                                selectedIds.add(choices[i].id);
                            }
                        }
                    } else { // SINGLE or MENU
                        if (selectedItem[0] != -1 && selectedItem[0] < choices.length && choices[selectedItem[0]].id != null) {
                            selectedIds.add(choices[selectedItem[0]].id);
                        }
                    }
                    Log.d(TAG, "onChoicePrompt: Confirming with selected IDs: " + selectedIds);
                    response = MainActivity.this.pendingChoicePrompt.confirm(selectedIds.toArray(new String[0]));
                }
                if (MainActivity.this.pendingGeckoResultForChoicePrompt != null) {
                    MainActivity.this.pendingGeckoResultForChoicePrompt.complete(response);
                }
                MainActivity.this.pendingChoicePrompt = null;
                MainActivity.this.pendingGeckoResultForChoicePrompt = null;
            });

            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                PromptResponse response = null;
                if (MainActivity.this.pendingChoicePrompt != null) {
                    response = MainActivity.this.pendingChoicePrompt.dismiss();
                }
                if (MainActivity.this.pendingGeckoResultForChoicePrompt != null) {
                    MainActivity.this.pendingGeckoResultForChoicePrompt.complete(response);
                }
                MainActivity.this.pendingChoicePrompt = null;
                MainActivity.this.pendingGeckoResultForChoicePrompt = null;
            });

            builder.setOnCancelListener(dialog -> {
                PromptResponse response = null;
                if (MainActivity.this.pendingChoicePrompt != null) {
                    response = MainActivity.this.pendingChoicePrompt.dismiss();
                }
                if (MainActivity.this.pendingGeckoResultForChoicePrompt != null) {
                    MainActivity.this.pendingGeckoResultForChoicePrompt.complete(response);
                }
                MainActivity.this.pendingChoicePrompt = null;
                MainActivity.this.pendingGeckoResultForChoicePrompt = null;
            });

            builder.show();
        });

        return this.pendingGeckoResultForChoicePrompt;
    }

    @Override
    public GeckoResult<PromptResponse> onTextPrompt(GeckoSession session, TextPrompt prompt) {
        Log.d(TAG, "onTextPrompt: Title: " + prompt.title + ", Message: " + prompt.message + ", DefaultValue: " + prompt.defaultValue);

        final GeckoResult<PromptResponse> result = new GeckoResult<>();
        
        // Use runOnUiThread as AlertDialog must be shown on the UI thread
        runOnUiThread(() -> {
            final EditText input = new EditText(MainActivity.this);
            input.setText(prompt.defaultValue);
            input.setHint("Enter text"); // Optional hint

            new AlertDialog.Builder(MainActivity.this)
                .setTitle(prompt.title)
                .setMessage(prompt.message)
                .setView(input) // Set the EditText as the dialog view
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String enteredText = input.getText().toString();
                    result.complete(prompt.confirm(enteredText)); // Confirm with entered text
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    result.complete(prompt.dismiss()); // Dismiss on cancel
                })
                .setOnCancelListener(dialog -> {
                    // Handle dialog dismissal (e.g., back button press)
                    // Removed !result.isDone() check as it's not a valid method
                    result.complete(prompt.dismiss());
                })
                .show();
        });

        return result; // Return the GeckoResult immediately
    }

    // Add this method to clean up temporary upload files
    private void cleanupTempUploadFiles() {
        Log.d(TAG, "Cleaning up temporary upload files.");
        for (File file : tempUploadFiles) {
            if (file.exists()) {
                if (file.delete()) {
                    Log.d(TAG, "Deleted temporary file: " + file.getAbsolutePath());
                } else {
                    Log.w(TAG, "Failed to delete temporary file: " + file.getAbsolutePath());
                }
            } else {
                Log.d(TAG, "Temporary file not found: " + file.getAbsolutePath());
            }
        }
        tempUploadFiles.clear(); // Clear the list after attempting deletion
    }

    private void applyImmersiveMode(boolean immersive) {
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (immersive) {
            // Hide system bars for immersive mode
            insetsController.hide(WindowInsetsCompat.Type.systemBars());
            // Make the behavior sticky
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        } else {
            // Show system bars
            insetsController.show(WindowInsetsCompat.Type.systemBars());
            // Reset the behavior to default
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_DEFAULT);
        }
    }

    // Helper method to load URI in GeckoView, abstracting the session choice
    private void loadUriInGeckoView(GeckoSession currentSessionContext, String uriString) {
        if (uriString == null || uriString.isEmpty()) {
            Log.w(TAG, "loadUriInGeckoView: Null or empty URI string.");
            return;
        }
        if (currentSessionContext != null) {
            Log.d(TAG, "loadUriInGeckoView: Loading in provided session context.");
            currentSessionContext.loadUri(uriString);
        } else {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                Log.d(TAG, "loadUriInGeckoView: Loading in active session.");
                activeSession.loadUri(uriString);
            } else {
                Log.e(TAG, "loadUriInGeckoView: No session available to load URI: " + uriString);
                Toast.makeText(MainActivity.this, "Cannot open link: No active browser tab.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- Picture-in-Picture Methods ---
    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        GeckoSession activeSession = getActiveSession();
        // Check if we are in a state where PiP is appropriate (e.g., video is fullscreen)
        // For now, we'll use isInGeckoViewFullscreen as a proxy for this.
        // You might need a more specific check if a video is actually playing.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            isMediaActuallyPlaying && activeSession == mediaPlayingSession && // Use new media playing state
            getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            
            Log.d(TAG, "onUserLeaveHint: Attempting to enter PiP mode because media is playing.");
            enterPictureInPictureModeWithCurrentParams();
        } else {
            Log.d(TAG, "onUserLeaveHint: Conditions for PiP not met. isMediaActuallyPlaying=" + isMediaActuallyPlaying +
                       ", activeSession == mediaPlayingSession: " + (activeSession == mediaPlayingSession) +
                       ", isInGeckoViewFullscreen=" + isInGeckoViewFullscreen);
        }
    }

    private void enterPictureInPictureModeWithCurrentParams() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // Define aspect ratio for PiP window (e.g., 16:9)
                Rational aspectRatio = new Rational(16, 9);
                PictureInPictureParams params = new PictureInPictureParams.Builder()
                        .setAspectRatio(aspectRatio)
                        .build();
                enterPictureInPictureMode(params);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error entering PiP mode", e);
                // This can happen if the activity is not in a state to enter PiP
                // (e.g., not visible, or finishing)
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPicInPicMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPicInPicMode, newConfig);
        this.isInPictureInPictureMode = isInPicInPicMode;
        if (isInPicInPicMode) {
            Log.d(TAG, "Entered Picture-in-Picture mode.");
            // Hide app controls when in PiP
            if (controlBarContainer != null) {
                controlBarContainer.setVisibility(View.GONE);
            }
            // You might want to hide other non-video UI elements as well
        } else {
            Log.d(TAG, "Exited Picture-in-Picture mode.");
            // Show app controls when PiP is exited
            // The visibility of controlBarContainer will be handled by exitFullScreen
            // or when returning to the app. We might need to explicitly show it if
            // exiting PiP doesn't trigger a fullscreen exit.
            if (controlBarContainer != null && !isInGeckoViewFullscreen) {
                 controlBarContainer.setVisibility(View.VISIBLE);
                 if (isControlBarExpanded) {
                    showExpandedBar();
                 } else {
                    showMinimizedBar();
                 }
            }
        }
        // If exiting PiP and we were in GeckoView fullscreen, we might want to re-enter it.
        // However, GeckoView's onFullScreen(false) should be triggered by the system
        // when PiP ends, which would call our exitFullScreen().
    }
    // --- End Picture-in-Picture Methods ---

    // --- Snapshot File Management Helpers ---
    private File getSnapshotDirectory() {
        File snapshotDir = new File(getCacheDir(), SNAPSHOT_DIRECTORY_NAME);
        if (!snapshotDir.exists()) {
            if (!snapshotDir.mkdirs()) {
                Log.e(TAG, "Failed to create snapshot directory: " + snapshotDir.getAbsolutePath());
            }
        }
        return snapshotDir;
    }

    private String getSnapshotFilename(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(url.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext + ".png"; // Save as PNG
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.e(TAG, "MD5 algorithm not found for snapshot filename", e);
            // Fallback to a simpler, less ideal naming if MD5 fails (should not happen)
            return url.replaceAll("[^a-zA-Z0-9.-]", "_") + ".png";
        }
    }

    private void saveSnapshotToDisk(Bitmap bitmap, String url) {
        String filename = getSnapshotFilename(url);
        if (filename == null || bitmap == null) {
            Log.w(TAG, "Cannot save snapshot to disk, filename or bitmap is null. URL: " + url);
            return;
        }
        File snapshotDir = getSnapshotDirectory();
        if (!snapshotDir.exists() && !snapshotDir.mkdirs()) {
             Log.e(TAG, "Failed to create snapshot directory for saving: " + snapshotDir.getAbsolutePath());
             return;
        }
        File file = new File(snapshotDir, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out); // PNG format, 90 quality
            Log.d(TAG, "Snapshot saved to disk: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Error saving snapshot to disk: " + file.getAbsolutePath(), e);
        }
    }

    private @Nullable Bitmap loadSnapshotFromDisk(String url) {
        String filename = getSnapshotFilename(url);
        if (filename == null) {
            return null;
        }
        File snapshotDir = getSnapshotDirectory();
        File file = new File(snapshotDir, filename);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Log.d(TAG, "Snapshot loaded from disk: " + file.getAbsolutePath());
                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, "Error loading snapshot from disk: " + file.getAbsolutePath(), e);
                return null;
            }
        } else {
            // Log.d(TAG, "Snapshot file not found on disk: " + file.getAbsolutePath()); // Can be noisy
            return null;
        }
    }

    private void deleteSnapshotFromDisk(String url) {
        String filename = getSnapshotFilename(url);
        if (filename == null) {
            return;
        }
        File snapshotDir = getSnapshotDirectory();
        File file = new File(snapshotDir, filename);
        if (file.exists()) {
            if (file.delete()) {
                Log.d(TAG, "Snapshot deleted from disk: " + file.getAbsolutePath());
            } else {
                Log.w(TAG, "Failed to delete snapshot from disk: " + file.getAbsolutePath());
            }
        }
    }

    private void deleteAllSnapshotsFromDisk() {
        File snapshotDir = getSnapshotDirectory();
        if (snapshotDir.exists() && snapshotDir.isDirectory()) {
            File[] files = snapshotDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        Log.d(TAG, "Deleted snapshot file: " + file.getAbsolutePath());
                    } else {
                        Log.w(TAG, "Failed to delete snapshot file: " + file.getAbsolutePath());
                    }
                }
            }
            Log.i(TAG, "All snapshots deleted from disk directory: " + snapshotDir.getAbsolutePath());
        }
    }
    // --- End Snapshot File Management Helpers ---

    // Method to clear all tabs and open a new default tab
    private void clearAllTabsAndCreateNew() {
        Log.d(TAG, "Clearing all tabs.");
        // Close all existing sessions
        synchronized (geckoSessionList) {
            for (GeckoSession session : new ArrayList<>(geckoSessionList)) { // Iterate over a copy
                // Clean up snapshot from map and disk if it exists for this session
                String url = sessionUrlMap.get(session);
                if (url != null) {
                    deleteSnapshotFromDisk(url); // Delete from disk
                }
                sessionSnapshotMap.remove(session); // Remove from memory map
                sessionUrlMap.remove(session); // Remove from URL map
                session.close();
            }
            geckoSessionList.clear();
        }

        // Clear all snapshots from the disk directory as a final sweep (as per requirement)
        deleteAllSnapshotsFromDisk(); 

        activeSessionIndex = -1; // Reset active index
        this.geckoSession = null; // Clear the global convenience field

        // Create a new default tab based on locale
        createNewTab(getDefaultHomepageUrl(), true);

        // Update UI (e.g., if there's a tab count display, it should reflect 1)
        updateUIForActiveSession(); // This will also update the URL bar for the new tab
        saveSessionState(); // Persist the new state (one tab)
        Toast.makeText(this, "All tabs cleared.", Toast.LENGTH_SHORT).show();
    }

    // --- MediaSession.Delegate Implementation ---

    @Override
    public void onActivated(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        // A media session has become active.
        // This might be a good place to reset isMediaActuallyPlaying if the active media session changes.
        Log.d(TAG, "MediaSession.Delegate: onActivated for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
        // If this newly activated session is not the one we thought was playing, reset.
        if (mediaPlayingSession != null && mediaPlayingSession != session && session == getActiveSession()) {
            isMediaActuallyPlaying = false;
            // mediaPlayingSession will be updated by onPlay if it starts playing.
        }
         // If the activated session is the current active tab, we might anticipate playback.
        // However, onPlay is a more direct indicator.
    }

    @Override
    public void onDeactivated(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        // A media session has become inactive.
        Log.d(TAG, "MediaSession.Delegate: onDeactivated for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
        if (session == mediaPlayingSession) {
            isMediaActuallyPlaying = false;
            mediaPlayingSession = null;
            Log.d(TAG, "MediaSession.Delegate: Media playback stopped (deactivated) for active session.");
        }
    }

    @Override
    public void onPlay(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        Log.d(TAG, "MediaSession.Delegate: onPlay for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
        if (session == getActiveSession()) {
            isMediaActuallyPlaying = true;
            mediaPlayingSession = session;
            Log.d(TAG, "MediaSession.Delegate: Media playback started for active session.");
        } else {
            // Media started in a background tab. We might want to handle this differently or ignore for PiP.
            // For now, PiP is only concerned with the active tab.
            Log.d(TAG, "MediaSession.Delegate: Media playback started for background session.");
        }
    }

    @Override
    public void onPause(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        Log.d(TAG, "MediaSession.Delegate: onPause for session: " + (sessionUrlMap.containsKey(session) ? sessionUrlMap.get(session) : "N/A"));
        if (session == mediaPlayingSession) { // Check if it\'s the session we were tracking
            isMediaActuallyPlaying = false;
            mediaPlayingSession = null; // Clear the playing session
            Log.d(TAG, "MediaSession.Delegate: Media playback paused for previously playing session.");
        }
    }

        @Override
    public void onStop(@NonNull GeckoSession session, @NonNull MediaSession mediaSession) {
        Log.d(TAG, "MediaSession.Delegate: onStop for session: " + (session != null ? "current session" : "null"));
        if (mediaPlayingSession == session) {
            isMediaActuallyPlaying = false;
            // Don't null out mediaPlayingSession here, we might get a "play" event for it again.
        }
    }

    private void setUiFocus(boolean enable) {
        isUiFocused = enable;
        if (enable) {
            if (tvCursorView != null) {
                tvCursorView.setVisibility(View.GONE);
            }
            // Focus the first element of the settings panel if visible, otherwise control bar
            if (settingsPanelLayout.getVisibility() == View.VISIBLE && !settingsPanelElements.isEmpty()) {
                settingsPanelElements.get(0).requestFocus();
            } else if (!controlBarElements.isEmpty()) {
                controlBarElements.get(0).requestFocus();
            }
        } else {
            if (tvCursorView != null) {
                tvCursorView.setVisibility(View.VISIBLE);
                centerCursor(); // Recenter cursor when returning to geckoView
            }
            if (geckoView != null) {
                geckoView.requestFocus();
            }
        }
    }

    private void updateFocusableRects() {
        if (isTvDevice()) {
            focusableRects.clear();
            if (controlBarElements != null) {
                for (View view : controlBarElements) {
                    if (view.getVisibility() == View.VISIBLE && view.isShown()) {
                        Rect rect = new Rect();
                        view.getGlobalVisibleRect(rect);
                        focusableRects.add(rect);
                    }
                }
            }
        }
    }

    private void centerCursor() {
        if (tvCursorView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvCursorView.getLayoutParams();
            params.leftMargin = (getWindowManager().getDefaultDisplay().getWidth() - tvCursorView.getWidth()) / 2;
            params.topMargin = (getWindowManager().getDefaultDisplay().getHeight() - tvCursorView.getHeight()) / 2;
            tvCursorView.setLayoutParams(params);
        }
    }

   
      /**
     * Shows the cursor and resets the auto-hide timer if in video fullscreen mode.
     */
    private void handleCursorInput() {
        if (isFullscreenVideo() && tvCursorView != null) {
            tvCursorView.showCursor();
            Log.d(TAG, "Cursor shown due to input");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Handle cursor visibility for any key event
        if (isTvDevice() && event.getAction() == KeyEvent.ACTION_DOWN) {
            // Show cursor on any key press in TV mode
            handleCursorInput();
            // Diagnostics: DPAD input state on ACTION_DOWN
            int _cx = -1, _cy = -1;
            if (tvCursorView != null && tvCursorView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams _lp = (FrameLayout.LayoutParams) tvCursorView.getLayoutParams();
                _cx = _lp.leftMargin;
                _cy = _lp.topMargin;
            }
            Log.d(TAG, "[TV][DPAD] key=" + event.getKeyCode() + " action=DOWN uiFocused=" + isUiFocused +
                    " cursor=(" + _cx + "," + _cy + ") step=" + currentStepSize);
            
            // Handle Exit button on TV devices
            if (event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
                finishAffinity();
                return true;
            }
        }
        // ... rest of your existing dispatchKeyEvent logic ...
        if (!isTvDevice()) {
            return super.dispatchKeyEvent(event);
        }

        // Acceleration logic for cursor movement
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!isKeyPressed) {
                accelerationStartTime = System.currentTimeMillis();
                isKeyPressed = true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            isKeyPressed = false;
            currentStepSize = 30; // Reset to base
        }

        if (isKeyPressed) {
            long duration = System.currentTimeMillis() - accelerationStartTime;
            if (duration > ACCELERATION_INTERVAL) {
                int increments = (int) (duration / ACCELERATION_INTERVAL);
                currentStepSize = Math.min(30 + increments * STEP_INCREMENT, MAX_STEP_SIZE);
            }
        }

        // If an EditText (like the URL bar) has focus, let the system handle it normally.
        if (getCurrentFocus() instanceof EditText) {
            return super.dispatchKeyEvent(event);
        }

        // If the UI (control bar, settings panel) has focus, let the system handle D-Pad navigation.
        if (isUiFocused) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                // Special case: If we're on the bottom-most UI element, pressing down exits UI focus mode.
                if (getCurrentFocus() != null && getCurrentFocus().focusSearch(View.FOCUS_DOWN) == null) {
                    setUiFocus(false);
                    return true; // Consume this event.
                }
            }
            // Allow the system to handle all other navigation between UI elements.
            return super.dispatchKeyEvent(event);
        }

        // --- From this point on, we are in GeckoView navigation mode. ---
        // We will handle all D-PAD events and consume them by returning true.

        final int keyCode = event.getKeyCode();

        // Handle clicks (Center/Enter)
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getRepeatCount() == 0) {
                    mGestureDownTime = System.currentTimeMillis();
                    simulateTouchEvent(MotionEvent.ACTION_DOWN);
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                simulateTouchEvent(MotionEvent.ACTION_UP);
            }
            return true; // Consume all parts of the click event.
        }

        // Handle arrow keys for cursor movement and scrolling.
        // We only act on ACTION_DOWN to avoid double actions.
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvCursorView.getLayoutParams();
            int currentX = params.leftMargin;
            int currentY = params.topMargin;
            int gvHeight = 0;
            if (geckoView != null) {
                gvHeight = geckoView.getHeight();
                if (gvHeight <= 0) gvHeight = geckoView.getMeasuredHeight();
            }
            if (gvHeight <= 0) {
                gvHeight = getWindowManager().getDefaultDisplay().getHeight();
            }
            // Use GeckoView's height to decide when the cursor has reached the visible bottom of the web content area
            // Subtract a tiny epsilon to avoid off-by-one preventing scroll trigger
            int geckoViewBottomEdge = Math.max(0, gvHeight - tvCursorView.getHeight() - 4);

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    params.leftMargin = Math.max(0, currentX - currentStepSize);
                    tvCursorView.setLayoutParams(params);
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    params.leftMargin = Math.min(getWindowManager().getDefaultDisplay().getWidth() - tvCursorView.getWidth(), currentX + currentStepSize);
                    tvCursorView.setLayoutParams(params);
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    // Always forward an app-driven menu navigation message.
                    sendTvMenuNav("up");
                    // If the cursor is not at the top edge, move the cursor.
                    if (currentY > 0) {
                        params.topMargin = Math.max(0, currentY - currentStepSize);
                        tvCursorView.setLayoutParams(params);
                    } else {
                        // At the top edge: try element-level scroll first; if not possible, fallback to root or UI focus.
                        GeckoSession active = getActiveSession();
                        if (tvElementScrollEnabled && active != null) {
                            Log.d(TAG, "[TV][EXT] topEdge -> try elementScrollUp (no focus pre-step)");
                            final boolean willFallbackToRoot = mCanScrollUp; // else we'll enter UI focus
                            final boolean[] retriedOnce = new boolean[]{false};
                            final Runnable finalFallback = () -> {
                                if (willFallbackToRoot) {
                                    Log.d(TAG, "[TV][SCROLL] extFailed -> rootScrollUp fallback");
                                    simulateScroll(true);
                                } else {
                                    Log.d(TAG, "[TV][FOCUS] extFailed and cannot rootScrollUp -> enterUiFocus");
                                    setUiFocus(true);
                                }
                            };
                            Runnable onFail = () -> {
                                // Second-chance attempt with a larger offset before giving up
                                if (!retriedOnce[0]) {
                                    retriedOnce[0] = true;
                                    Log.d(TAG, "[TV][EXT] topEdgeUp first attempt failed -> second scroll attempt (no focus)");
                                    // No further retries; on failure, perform final fallback
                                    tryElementScrollAtCursorWithRetryFlag(active, -TV_EXT_SCROLL_CSS_PX, finalFallback, false, true);
                                } else {
                                    finalFallback.run();
                                }
                            };
                            // Tag this request as a top-edge UP so that ok=false leads to immediate focus/root decision
                            tryElementScrollAtCursorWithRetryFlag(active, -TV_EXT_SCROLL_CSS_PX, onFail, true, true);
                        } else {
                            if (mCanScrollUp) {
                                Log.d(TAG, "[TV][SCROLL] topEdge mCanScrollUp=true -> rootScrollUp");
                                simulateScroll(true); // true = scroll content up
                            } else {
                                Log.d(TAG, "[TV][FOCUS] topEdge mCanScrollUp=false -> enterUiFocus");
                                setUiFocus(true);
                            }
                        }
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    // Always forward an app-driven menu navigation message.
                    sendTvMenuNav("down");
                    // If the cursor is not at the bottom edge, move the cursor.
                    if (currentY < geckoViewBottomEdge) {
                        params.topMargin = Math.min(geckoViewBottomEdge, currentY + currentStepSize);
                        tvCursorView.setLayoutParams(params);
                    } else {
                        // If at the bottom edge, try element-level scroll first, fallback to root scroll.
                        GeckoSession active = getActiveSession();
                        if (tvElementScrollEnabled && active != null) {
                            Log.d(TAG, "[TV][EXT] bottomEdge -> focus scrollable then try elementScrollDown");
                            tryFocusScrollableAtCursor(active, null);
                            Runnable onFail = () -> {
                                Log.d(TAG, "[TV][SCROLL] extFailed -> rootScrollDown fallback (debounced)");
                                runDebouncedRootScroll(false);
                            };
                            tryElementScrollAtCursor(active, TV_EXT_SCROLL_CSS_PX, onFail);
                        } else {
                            Log.d(TAG, "[TV][SCROLL] bottomEdge -> rootScrollDown");
                            simulateScroll(false); // false = scroll content down
                        }
                    }
                    break;
            }
        }
        
        // If the key was a D-PAD key, we consume the event (both down and up) to prevent it from reaching the webpage.
        if (keyCode >= KeyEvent.KEYCODE_DPAD_UP && keyCode <= KeyEvent.KEYCODE_DPAD_CENTER) {
            // For D-PAD keys, we've already handled cursor visibility in the ACTION_DOWN case
            return true;
        }

        // For any other key (e.g., BACK, VOLUME), let the system handle it.
        return super.dispatchKeyEvent(event);
    }

    private Rect findNearestFocusableArea(int direction) {
        if (tvCursorView == null) return null;

        float cursorX = tvCursorView.getX() + tvCursorView.getWidth() / 2f;
        float cursorY = tvCursorView.getY() + tvCursorView.getHeight() / 2f;

        Rect bestCandidate = null;
        double minDistance = Double.MAX_VALUE;

        // Use the pre-calculated focusableRects list for efficiency and reliability
        for (Rect rect : focusableRects) {
            float centerX = rect.centerX();
            float centerY = rect.centerY();

            boolean isCandidate = false;
            switch (direction) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (centerY < cursorY) isCandidate = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (centerY > cursorY) isCandidate = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (centerX < cursorX) isCandidate = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (centerX > cursorX) isCandidate = true;
                    break;
            }

            if (isCandidate) {
                double distance = Math.sqrt(Math.pow(cursorX - centerX, 2) + Math.pow(cursorY - centerY, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    bestCandidate = rect;
                }
            }
        }
        return bestCandidate;
    }

    private void simulateTouchEvent(int motionEventAction) {
        if (geckoView != null && tvCursorView != null) {
            // Get absolute screen coordinates for the center of the cursor
            int[] cursorLocation = new int[2];
            tvCursorView.getLocationOnScreen(cursorLocation);
            int cursorScreenX = cursorLocation[0] + tvCursorView.getWidth() / 2;
            int cursorScreenY = cursorLocation[1] + tvCursorView.getHeight() / 2;

            // Get absolute screen coordinates for the top-left of GeckoView
            int[] geckoViewLocation = new int[2];
            geckoView.getLocationOnScreen(geckoViewLocation);
            int geckoScreenX = geckoViewLocation[0];
            int geckoScreenY = geckoViewLocation[1];

            // Calculate tap coordinates relative to GeckoView's top-left corner
            float tapX = cursorScreenX - geckoScreenX;
            float tapY = cursorScreenY - geckoScreenY;

            if (mGestureDownTime == 0 && motionEventAction == MotionEvent.ACTION_DOWN) {
                mGestureDownTime = System.currentTimeMillis();
            }

            final long eventTime = System.currentTimeMillis();

            MotionEvent event = MotionEvent.obtain(
                mGestureDownTime, // Use the stored downTime for a consistent gesture
                eventTime,
                motionEventAction,
                tapX,
                tapY,
                0
            );

            // If desktop mode is active (or on TV), emulate a mouse-like event so sites treat it like a desktop click
            try {
                String uaMode = prefs != null ? prefs.getString(PREF_USER_AGENT_MODE, DEFAULT_USER_AGENT_MODE) : DEFAULT_USER_AGENT_MODE;
                boolean desktopLike = "desktop".equals(uaMode) || isTvDevice();
                if (desktopLike) {
                    event.setSource(android.view.InputDevice.SOURCE_MOUSE);
                }
            } catch (Throwable ignored) {}

            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                // CORRECT METHOD: Use the PanZoomController to process the touch event.
                // This correctly interacts with the page at a low level.
                activeSession.getPanZoomController().onTouchEvent(event);
                Log.d(TAG, "Sent touch event via PanZoomController.onTouchEvent.");
            } else {
                Log.w(TAG, "Could not send touch event because active session was null.");
            }
            
            event.recycle();
        }
    }

    private void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Ensure text is selected before showing keyboard
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.selectAll();
                }
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                Log.d(TAG, "showKeyboard() called for view: " + view.getClass().getSimpleName());
            }
        } else {
            Log.w(TAG, "showKeyboard: view is null.");
        }
    }

    private void setupGlobalLayoutListener() {
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();
            private boolean wasOpened;

            @Override
            public void onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isKeyboardVisible = keypadHeight > screenHeight * 0.15;

                if (isKeyboardVisible != wasOpened) {
                    wasOpened = isKeyboardVisible;
                    if (!isKeyboardVisible && isTvDevice()) {
                        // Keyboard was just closed on a TV device
                        Log.d(TAG, "Keyboard hidden on TV. Returning focus to URL bar.");
                        // It's possible the focus is already on the URL bar,
                        // but requesting it ensures the state is correct.
                        urlBar.requestFocus();
                    }
                }
            }
        });
    }

    private void simulateScroll(boolean scrollUp) {
        GeckoSession activeSession = getActiveSession();
        if (activeSession == null) {
            Log.w(TAG, "Cannot scroll, no active session.");
            return;
        }

        final PanZoomController pzc = activeSession.getPanZoomController();
        // A reasonable distance to scroll with one D-pad press
        final int scrollDistance = 100;
        final float direction = scrollUp ? -1.0f : 1.0f;

        // Use PanZoomController's built-in scrollBy method for a reliable scroll.
        // SCROLL_BEHAVIOR_AUTO provides an instantaneous jump, which feels better for D-pad navigation.
        Log.d(TAG, "[TV][SCROLL] PZC.scrollBy dy=" + (scrollDistance * direction));
        pzc.scrollBy(
            ScreenLength.fromPixels(0),
            ScreenLength.fromPixels(scrollDistance * direction),
            PanZoomController.SCROLL_BEHAVIOR_AUTO
        );

        Log.d(TAG, "[TV][SCROLL] PZC.scrollBy invoked");
    }

    private void handleTorrentIntent(Intent intent) {
        if (intent == null || intent.getData() == null) return;
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            String scheme = data.getScheme();
            String type = intent.getType();

            boolean isMagnet = "magnet".equals(scheme);
            boolean isTorrentFile = "application/x-bittorrent".equals(type) || (data.toString().endsWith(".torrent"));

            if (isMagnet || isTorrentFile) {
                showTorrentDecisionDialog(data.toString());
            }
        }
    }

    private void showTorrentDecisionDialog(String url) {
        Log.i(TAG, "Torrent Detection: Magnet link intercepted: " + url);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Torrent Detected")
                .setMessage("What would you like to do with this torrent?")
                .setPositiveButton("Stream", (d, w) -> {
                    Log.i(TAG, "Torrent Decision: User selected STREAM for " + url);
                    Intent serviceIntent = new Intent(this, TorrentService.class);
                    serviceIntent.setAction(TorrentService.ACTION_START_STREAM);
                    serviceIntent.putExtra(TorrentService.EXTRA_MAGNET_URL, url);
                    startService(serviceIntent);

                    // Launch Player Activity
                    Intent playerIntent = new Intent(this, TorrentPlayerActivity.class);
                    // No data needed, it binds to service
                    startActivity(playerIntent);
                })
                .setNegativeButton("Download", (d, w) -> {
                    Log.i(TAG, "Torrent Decision: User selected DOWNLOAD for " + url);
                    Intent serviceIntent = new Intent(this, TorrentService.class);
                    serviceIntent.setAction(TorrentService.ACTION_START_DOWNLOAD);
                    serviceIntent.putExtra(TorrentService.EXTRA_MAGNET_URL, url);
                    startService(serviceIntent);
                    
                    // Add to Downloads List
                    String name = "Torrent Download";
                    try {
                         android.net.Uri uri = android.net.Uri.parse(url);
                         String dn = uri.getQueryParameter("dn");
                         if (dn != null && !dn.isEmpty()) name = dn;
                    } catch (Exception e) {}
                    
                    DownloadsActivity.addDownload(this, new DownloadsActivity.DownloadItem(
                        name,
                        null, // File path unknown initially
                        url,
                        -1, // No DownloadManager ID
                        true // isTorrent
                    ));
                    
                    Toast.makeText(this, "Download started in background", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void showTorrentFileDecisionDialog(String url, String fileName, Map<String, String> headers) {
        Log.i(TAG, "Torrent File Detection: " + url);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Torrent File Detected")
                .setMessage("Do you want to stream or download this torrent file?")
                .setPositiveButton("Stream", (d, w) -> {
                    downloadAndStartTorrent(url, fileName, headers, true);
                })
                .setNegativeButton("Download", (d, w) -> {
                    downloadAndStartTorrent(url, fileName, headers, false);
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void downloadAndStartTorrent(String url, String fileName, Map<String, String> headers, boolean isStreaming) {
        Toast.makeText(this, "Downloading metadata...", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                java.net.URL u = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                if (headers != null) {
                    for (Map.Entry<String, String> e : headers.entrySet()) {
                        conn.setRequestProperty(e.getKey(), e.getValue());
                    }
                }
                conn.setInstanceFollowRedirects(true);
                conn.connect();

                File cacheDir = getCacheDir();
                // Use provided filename if available, else generic
                String safeName = (fileName != null && !fileName.isEmpty()) ? fileName : "temp.torrent";
                File torrentFile = new File(cacheDir, safeName);
                if (torrentFile.exists()) torrentFile.delete();

                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(torrentFile)) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                }

                String fileUri = "file://" + torrentFile.getAbsolutePath();
                Log.i(TAG, "Downloaded torrent file to: " + fileUri);

                runOnUiThread(() -> {
                     // Start Service
                    Intent serviceIntent = new Intent(this, TorrentService.class);
                    serviceIntent.setAction(isStreaming ? TorrentService.ACTION_START_STREAM : TorrentService.ACTION_START_DOWNLOAD);
                    serviceIntent.putExtra(TorrentService.EXTRA_MAGNET_URL, fileUri); // Reuse EXTRA_MAGNET_URL for file URI
                    startService(serviceIntent);

                    if (isStreaming) {
                        Intent playerIntent = new Intent(this, TorrentPlayerActivity.class);
                        startActivity(playerIntent);
                    } else {
                        // Add to downloads
                        DownloadsActivity.addDownload(this, new DownloadsActivity.DownloadItem(
                            safeName.replace(".torrent", ""), // Display name
                            null, 
                            fileUri,
                            -1,
                            true
                        ));
                        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error downloading torrent file", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to download torrent file", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
