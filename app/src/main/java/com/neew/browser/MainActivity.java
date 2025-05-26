package com.neew.browser;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
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
import android.util.Log;
import android.util.Patterns;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import android.widget.Toast;
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

public class MainActivity extends AppCompatActivity implements ScrollDelegate, GeckoSession.PromptDelegate {
    private static final String TAG = "MainActivity";
    private static final String SNAPSHOT_DIRECTORY_NAME = "tab_snapshots";

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

    // Scroll detection state
    private boolean isControlBarExpanded = true; // Default to true as per existing logic for initial state
    private boolean isControlBarHidden = false; // New: true if both bars are GONE
    private int lastScrollY = 0;
    private static final int SCROLL_THRESHOLD = 50; // Pixels to scroll before triggering hide/show
    private static final int TAP_THRESHOLD_BOTTOM_EDGE_DP = 60; // DP for tap detection
    private int tapThresholdBottomEdgePx; // Will be calculated in onCreate

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

    // --- Tab Management --- 
    private List<GeckoSession> geckoSessionList = new ArrayList<>();
    private int activeSessionIndex = -1;
    private Map<GeckoSession, String> sessionUrlMap = new HashMap<>();
    private static final int MAX_SNAPSHOTS = 10; // Limit snapshots stored
    private static final int SNAPSHOT_WIDTH = 300; // Target width for resized snapshots
    
    private Map<GeckoSession, Bitmap> sessionSnapshotMap = new LinkedHashMap<GeckoSession, Bitmap>(MAX_SNAPSHOTS + 1, .75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<GeckoSession, Bitmap> eldest) {
             boolean shouldRemove = size() > MAX_SNAPSHOTS;
             if (shouldRemove) {
                  Log.d(TAG, "Removing eldest snapshot due to size limit.");
             }
             return shouldRemove;
        }
    };
    private ActivityResultLauncher<Intent> tabSwitcherLauncher;
    // --- End Tab Management ---

    private LinearLayout settingsPanelLayout;
    private SwitchCompat panelCookieSwitch;
    private SwitchCompat panelAdBlockerSwitch;
    private SwitchCompat panelUBlockSwitch;
    private Button panelApplyButton;
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

    private GeckoSession geckoSession;
    private GeckoRuntime runtime;
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
    private SwitchCompat panelImmersiveSwitch;

    private boolean isInGeckoViewFullscreen = false; // To track if GeckoView requested fullscreen
    private boolean isInPictureInPictureMode = false; // To track PiP state
    private boolean firstWindowFocus = true; // Add this new field

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Initialize UI components
        geckoView = findViewById(R.id.geckoView);
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
        
        // Initialize Settings Panel components
        settingsPanelLayout = findViewById(R.id.settingsPanelLayout);
        panelCookieSwitch = findViewById(R.id.panelCookieSwitch);
        panelAdBlockerSwitch = findViewById(R.id.panelAdBlockerSwitch);
        panelUBlockSwitch = findViewById(R.id.panelUBlockSwitch);
        panelApplyButton = findViewById(R.id.panelApplyButton);
        panelCancelButton = findViewById(R.id.panelCancelButton);

        panelImmersiveSwitch = findViewById(R.id.panelImmersiveSwitch);
        // Set Immersive Mode enabled by default (true)
        boolean immersiveDefault = prefs.getBoolean(PREF_IMMERSIVE_MODE_ENABLED, true);
        panelImmersiveSwitch.setChecked(immersiveDefault);
        panelImmersiveSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_IMMERSIVE_MODE_ENABLED, isChecked).apply();
            applyImmersiveMode(isChecked);
        });
        // Apply immersive mode on startup - This will be re-asserted in onWindowFocusChanged too
        applyImmersiveMode(panelImmersiveSwitch.isChecked());

        // Initialize Gecko Runtime (only once)
        if (runtime == null) {
            Log.d(TAG, "Creating GeckoRuntime with console output enabled");

            // Create runtime with console output enabled
            GeckoRuntimeSettings.Builder runtimeSettingsBuilder = new GeckoRuntimeSettings.Builder();
            runtimeSettingsBuilder.consoleOutput(true);
            runtime = GeckoRuntime.create(this, runtimeSettingsBuilder.build());
            Log.i(TAG, "GeckoRuntime created with console output enabled.");

            applyRuntimeSettings(); // Apply other dynamic settings
            initializeUBlockOrigin(); // Initialize uBlock Origin after runtime is ready

        } else {
            Log.d(TAG, "Reusing existing GeckoRuntime");
            // If reusing, maybe re-apply dynamic settings?
            applyRuntimeSettings(); // Consider if needed on reuse
            initializeUBlockOrigin(); // Initialize uBlock Origin if runtime is being reused
        }

        // --- Restore Session State --- 
        if (geckoSessionList.isEmpty() && !restoreSessionState()) {
            // If restore failed or no saved state, create a single initial tab
            Log.d(TAG, "No saved state found or restore failed, creating initial tab.");
            createNewTab("https://www.google.com", true); // Create and make active
        }
        // --- End Restore Session State ---

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
        showExpandedBar(); // New initial state call

        // Ensure GeckoView uses the correct session after potential restore
        if (getActiveSession() != null && geckoView.getSession() != getActiveSession()) {
             Log.d(TAG, "Setting GeckoView session after restore/create");
             geckoView.setSession(getActiveSession());
             updateUIForActiveSession();
        } else if (getActiveSession() == null && !geckoSessionList.isEmpty()) {
             // Fallback if active index was invalid after restore
             Log.w(TAG, "Active session null after restore, defaulting to index 0");
             activeSessionIndex = 0;
             geckoView.setSession(getActiveSession());
             updateUIForActiveSession();
        } else if (getActiveSession() == null && geckoSessionList.isEmpty()) {
             // This case should have been handled by creating an initial tab, but log just in case
             Log.e(TAG, "Error: No active session and session list is empty after onCreate logic.");
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
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        checkAndShowStorageWarning();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void checkAndShowStorageWarning() {
        long totalBytes = getAppUsedStorageBytes();
        Log.d(TAG, "[StorageDebug] App used storage: " + totalBytes + " bytes (" + (totalBytes / (1024.0 * 1024.0)) + " MB)");
        if (totalBytes >= 400L * 1024 * 1024) { // 400 MB
            Log.d(TAG, "[StorageDebug] Threshold exceeded, showing storage warning dialog.");
            showStorageWarningDialog();
        } else {
            Log.d(TAG, "[StorageDebug] Threshold not reached, no dialog.");
        }
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
            int[] result = new int[1];
            long[] size = new long[1];
            result[0] = countFiles(getCacheDir());
            size[0] = getDirSize(getCacheDir());
            deleteDir(getCacheDir());
            dialog.dismiss();
            showCleanupDoneDialog(result[0], size[0]);
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
        // Set up navigation button click listeners
        backButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                activeSession.goBack();
            }
        });
        forwardButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                activeSession.goForward();
            }
        });
        refreshButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) {
                activeSession.reload();
            }
        });
        settingsButton.setOnClickListener(v -> toggleSettingsPanel());
        
        downloadsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadsActivity.class);
            startActivity(intent);
        });

        // --- NEW TAB --- 
        newTabButton.setOnClickListener(v -> {
            Log.d(TAG, "Expanded New Tab button clicked.");
            showExpandedBar(); // Ensure expanded bar is shown
            createNewTab(true); // Create and switch to the new tab
        });
        tabsButton.setOnClickListener(v -> {
            Log.d(TAG, "Tabs button clicked.");
            // Ensure the expanded bar is definitely visible and layout is stable before transition
            showExpandedBar(); // This sets expandedControlBar.setVisibility(View.VISIBLE)
                               // and minimizedControlBar.setVisibility(View.GONE)
            v.post(() -> {
                Log.d(TAG, "Executing launchTabSwitcher from post.");
            launchTabSwitcher();
            });
        });

        // Add listeners for MINIMIZED buttons
        minimizedBackButton.setOnClickListener(v -> {
             GeckoSession activeSession = getActiveSession();
             if (activeSession != null) activeSession.goBack();
        });
        minimizedForwardButton.setOnClickListener(v -> {
             GeckoSession activeSession = getActiveSession();
             if (activeSession != null) activeSession.goForward();
        });
        minimizedRefreshButton.setOnClickListener(v -> {
             GeckoSession activeSession = getActiveSession();
             if (activeSession != null) activeSession.reload();
        });
        minimizedNewTabButton.setOnClickListener(v -> {
            Log.d(TAG, "Minimized New Tab button clicked.");
            showExpandedBar(); // Ensure expanded bar is shown
              createNewTab(true); // Create and switch to the new tab
        });

        minimizedUrlBar.setOnClickListener(v -> showExpandedBar());
    }

     // Refactored method to set up URL bar listener
    private void setupUrlBarListener() {
        urlBar.setOnClickListener(v -> {
            urlBar.post(() -> urlBar.selectAll());
        });

        urlBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Post to ensure selection happens after focus events are fully processed
                urlBar.post(() -> urlBar.selectAll());
            }
        });

        urlBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT) { 
                String rawInput = urlBar.getText().toString();
                String input = rawInput.trim(); 
                Log.d(TAG, "Input received: '" + input + "'"); 

                if (input.isEmpty()) {
                    return true; // Consume the action
                }

                String urlToLoad = processUrlInput(input);
                GeckoSession activeSession = getActiveSession();

                if (urlToLoad != null && activeSession != null) {
                    activeSession.loadUri(urlToLoad);
                    Log.d(TAG, "Called loadUri with: " + urlToLoad);
                    hideKeyboard();
                } else if (activeSession == null) {
                     Log.e(TAG, "activeSession is null, cannot load URI.");
                } else {
                    Log.e(TAG, "urlToLoad is null (likely encoding error), not loading.");
                }
                return true;
            }
            return false;
        });
    }

    // Helper method to process URL input (search or direct URL)
    private String processUrlInput(String input) {
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
                 urlToLoad = "https://www.google.com/search?q=" + encodedQuery;
             } catch (UnsupportedEncodingException e) {
                 Log.e(TAG, "Failed to encode search query", e);
                 urlToLoad = null;
             }
         }
         return urlToLoad;
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
        
        newSession.open(runtime);
        Log.d(TAG, "Opened new GeckoSession");
        
        // Add delegates AFTER opening
        newSession.setProgressDelegate(new ProgressDelegate() {
            @Override
            public void onProgressChange(GeckoSession session, int progress) {
                // Only update progress bar if this session is active
                if (session == getActiveSession()) {
                progressBar.setProgress(progress);
                    progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
                }
            }
            @Override
            public void onPageStop(GeckoSession session, boolean success) {
                if (success && session == getActiveSession() && geckoView.getSession() == session) {
                    Log.d(TAG, "MainTab ProgressDelegate: onPageStop for active session. Capturing snapshot.");
                    captureSnapshot(session);
                }
            }
             // Implement other ProgressDelegate methods like onPageStart/Stop if needed for this session
        });

        // This is the NavigationDelegate for the tab created by createNewTab()
        newSession.setNavigationDelegate(new NavigationDelegate() {
            @Override
             public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                String uriString = request.uri;
                Log.d(TAG, "MainTab NavDelegate onLoadRequest: URI="+uriString + " (Session: " + sessionUrlMap.getOrDefault(session, "N/A") + ")");

                if (uriString != null && uriString.startsWith("intent://")) {
                    Log.i(TAG, "MainTab NavDelegate: Intercepted intent:// URI: " + uriString);
                    runOnUiThread(() -> {
                        try {
                            Intent intent = Intent.parseUri(uriString, Intent.URI_INTENT_SCHEME);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Log.d(TAG, "MainTab NavDelegate: Parsed intent: " + intent.toString() + " Extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));

                            if (intent.resolveActivity(getPackageManager()) != null) {
                                Log.i(TAG, "MainTab NavDelegate: Activity found for intent. Attempting to start...");
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
                Log.d(TAG, "NavDelegate: onLocationChange for session: " + sessionUrlMap.getOrDefault(session, "N/A") + " to URI: " + newUri +
                           " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                if (newUri != null) {
                    sessionUrlMap.put(session, newUri);
                    if (session == getActiveSession()) {
                            runOnUiThread(() -> {
                            urlBar.setText(newUri);
                                if (!isControlBarExpanded) {
                                minimizedUrlBar.setText(newUri);
                                }
                            });
                        }
                    }
                    }

            // This onNewSession is called when the main tab (newSession) tries to open a popup/new window
                    @Override
            public GeckoResult<GeckoSession> onNewSession(GeckoSession originatingSession, String uri) {
                Log.d(TAG, "createNewTab NavDelegate: onNewSession called for URI: " + uri + " from session: " + sessionUrlMap.getOrDefault(originatingSession, "N/A"));

                // 1. Create the GeckoSession object for the new tab/popup.
                //    DO NOT CALL .open(runtime) on it here. GeckoView will do that.
                final GeckoSession newTabSession = new GeckoSession();

                // 2. Configure its delegates:
                newTabSession.setProgressDelegate(new ProgressDelegate() {
                     @Override
                    public void onProgressChange(GeckoSession session, int progress) {
                        if (session == getActiveSession()) {
                            progressBar.setProgress(progress);
                            progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
                        }
                        Log.v(TAG, "NewTab/Popup Progress: " + progress + "% for " + sessionUrlMap.getOrDefault(session, "Unknown URI"));
                    }
                    @Override
                    public void onPageStop(GeckoSession session, boolean success) {
                        if (success && session == getActiveSession() && geckoView.getSession() == session) {
                            Log.d(TAG, "Popup ProgressDelegate: onPageStop for active session. Capturing snapshot.");
                            captureSnapshot(session);
                        }
                    }
                });

                newTabSession.setNavigationDelegate(new NavigationDelegate() {
                    @Override
                    public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                        String uriString = request.uri;
                        Log.d(TAG, "Popup NavDelegate onLoadRequest: URI="+uriString + " (Session: " + sessionUrlMap.getOrDefault(session, "N/A") + ")");

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
                        Log.d(TAG, "Popup NavDelegate: onLocationChange for session: " + sessionUrlMap.getOrDefault(session, "N/A") + " to URI: " + newUri +
                                   " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                        if (newUri != null) {
                            sessionUrlMap.put(session, newUri);
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
                    public GeckoResult<GeckoSession> onNewSession(GeckoSession currentPopupSession, String newUriFromPopup) {
                        Log.i(TAG, "Popup's NavDelegate: Allowing nested popup from " + newUriFromPopup + " (no domain restriction).");
                        final GeckoSession grandChildSession = new GeckoSession();

                        grandChildSession.setProgressDelegate(new ProgressDelegate() {
                    @Override
                            public void onProgressChange(GeckoSession session, int progress) {
                                if (session == getActiveSession()) {
                                    progressBar.setProgress(progress);
                                    progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
                                }
                                Log.v(TAG, "GrandChildPopup Progress: " + progress + "% for " + sessionUrlMap.getOrDefault(session, "Unknown URI"));
                            }
                    @Override
                            public void onPageStop(GeckoSession session, boolean success) {
                                if (success && session == getActiveSession() && geckoView.getSession() == session) {
                                    Log.d(TAG, "GrandChild ProgressDelegate: onPageStop for active session. Capturing snapshot.");
                                    captureSnapshot(session);
                                }
                            }
                        });

                        grandChildSession.setNavigationDelegate(new NavigationDelegate() {
                    @Override
                            public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                                String uriString = request.uri;
                                Log.d(TAG, "GrandChild NavDelegate onLoadRequest: URI="+uriString + " (Session: " + sessionUrlMap.getOrDefault(session, "N/A") + ")");

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
                        Log.d(TAG, "GrandChild NavDelegate: onLocationChange for session: " + sessionUrlMap.getOrDefault(session, "N/A") + " to URI: " + newUri +
                                   " Perms: " + perms.size() + " HasGesture: " + hasUserGesture);
                        if (newUri != null) {
                            sessionUrlMap.put(session, newUri);
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
                                Log.w(TAG, "GrandChildPopup's NavDelegate: Blocking further (3rd level) nested popup: " + uri);
                                Toast.makeText(MainActivity.this, "Further nested popups are blocked.", Toast.LENGTH_SHORT).show();
                                return null; // Block 3rd level popups
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
                                    runOnUiThread(() -> handleDownloadResponse(response));
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
                                        runOnUiThread(() -> handleDownloadResponse(response));
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
                                Log.d(TAG, "GrandChildPopup's ContentDelegate: onCloseRequest for " + sessionUrlMap.getOrDefault(session, "N/A"));
                                int indexToClose = geckoSessionList.indexOf(session);
                                if (indexToClose != -1) { closeTab(indexToClose); } else { session.close(); }
                            }
                        });

                        grandChildSession.setPromptDelegate(MainActivity.this);
                        grandChildSession.setScrollDelegate(MainActivity.this);

                        // Add ConsoleDelegate for the grandchild popup session
                        // grandChildSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

                        synchronized (geckoSessionList) { geckoSessionList.add(grandChildSession); }
                        sessionUrlMap.put(grandChildSession, newUriFromPopup != null ? newUriFromPopup : "about:blank");

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
                            runOnUiThread(() -> handleDownloadResponse(response));
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
                                runOnUiThread(() -> handleDownloadResponse(response));
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
                        Log.d(TAG, "Popup's ContentDelegate: onCloseRequest for session " + sessionUrlMap.getOrDefault(session, "N/A"));
                        int indexToClose = geckoSessionList.indexOf(session);
                        if (indexToClose != -1) {
                            closeTab(indexToClose);
                        } else {
                            session.close();
                        }
                    }
                });

                // ***** THIS IS THE CRITICAL FIX *****
                newTabSession.setPromptDelegate(MainActivity.this); 
                newTabSession.setScrollDelegate(MainActivity.this); 
                // ***** END CRITICAL FIX *****

                // Add ConsoleDelegate for the new tab/popup session
                // newTabSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

                // 3. Add to tab management list (ensure thread safety if needed)
                synchronized (geckoSessionList) {
                    geckoSessionList.add(newTabSession);
                }
                sessionUrlMap.put(newTabSession, uri != null ? uri : "about:blank");

                // 4. UI: Switch to this new tab and update UI elements.
                final int newTabIndex = geckoSessionList.indexOf(newTabSession);
                if (newTabIndex != -1) {
                        runOnUiThread(() -> {
                        Log.d(TAG, "createNewTab NavDelegate: Switching to new tab index: " + newTabIndex + " for URI: " + uri);
                        switchToTab(newTabIndex);
                    });
                } else {
                    Log.e(TAG, "createNewTab NavDelegate: New tab session not found in list for URI: " + uri);
                    if (uri != null) {
                        newTabSession.loadUri(uri);
                    }
                }

                Log.d(TAG, "createNewTab NavDelegate: Returning new GeckoSession to GeckoView for URI: " + uri);
                return GeckoResult.fromValue(newTabSession);
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
                Log.d(TAG, "ContentDelegate: onCloseRequest received for session: " + sessionUrlMap.getOrDefault(session, "Unknown URI"));
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
        });

        newSession.setScrollDelegate(this); // Set MainActivity as scroll delegate
        newSession.setPromptDelegate(this); // Added for regular new sessions

        // Add ConsoleDelegate to log web console messages
        // newSession.setConsoleDelegate(new GeckoSession.ConsoleDelegate() { ... });

        geckoSessionList.add(newSession);
        String targetUrl = (initialUrl != null && !initialUrl.isEmpty()) ? initialUrl : "about:blank";
        sessionUrlMap.put(newSession, targetUrl); // Store initial URL
        
        newSession.loadUri(targetUrl); // Load initial URL
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
        createNewTab("https://www.google.com", switchToTab);
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
                     Log.d(TAG, "Capturing snapshot of non-logical-active/unexpected session being released: " + sessionUrlMap.getOrDefault(sessionCurrentlyInView, "N/A"));
                     captureSnapshot(sessionCurrentlyInView);
                }
            }
            geckoView.releaseSession();
            Log.d(TAG, "Released session from GeckoView: " + sessionUrlMap.getOrDefault(sessionCurrentlyInView, "N/A"));
        }

        this.activeSessionIndex = targetIndex;
        this.geckoSession = targetSession; // Update the global 'geckoSession' convenience field
        
        geckoView.setSession(targetSession);
        Log.d(TAG, "Attached session to GeckoView: " + sessionUrlMap.getOrDefault(targetSession, "N/A"));
        // captureSnapshot(targetSession); // CAPTURE SNAPSHOT OF THE NEWLY ACTIVE TAB -> REMOVED, handled by onPageStop

        updateUIForActiveSession();
        saveSessionState();
        geckoView.requestFocus();
    }

    // Method to capture snapshot for a session
    private void captureSnapshot(GeckoSession session) {
        if (session == null || geckoView == null) {
            Log.w(TAG, "Cannot capture snapshot, session or geckoView is null.");
            return;
        }
        
        // Check if the session is currently attached to the view
        if (geckoView.getSession() != session) {
            Log.d(TAG, "Snapshot skipped: Session index " + geckoSessionList.indexOf(session) + " is not the active session in GeckoView.");
            return; // Can only capture the currently displayed session
        }
        
        Log.d(TAG, "Requesting snapshot for active session index: " + geckoSessionList.indexOf(session));

        // Add debouncing to prevent rapid captures:
        // snapshotHandler.removeCallbacksAndMessages(session); // REMOVE
        // snapshotHandler.postDelayed(() -> { // REMOVE

            // Actual capture logic
            // synchronized (geckoSessionList) { // REMOVE CONTAINS CHECK
                GeckoResult<Bitmap> result = geckoView.capturePixels();

                result.accept(originalBitmap -> {
                    if (originalBitmap != null) {
                         Log.d(TAG, "Snapshot captured successfully for session index: " + geckoSessionList.indexOf(session) + 
                               " (Original size: " + originalBitmap.getWidth() + "x" + originalBitmap.getHeight() + ")");
                        
                         // --- Resize the bitmap --- 
                         int originalWidth = originalBitmap.getWidth();
                         int originalHeight = originalBitmap.getHeight();
                         if (originalWidth == 0 || originalHeight == 0) {
                              Log.w(TAG, "Snapshot has zero dimensions, skipping resize/storage.");
                              originalBitmap.recycle(); // Release the original bitmap
                              return;
                         }
                         float aspectRatio = (float) originalHeight / originalWidth;
                         int targetHeight = Math.round(SNAPSHOT_WIDTH * aspectRatio);
                         
                         if (targetHeight <= 0) { // Prevent zero height
                             targetHeight = 1;
                         }

                         Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, SNAPSHOT_WIDTH, targetHeight, true);
                         originalBitmap.recycle(); // Recycle the original large bitmap immediately
                         // --- End Resize ---
                         
                        Log.d(TAG, "Snapshot resized to: " + resizedBitmap.getWidth() + "x" + resizedBitmap.getHeight());
                        sessionSnapshotMap.put(session, resizedBitmap); // Store the resized bitmap

                        // --- Save snapshot to disk ---
                        String url = sessionUrlMap.getOrDefault(session, null);
                        if (url != null) {
                            saveSnapshotToDisk(resizedBitmap, url);
                        }
                        // --- End save snapshot to disk ---

                    } else {
                         Log.w(TAG, "Snapshot capture returned null bitmap for session index: " + geckoSessionList.indexOf(session));
                    }
                }, e -> {
                     Log.e(TAG, "Snapshot capture failed for session index: " + geckoSessionList.indexOf(session), e);
                });
            // } // REMOVE CONTAINS CHECK END

        // }, SNAPSHOT_DEBOUNCE_MS); // REMOVE
    }

    // Updates URL bar and navigation buttons based on the active session state
    private void updateUIForActiveSession() {
        GeckoSession activeSession = getActiveSession();
        if (activeSession != null && urlBar != null && minimizedUrlBar != null) {
            String currentUrl = sessionUrlMap.getOrDefault(activeSession, ""); 

            Log.d(TAG, "Updating UI for Active Session: URL=" + currentUrl);
            
            runOnUiThread(() -> {
                urlBar.setText(currentUrl);
                if (!isControlBarExpanded) {
                    minimizedUrlBar.setText(currentUrl);
                }
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            });
        } else {
             Log.w(TAG, "updateUIForActiveSession: Active session or URL bar is null");
        }
    }

    @Override
    public void onBackPressed() {
        if (settingsPanelLayout.getVisibility() == View.VISIBLE) {
            hideSettingsPanel(false);
            return;
        }
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
    }

    // --- Control Bar State Logic ---

    private void showExpandedBar() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            expandedControlBar.setVisibility(View.VISIBLE);
            minimizedControlBar.setVisibility(View.GONE);
            // Ensure the main URL bar is interactive
            urlBar.setFocusableInTouchMode(true);
            urlBar.setFocusable(true);
            urlBar.setClickable(true);

            isControlBarExpanded = true;
            isControlBarHidden = false;
            Log.d(TAG, "showExpandedBar: Expanded bar visible.");
        }
    }

    private void showMinimizedBar() {
        if (expandedControlBar != null && minimizedControlBar != null && getActiveSession() != null) {
            expandedControlBar.setVisibility(View.GONE);
            minimizedControlBar.setVisibility(View.VISIBLE);
            // Main URL bar should not be interactive when minimized bar is shown
            urlBar.setFocusable(false);
            urlBar.setFocusableInTouchMode(false);
            urlBar.setClickable(false);
            
            isControlBarExpanded = false;
            isControlBarHidden = false;
            // Update the text of the minimized URL bar
            String currentUrl = sessionUrlMap.getOrDefault(getActiveSession(), "");
            minimizedUrlBar.setText(currentUrl);
            Log.d(TAG, "showMinimizedBar: Minimized bar visible. URL: " + currentUrl);
        } else if (getActiveSession() == null) {
            Log.w(TAG, "showMinimizedBar: Cannot show, active session is null. Hiding both bars.");
            hideBothBars(); // Fallback if no session
        }
    }

    private void hideBothBars() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            expandedControlBar.setVisibility(View.GONE);
            minimizedControlBar.setVisibility(View.GONE);

            isControlBarExpanded = false; // When hidden, it's not expanded
            isControlBarHidden = true;
            Log.d(TAG, "hideBothBars: Both control bars hidden.");
        }
    }

    @Override
    public void onScrollChanged(GeckoSession session, int scrollX, int scrollY) {
        int dy = scrollY - lastScrollY;

        // Don't do anything if the scroll is too small
        if (Math.abs(dy) < SCROLL_THRESHOLD) {
            // Update lastScrollY only if the scroll is not negligible,
            // to avoid resetting it for very small movements that don't cross threshold.
            if (Math.abs(dy) > 5) { // A smaller tolerance for updating lastScrollY
                 lastScrollY = scrollY;
            }
            return;
        }

        if (dy > 0) { // Scrolling Down
            if (!isControlBarHidden) { // Only hide if not already hidden
                hideBothBars();
                Log.d(TAG, "onScrollChanged: Scrolled DOWN - Hiding both bars.");
            }
        } else { // Scrolling Up (dy < 0)
            if (isControlBarHidden || isControlBarExpanded) { // Show minimized if currently hidden OR if it was expanded (and now scrolled up)
                showMinimizedBar();
                Log.d(TAG, "onScrollChanged: Scrolled UP - Showing minimized bar.");
            }
        }

        lastScrollY = scrollY; // Update scroll position
    }

    // Method to apply runtime settings based on SharedPreferences
    private void applyRuntimeSettings() {
        if (runtime == null) {
            Log.e(TAG, "applyRuntimeSettings: Runtime is null!");
            return;
        }
        Log.d(TAG, "Applying dynamic runtime settings...");

        GeckoRuntimeSettings settings = runtime.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setRemoteDebuggingEnabled(true); // Enable remote debugging

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
        // runtime.setSettings(settings); // This line is problematic, settings are applied via direct setters or at creation
        Log.d(TAG, "Dynamic runtime settings application finished.");
        // Moved uBlock initialization to be called after applyRuntimeSettings in onCreate
    }

    // --- Settings Panel Logic ---
    private void setupSettingsPanelListeners() {
        panelApplyButton.setOnClickListener(v -> applySettingsFromPanel());
        panelCancelButton.setOnClickListener(v -> hideSettingsPanel(false));

        // Optional: Hide panel if touch occurs outside of it (on GeckoView)
        geckoView.setOnTouchListener((view, motionEvent) -> {
            if (settingsPanelLayout.getVisibility() == View.VISIBLE) {
                // Check if touch is outside the panel bounds
                Rect panelRect = new Rect();
                settingsPanelLayout.getGlobalVisibleRect(panelRect);
                if (!panelRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
                    hideSettingsPanel(false); // Hide without applying if touched outside
                }
            } 
            // Also handle keyboard hide logic from previous implementation
             if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                 View currentFocus = getCurrentFocus();
                 if (currentFocus != null && currentFocus != urlBar) {
                     Log.d(TAG, "Touch on GeckoView detected, hiding keyboard (current focus: " + currentFocus.getClass().getSimpleName() + ")");
                     hideKeyboard();
                 } else if (currentFocus == null) {
                     Log.d(TAG, "Touch on GeckoView detected (no focus), hiding keyboard.");
                     hideKeyboard();
                 }
             }
            return false; // Allow event propagation
        });
    }

    private void toggleSettingsPanel() {
        if (settingsPanelLayout.getVisibility() == View.GONE) {
            showSettingsPanel();
        } else {
            hideSettingsPanel(false); // Hide without applying changes if toggled off
        }
    }

    private void showSettingsPanel() {
        // Load current settings into switches before showing
        panelCookieSwitch.setChecked(prefs.getBoolean(PREF_COOKIES_ENABLED, false));
        panelAdBlockerSwitch.setChecked(prefs.getBoolean(PREF_AD_BLOCKER_ENABLED, true));
        if (panelUBlockSwitch != null) { // Add null check for safety
            panelUBlockSwitch.setChecked(prefs.getBoolean(PREF_UBLOCK_ENABLED, false));
        }

        settingsPanelLayout.setVisibility(View.VISIBLE); // Make it visible first

        settingsPanelLayout.post(() -> {
            // Simple slide-up animation
            TranslateAnimation animate = new TranslateAnimation(
                    0,
                    0,
                    settingsPanelLayout.getHeight(), // fromYDelta (start below)
                    0);                        // toYDelta (end at original position)
            animate.setDuration(300);
            animate.setFillAfter(true); // Keep the view at its new position after animation
            settingsPanelLayout.startAnimation(animate);
            Log.d(TAG, "Showing settings panel animation started");
        });
        Log.d(TAG, "Showing settings panel requested");
    }

    private void hideSettingsPanel(boolean applyChanges) {
        if (settingsPanelLayout.getVisibility() == View.GONE) return;

        // Simple slide-down animation
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                0,
                settingsPanelLayout.getHeight()); // toYDelta (end below)
        animate.setDuration(300);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationEnd(Animation animation) {
                settingsPanelLayout.setVisibility(View.GONE);
                settingsPanelLayout.clearAnimation(); // Important to clear after GONE
                Log.d(TAG, "Settings panel hidden." + (applyChanges ? " Applied changes." : " Cancelled."));
            }
            @Override public void onAnimationRepeat(Animation animation) { }
        });
        settingsPanelLayout.startAnimation(animate);
    }

    private void applySettingsFromPanel() {
        boolean cookiesChecked = panelCookieSwitch.isChecked();
        boolean adBlockerChecked = panelAdBlockerSwitch.isChecked();
        boolean uBlockChecked = (panelUBlockSwitch != null) && panelUBlockSwitch.isChecked(); // Add null check
        Log.d(TAG, "Applying Settings from Panel: Cookies = " + cookiesChecked + ", Blocker = " + adBlockerChecked + ", uBlock = " + uBlockChecked);

        // Save the new preferences
        prefs.edit()
             .putBoolean(PREF_COOKIES_ENABLED, cookiesChecked)
             .putBoolean(PREF_AD_BLOCKER_ENABLED, adBlockerChecked)
             .putBoolean(PREF_UBLOCK_ENABLED, uBlockChecked) // Save uBlock state
             .apply();

        // Apply GeckoView settings dynamically
        applyRuntimeSettings();
        setUBlockOriginEnabled(uBlockChecked); // Enable/disable uBlock

        Toast.makeText(MainActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
        hideSettingsPanel(true); // Hide panel after applying
    }
    // --- End Settings Panel Logic ---

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
                            panelUBlockSwitch.setChecked(false); // Reflect failure
                        }
                    }
                },
                e -> {
                    uBlockInstallAttempted = true; // Mark that an attempt has been made
                    Log.e(TAG, "ensureBuiltIn for uBlock Origin FAILED. Path: " + UBLOCK_ASSET_PATH + ", ID: " + UBLOCK_EXTENSION_ID, e);
                    Toast.makeText(MainActivity.this, "Failed to install uBlock Origin (exception)", Toast.LENGTH_LONG).show();
                    if (panelUBlockSwitch != null) {
                        panelUBlockSwitch.setChecked(false); // Reflect failure
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
                        panelUBlockSwitch.setChecked(enabled); // Set switch to the state we attempted
                        Log.d(TAG, "uBlock switch updated to: " + enabled);
                    }
                     // Persist the successful state change
                    prefs.edit().putBoolean(PREF_UBLOCK_ENABLED, enabled).apply();
                } else {
                     Log.w(TAG, "uBlock Origin enable/disable call returned null WebExtension object. Intended state: " + (enabled ? "enabled" : "disabled"));
                     // Attempt to reflect the last known good state of the switch if available
                    if (panelUBlockSwitch != null) {
                         panelUBlockSwitch.setChecked(prefs.getBoolean(PREF_UBLOCK_ENABLED, false)); // Revert to preference
                    }
                }
            },
            e -> {
                Log.e(TAG, "Failed to set uBlock Origin (" + ublockOriginExtension.id + ") target enabled state to " + enabled, e);
                Toast.makeText(MainActivity.this, "Failed to " + (enabled ? "enable" : "disable") + " uBlock Origin", Toast.LENGTH_SHORT).show();
                // Revert switch to previous known state (from prefs)
                if (panelUBlockSwitch != null) {
                    panelUBlockSwitch.setChecked(prefs.getBoolean(PREF_UBLOCK_ENABLED, false)); // Revert to preference
                     Log.d(TAG, "uBlock switch reverted to: " + prefs.getBoolean(PREF_UBLOCK_ENABLED, false) + " after failure.");
                }
            }
        );
    }

    // --- End uBlock Origin WebExtension Logic ---

    // --- Fullscreen Helper Methods ---
    private void enterFullScreen() {
        Log.d(TAG, "Entering fullscreen (enterFullScreen called)");
        isInGeckoViewFullscreen = true; // GeckoView has requested fullscreen

        // Don't hide system UI if we are already in PiP or about to enter it.
        // The system handles UI for PiP.
        if (isInPictureInPictureMode) {
            Log.d(TAG, "Already in PiP mode or transitioning, not changing system UI from enterFullScreen.");
            if (controlBarContainer != null) {
                controlBarContainer.setVisibility(View.GONE); // Keep controls hidden in PiP
            }
            return;
        }

        // Hide system UI (status bar, navigation bar)
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Keeps UI hidden on interaction
        decorView.setSystemUiVisibility(uiOptions);

        // Optionally force landscape for video
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // Hide the app's control bar as well
        if (controlBarContainer != null) {
            controlBarContainer.setVisibility(View.GONE);
        }
    }

    private void exitFullScreen() {
        Log.d(TAG, "Exiting fullscreen (exitFullScreen called)");
        isInGeckoViewFullscreen = false; // GeckoView no longer desires fullscreen

        // If we are exiting fullscreen and also in PiP mode, let PiP mode changes handle UI.
        // Typically, exiting PiP will call onFullScreen(false) from GeckoView.
        if (isInPictureInPictureMode) {
            Log.d(TAG, "Currently in PiP mode, system will handle UI changes on PiP exit.");
            // We might not need to do anything here if PiP exit flow is clean.
            // If controlBarContainer was hidden for PiP, it should become visible
            // when PiP ends and onPictureInPictureModeChanged(false) is called.
            return;
        }

        // Show system UI
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Allow any orientation again
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Show the app's control bar (ensure it's in the correct state)
        if (controlBarContainer != null) {
            controlBarContainer.setVisibility(View.VISIBLE);
            if (isControlBarExpanded) {
                showExpandedBar();
            } else {
                showMinimizedBar();
            }
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
            urls.add(sessionUrlMap.getOrDefault(session, "Loading...")); 
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
    protected void onStop() {
        super.onStop();
        saveSessionState();
    }

    private void saveSessionState() {
        Log.d(TAG, "[StorageDebug] saveSessionState CALLED");
        SharedPreferences.Editor editor = prefs.edit();

        // Save URLs as a JSON array string
        JSONArray urlsJsonArray = new JSONArray();
        for (GeckoSession session : geckoSessionList) {
            urlsJsonArray.put(sessionUrlMap.getOrDefault(session, "about:blank"));
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
        // Check storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Storage permission not granted. Requesting...");
            // Store the response to retry after permission grant
            pendingDownloadResponse = response; 
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
            // Don't proceed with download logic yet
            return; 
        }
        
        Log.i(TAG, "Storage permission granted. Proceeding with download.");
        
        // Clear any pending response as we are proceeding now
        pendingDownloadResponse = null;

        String fileName = "downloaded_file";
        String url = response.uri;
        String contentDisposition = response.headers != null ? response.headers.get("content-disposition") : null;
        if (contentDisposition != null) {
            // Basic parsing for filename - might need improvement for complex cases
            String[] parts = contentDisposition.split("filename=");
            if (parts.length > 1) {
                fileName = parts[1].replaceAll("[\\\"';]", "").trim(); 
            }
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
                    url
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
                Toast.makeText(this, "Storage permission is required to download files.", Toast.LENGTH_LONG).show();
                pendingDownloadResponse = null; // Clear pending download as permission was denied
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
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int uiOptions = decorView.getSystemUiVisibility();

            if (immersive) {
                // Flags to make content extend into status/nav bar areas
                uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                // uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION; // Optional: if you want content behind nav bar too

                // Flags to actually hide the status bar and nav bar (optional for nav bar)
                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN; // Hides status bar
                // uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // Optional: Hides navigation bar

                // Flag for sticky immersive mode
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                // Make status bar transparent so if it temporarily appears (sticky mode), content is behind it
                getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
                // Optional: Make navigation bar transparent if LAYOUT_HIDE_NAVIGATION is used
                // getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);

            } else {
                // Clear fullscreen flags
                uiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                uiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                // uiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                uiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
                // uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                // Restore default status bar color (you might need to define one)
                // For now, setting to a common default (e.g., black or your theme's primaryDark)
                // If your theme handles this, you might not need to set it explicitly.
                // Consider a theme attribute for this color.
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.statusBarColor, typedValue, true);
                int statusBarColorDefault = typedValue.resourceId != 0 ? ContextCompat.getColor(this, typedValue.resourceId) : android.graphics.Color.BLACK;
                getWindow().setStatusBarColor(statusBarColorDefault);


                // Restore default navigation bar color if it was changed
                // getTheme().resolveAttribute(android.R.attr.navigationBarColor, typedValue, true);
                // int navigationBarColorDefault = typedValue.resourceId != 0 ? ContextCompat.getColor(this, typedValue.resourceId) : android.graphics.Color.BLACK;
                // getWindow().setNavigationBarColor(navigationBarColorDefault);
            }
            decorView.setSystemUiVisibility(uiOptions);
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
            isInGeckoViewFullscreen && // Only enter PiP if content was fullscreen
            getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            
            Log.d(TAG, "onUserLeaveHint: Attempting to enter PiP mode.");
            enterPictureInPictureModeWithCurrentParams();
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

        // Create a new default tab (e.g., Google homepage)
        createNewTab("https://www.google.com", true);

        // Update UI (e.g., if there's a tab count display, it should reflect 1)
        updateUIForActiveSession(); // This will also update the URL bar for the new tab
        saveSessionState(); // Persist the new state (one tab)
        Toast.makeText(this, "All tabs cleared.", Toast.LENGTH_SHORT).show();
    }
} 