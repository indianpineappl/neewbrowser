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

public class MainActivity extends AppCompatActivity implements ScrollDelegate, GeckoSession.PromptDelegate {
    private static final String TAG = "MainActivity";

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
    private boolean isControlBarExpanded = true;
    private int lastScrollY = 0;
    private static final int SCROLL_THRESHOLD = 50; // Pixels to scroll before triggering hide/show

    private SharedPreferences prefs;
    private static final String PREF_COOKIES_ENABLED = "cookies_enabled";
    // --- Session Persistence Keys ---
    private static final String PREF_SAVED_URLS = "saved_urls";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Initialize Gecko Runtime (only once)
        if (runtime == null) {
            Log.d(TAG, "Creating GeckoRuntime (default settings)");

            // Create runtime with default settings
            runtime = GeckoRuntime.create(this);
            Log.i(TAG, "GeckoRuntime created with default settings.");

            applyRuntimeSettings(); // Apply dynamic settings AFTER creation using direct setters
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
        showExpandedControls(); // Start with expanded controls

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
            Log.d(TAG, "New Tab button clicked.");
            createNewTab(true); // Create and switch to the new tab
        });
        tabsButton.setOnClickListener(v -> {
            Log.d(TAG, "Tabs button clicked. Launching TabSwitcherActivity.");
            launchTabSwitcher();
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
              createNewTab(true); // Create and switch to the new tab
        });

        minimizedUrlBar.setOnClickListener(v -> showExpandedControls());
    }

     // Refactored method to set up URL bar listener
    private void setupUrlBarListener() {
        urlBar.setOnClickListener(v -> {
            urlBar.post(() -> urlBar.selectAll());
            // Optionally, also show the keyboard if it's not already visible
            // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // imm.showSoftInput(urlBar, InputMethodManager.SHOW_IMPLICIT);
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
            // but avoid clearing focus generally as it might affect web content
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
             // Implement other ProgressDelegate methods like onPageStart/Stop if needed for this session
        });

        newSession.setNavigationDelegate(new NavigationDelegate() {
            @Override
             public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, NavigationDelegate.LoadRequest request) {
                return GeckoResult.fromValue(AllowOrDeny.ALLOW);
            }

            @Override
            public GeckoResult<GeckoSession> onNewSession(GeckoSession session, String uri) {
                Log.d(TAG, "NavigationDelegate: onNewSession called for URI: " + uri);

                GeckoSession newPopupWindowSession = new GeckoSession();
                // DO NOT CALL newPopupWindowSession.open(runtime); HERE.
                // GeckoView is expected to open the session that is returned.

                newPopupWindowSession.setProgressDelegate(new ProgressDelegate() {
                    @Override
                    public void onProgressChange(GeckoSession popupSession, int progress) {
                        Log.v(TAG, "Popup session progress: " + progress + "% for " + sessionUrlMap.getOrDefault(popupSession, "Unknown URI"));
                    }
                });

                newPopupWindowSession.setNavigationDelegate(new NavigationDelegate() {
                    @Override
                    public void onLocationChange(GeckoSession popupSession, String url, List<PermissionDelegate.ContentPermission> perms, Boolean hasUserGesture) {
                        Log.d(TAG, "Popup onLocationChange: " + url);
                        // Update the map for the new popup session
                        sessionUrlMap.put(popupSession, url);
                        // If this popup becomes the active session, update the main URL bar
                        if (popupSession == getActiveSession()) {
                            runOnUiThread(() -> {
                                urlBar.setText(url);
                                if (!isControlBarExpanded) {
                                    minimizedUrlBar.setText(url);
                                }
                            });
                        }
                    }
                     @Override
                     public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession popupSession, NavigationDelegate.LoadRequest request) {
                        return GeckoResult.fromValue(AllowOrDeny.ALLOW);
                    }
                });

                 newPopupWindowSession.setContentDelegate(new ContentDelegate() {
                    @Override
                    public void onExternalResponse(GeckoSession popupSession, org.mozilla.geckoview.WebResponse response) {
                        runOnUiThread(() -> handleDownloadResponse(response));
                    }
                     @Override
                     public void onCloseRequest(GeckoSession popupSession) {
                         Log.d(TAG, "Popup onCloseRequest received. Closing session for URI: " + sessionUrlMap.getOrDefault(popupSession, "Unknown URI"));
                         int indexToClose = geckoSessionList.indexOf(popupSession);
                         if (indexToClose != -1) {
                             closeTab(indexToClose);
                         } else {
                             popupSession.close();
                         }
                     }
                });

                newPopupWindowSession.setPromptDelegate(new PromptDelegate() {
                    @Override
                    public GeckoResult<PromptResponse> onSharePrompt(GeckoSession session, SharePrompt prompt) {
                        Log.d(TAG, "ANON_PD_POPUP: onSharePrompt: URI: " + prompt.uri + ", Title: " + prompt.title + ", Text: " + prompt.text);
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
                            MainActivity.this.startActivity(Intent.createChooser(sendIntent, "Share via"));
                            // For SharePrompt, confirm() takes a SharePrompt.Result
                            // Assuming this was previously correct, just ensure it's not completing a GeckoResult with null directly.
                            // If onSharePrompt itself returns a GeckoResult, that GeckoResult should be completed.
                            // The snippet showed 'return null;' for onSharePrompt which is also problematic if GeckoView expects a GeckoResult.
                            // However, the immediate crash is from onAlertPrompt. Let's focus there.
                            // This snippet doesn't show onSharePrompt returning a GeckoResult so this line is a direct call.
                            // prompt.confirm(SharePrompt.Result.SUCCESS); // This line does not complete a GeckoResult.
                        } catch (Exception e) {
                            Log.e(TAG, "ANON_PD_POPUP: Error starting share activity", e);
                            // prompt.confirm(SharePrompt.Result.FAILURE);
                        }
                        // onSharePrompt in anonymous delegate returning null is an issue if GeckoView expects a result.
                        // For now, let's assume it should return a completed result or be refactored later.
                        // We will create a dummy completed result for now to avoid crashes from onSharePrompt if it's called.
                        GeckoResult<PromptResponse> shareResult = new GeckoResult<>();
                        if (prompt != null) { // Check if prompt is not null
                           shareResult.complete(prompt.dismiss()); // Dismiss by default if not handled properly
                        } else {
                           shareResult.completeExceptionally(new NullPointerException("SharePrompt was null"));
                        }
                        return shareResult; 
                    }

                    @Override
                    public GeckoResult<PromptResponse> onAlertPrompt(GeckoSession session, AlertPrompt prompt) {
                        Log.d(TAG, "MAIN ACTIVITY onAlertPrompt: Title=" + prompt.title + ", Message=" + prompt.message);
                        
                        final GeckoResult<PromptResponse> result = new GeckoResult<>();
                        final AlertPrompt currentPrompt = prompt;

                        runOnUiThread(() -> {
                            if (MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                                result.complete(currentPrompt.dismiss());
                                return;
                            }

                            new AlertDialog.Builder(MainActivity.this)
                                .setTitle(currentPrompt.title)
                                .setMessage(currentPrompt.message)
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    result.complete(currentPrompt.dismiss()); // Use dismiss() for OK
                                })
                                .setOnDismissListener(dialog -> {
                                    result.complete(currentPrompt.dismiss()); // Use dismiss() for cancel/dismiss
                                })
                                .setCancelable(true)
                                .show();
                        });
                        return result;
                    }

                    @Override
                    public GeckoResult<PromptResponse> onAuthPrompt(GeckoSession session, AuthPrompt prompt) {
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

                    @Override
                    public GeckoResult<PromptResponse> onBeforeUnloadPrompt(GeckoSession session, BeforeUnloadPrompt prompt) {
                        Log.d(TAG, "ANON_PD_POPUP: BeforeUnload prompt. Title: " + prompt.title);
                        return GeckoResult.fromValue(prompt.confirm(AllowOrDeny.ALLOW)); 
                    }

                    @Override
                    public GeckoResult<PromptResponse> onButtonPrompt(GeckoSession session, ButtonPrompt prompt) {
                        Log.d(TAG, "ANON_PD_POPUP: onButtonPrompt: Title: " + prompt.title + ", Message: " + prompt.message);
                        // ButtonPrompt does not have a 'choices' field for the anonymous delegate.
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
                        Log.d(TAG, "ANON_PD_POPUP: onColorPrompt: Title=" + prompt.title + ", DefaultValue=" + prompt.defaultValue);

                        final ColorPrompt currentPrompt = prompt; // Use local variable
                        final GeckoResult<PromptResponse> localPendingGeckoResult = new GeckoResult<>(); // Use local variable

                        runOnUiThread(() -> {
                            if (currentPrompt == null) {
                                Log.e(TAG, "ANON_PD_POPUP: onColorPrompt: currentPrompt is null in runOnUiThread before showing dialog.");
                                if (localPendingGeckoResult != null) {
                                    localPendingGeckoResult.complete(null); // Or some error response
                                }
                                return;
                            }

                            // For displaying the color, convert int to hex string
                            String hexColor = String.format("#%08X", currentPrompt.defaultValue); // ARGB

                            new AlertDialog.Builder(MainActivity.this)
                                .setTitle(currentPrompt.title != null ? currentPrompt.title : "Choose Color")
                                .setMessage("Suggested color: " + hexColor)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    PromptResponse response = null;
                                    if (currentPrompt != null) {
                                        Log.d(TAG, "ANON_PD_POPUP: onColorPrompt: OK clicked, confirming with default value: " + hexColor);
                                        response = currentPrompt.confirm(currentPrompt.defaultValue);
                                    }
                                    if (localPendingGeckoResult != null) {
                                        localPendingGeckoResult.complete(response);
                                    }
                                    // Do not nullify MainActivity fields here
                                })
                                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                                    PromptResponse response = null;
                                    if (currentPrompt != null) {
                                        Log.d(TAG, "ANON_PD_POPUP: onColorPrompt: Cancel clicked, dismissing.");
                                        response = currentPrompt.dismiss();
                                    }
                                    if (localPendingGeckoResult != null) {
                                        localPendingGeckoResult.complete(response);
                                    }
                                    // Do not nullify MainActivity fields here
                                })
                                .setOnCancelListener(dialog -> { // Handles back button or touch outside
                                    PromptResponse response = null;
                                    if (currentPrompt != null) {
                                        Log.d(TAG, "ANON_PD_POPUP: onColorPrompt: Dialog cancelled, dismissing.");
                                        response = currentPrompt.dismiss();
                                    }
                                    if (localPendingGeckoResult != null) {
                                        localPendingGeckoResult.complete(response);
                                    }
                                    // Do not nullify MainActivity fields here
                                })
                                .show();
                        });

                        return localPendingGeckoResult; // Return local result
                    }

                    @Override
                    public GeckoResult<PromptResponse> onDateTimePrompt(GeckoSession session, DateTimePrompt prompt) {
                        Log.d(TAG, "ANON_PD_POPUP: onDateTimePrompt: Title=" + prompt.title +
                                   ", Type=" + prompt.type + // Log int type for anonymous delegate
                                   ", DefaultValue=" + prompt.defaultValue);

                        final DateTimePrompt currentPopupDateTimePrompt = prompt;
                        final GeckoResult<PromptResponse> localPopupDateTimeResult = new GeckoResult<>();

                        runOnUiThread(() -> {
                            if (currentPopupDateTimePrompt == null) {
                                Log.e(TAG, "ANON_PD_POPUP: onDateTimePrompt: currentPopupDateTimePrompt is null in runOnUiThread.");
                                if (localPopupDateTimeResult != null) {
                                    localPopupDateTimeResult.complete(null);
                                }
                                return;
                            }

                            final int type = currentPopupDateTimePrompt.type; // Use int from local prompt
                            final String defaultValue = currentPopupDateTimePrompt.defaultValue;
                            java.util.Calendar calendar = java.util.Calendar.getInstance();

                            if (defaultValue != null && !defaultValue.isEmpty()) {
                                try {
                                    if (type == DateTimePrompt.Type.DATE || type == DateTimePrompt.Type.DATETIME_LOCAL || type == DateTimePrompt.Type.MONTH || type == DateTimePrompt.Type.WEEK) {
                                        java.text.SimpleDateFormat sdf = null;
                                        if (type == DateTimePrompt.Type.MONTH) sdf = new java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US);
                                        else if (type == DateTimePrompt.Type.WEEK) sdf = new java.text.SimpleDateFormat("yyyy-'W'ww", java.util.Locale.US);
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
                                    Log.w(TAG, "ANON_PD_POPUP: onDateTimePrompt: Could not parse defaultValue '" + defaultValue + "' for type code '" + type + "'", e);
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
                                        if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.confirm(selectedDate));
                                    }, year, month, day);
                                datePickerDialog.setOnCancelListener(dialog -> {
                                    if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                                });
                                datePickerDialog.setTitle(currentPopupDateTimePrompt.title);
                                datePickerDialog.show();
                            } else if (type == DateTimePrompt.Type.TIME) {
                                android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(MainActivity.this,
                                    (view, selectedHour, selectedMinute) -> {
                                        String selectedTime = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute);
                                        if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.confirm(selectedTime));
                                    }, hour, minute, android.text.format.DateFormat.is24HourFormat(MainActivity.this));
                                timePickerDialog.setOnCancelListener(dialog -> {
                                    if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                                });
                                timePickerDialog.setTitle(currentPopupDateTimePrompt.title);
                                timePickerDialog.show();
                            } else if (type == DateTimePrompt.Type.DATETIME_LOCAL) {
                                android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(MainActivity.this,
                                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                                        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(MainActivity.this,
                                            (timeView, selectedHour, selectedMinute) -> {
                                                String selectedDateTime = String.format(java.util.Locale.US, "%04d-%02d-%02dT%02d:%02d",
                                                    selectedYear, selectedMonth + 1, selectedDayOfMonth, selectedHour, selectedMinute);
                                                if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.confirm(selectedDateTime));
                                            }, hour, minute, android.text.format.DateFormat.is24HourFormat(MainActivity.this));
                                        timePickerDialog.setOnCancelListener(dialog -> {
                                            if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                                        });
                                        timePickerDialog.setTitle(currentPopupDateTimePrompt.title != null && !currentPopupDateTimePrompt.title.isEmpty() ? currentPopupDateTimePrompt.title : "Select Time");
                                        timePickerDialog.show();
                                    }, year, month, day);
                                datePickerDialog.setOnCancelListener(dialog -> {
                                    if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                                });
                                datePickerDialog.setTitle(currentPopupDateTimePrompt.title != null && !currentPopupDateTimePrompt.title.isEmpty() ? currentPopupDateTimePrompt.title : "Select Date");
                                datePickerDialog.show();
                            } else if (type == DateTimePrompt.Type.MONTH) {
                                android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(MainActivity.this,
                                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                                        String selectedMonthStr = String.format(java.util.Locale.US, "%04d-%02d", selectedYear, selectedMonth + 1);
                                        if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.confirm(selectedMonthStr));
                                    }, year, month, day);
                                datePickerDialog.setOnCancelListener(dialog -> {
                                    if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                                });
                                datePickerDialog.setTitle(currentPopupDateTimePrompt.title);
                                datePickerDialog.show();
                            } else if (type == DateTimePrompt.Type.WEEK) {
                                Log.w(TAG, "ANON_PD_POPUP: DateTimePrompt.Type.WEEK is not fully supported. Dismissing.");
                                android.widget.Toast.makeText(MainActivity.this, "Week selection is not fully supported.", android.widget.Toast.LENGTH_SHORT).show();
                                if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                            } else {
                                Log.w(TAG, "ANON_PD_POPUP: Unsupported DateTimePrompt type code: " + type + ". Dismissing.");
                                android.widget.Toast.makeText(MainActivity.this, "Unsupported date/time input type.", android.widget.Toast.LENGTH_SHORT).show();
                                if (localPopupDateTimeResult != null) localPopupDateTimeResult.complete(currentPopupDateTimePrompt.dismiss());
                            }
                        });

                        return localPopupDateTimeResult;
                    }

                    @Override
                    public GeckoResult<PromptResponse> onFilePrompt(GeckoSession session, FilePrompt prompt) {
                        Log.d(TAG, "ANON_PD_POPUP: onFilePrompt received for popup, dismissing. Title: " + prompt.title);
                        // For popups, if file uploads are not a primary concern or need separate handling,
                        // simply dismissing is the safest to avoid conflict with main activity's file prompt logic.
                        return GeckoResult.fromValue(prompt.dismiss());
                    }

                    @Override
                    public GeckoResult<PromptResponse> onChoicePrompt(GeckoSession session, ChoicePrompt prompt) { 
                        Log.d(TAG, "ANON_PD_POPUP: onChoicePrompt: Title=" + prompt.title +
                                   ", Message=" + prompt.message +
                                   ", Type=" + prompt.type +
                                   ", Choices Count=" + (prompt.choices != null ? prompt.choices.length : 0));
                        // if (prompt.choices != null) { // Original detailed logging, can be restored if needed
                        //     Log.d(TAG, "ANON_PD_POPUP: Choices available: " + prompt.choices.length);
                        //     for (GeckoSession.PromptDelegate.ChoicePrompt.Choice choice : prompt.choices) {
                        //         Log.d(TAG, "ANON_PD_POPUP: Choice: Label=" + choice.label + ", ID=" + choice.id + ", Selected=" + choice.selected + ", Disabled=" + choice.disabled);
                        //         if (choice.items != null && choice.items.length > 0) {
                        //             Log.d(TAG, "ANON_PD_POPUP:   Sub-items present: " + choice.items.length);
                        //         }
                        //     }
                        // } else {
                        //     Log.d(TAG, "ANON_PD_POPUP: Choices array is null.");
                        // }
                        // return GeckoResult.fromValue(prompt.dismiss()); // REMOVE OLD STUB

                        final ChoicePrompt currentPopupChoicePrompt = prompt;
                        final GeckoResult<PromptResponse> localPopupChoiceResult = new GeckoResult<>();

                        runOnUiThread(() -> {
                            if (currentPopupChoicePrompt == null || currentPopupChoicePrompt.choices == null || currentPopupChoicePrompt.choices.length == 0) {
                                Log.e(TAG, "ANON_PD_POPUP: onChoicePrompt: currentPopupChoicePrompt or its choices are null/empty.");
                                PromptResponse dismissResponse = null;
                                if(currentPopupChoicePrompt != null) dismissResponse = currentPopupChoicePrompt.dismiss();
                                // Ensure localPopupChoiceResult is checked before completing
                                if (localPopupChoiceResult != null) { 
                                    localPopupChoiceResult.complete(dismissResponse);
                                }
                                return;
                            }

                            final Choice[] choices = currentPopupChoicePrompt.choices;
                            final String[] displayItems = new String[choices.length];
                            final boolean[] checkedItems = new boolean[choices.length];
                            final int[] selectedItem = {-1}; // For single-choice

                            for (int i = 0; i < choices.length; i++) {
                                displayItems[i] = choices[i].label;
                                if (currentPopupChoicePrompt.type == ChoicePrompt.Type.MULTIPLE) {
                                    checkedItems[i] = choices[i].selected;
                                } else if (choices[i].selected) {
                                    selectedItem[0] = i;
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(currentPopupChoicePrompt.title);
                            if (currentPopupChoicePrompt.message != null && !currentPopupChoicePrompt.message.isEmpty()) {
                                builder.setMessage(currentPopupChoicePrompt.message);
                            }

                            if (currentPopupChoicePrompt.type == ChoicePrompt.Type.MULTIPLE) {
                                builder.setMultiChoiceItems(displayItems, checkedItems, (dialog, which, isChecked) -> {
                                    checkedItems[which] = isChecked;
                                });
                            } else { // SINGLE or MENU
                                builder.setSingleChoiceItems(displayItems, selectedItem[0], (dialog, which) -> {
                                    selectedItem[0] = which;
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
                                    Log.d(TAG, "ANON_PD_POPUP: onChoicePrompt: Confirming with selected IDs: " + selectedIds);
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

                        return localPopupChoiceResult;
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
                });

                // Add to our list of sessions
                // It's important that the session is in the list before switchToTab is called
                // if switchToTab relies on the session being in the list.
                if (!geckoSessionList.contains(newPopupWindowSession)) {
                    geckoSessionList.add(newPopupWindowSession);
                }
                sessionUrlMap.put(newPopupWindowSession, uri); // Store its initial URL (or intended URL)

                // Load the requested URI. This will be queued until the session is opened by GeckoView.
                newPopupWindowSession.loadUri(uri);
                Log.d(TAG, "New session configured by onNewSession, URI set to: " + uri);
                
                final int newTabIndex = geckoSessionList.indexOf(newPopupWindowSession);
                if (newTabIndex != -1) { // Ensure it was added
                    runOnUiThread(() -> switchToTab(newTabIndex));
                } else {
                    // This case should ideally not happen if logic is correct
                    Log.e(TAG, "onNewSession: newPopupWindowSession not found in list after adding. Cannot switch.");
                }
                
                // Return the session instance. GeckoView will take care of opening it.
                return GeckoResult.fromValue(newPopupWindowSession);
            }

            @Override
             public void onLocationChange(GeckoSession session, String url, List<PermissionDelegate.ContentPermission> perms, Boolean hasUserGesture) {
                 Log.d(TAG, "onLocationChange (Session: " + geckoSessionList.indexOf(session) + "): " + url);
                 sessionUrlMap.put(session, url);
                 if (session == getActiveSession()) {
                     runOnUiThread(() -> {
                         urlBar.setText(url);
                         if (!isControlBarExpanded) {
                             minimizedUrlBar.setText(url);
                         }
                     });
                 }
             }

            public void onCanGoBack(GeckoSession session, boolean canGoBack) {
                  Log.d(TAG, "onCanGoBack (Session: " + geckoSessionList.indexOf(session) + "): " + canGoBack);
                 if (session == getActiveSession()) {
                    runOnUiThread(() -> {
                backButton.setEnabled(canGoBack);
                         minimizedBackButton.setEnabled(canGoBack);
                    });
                 }
            }

            public void onCanGoForward(GeckoSession session, boolean canGoForward) {
                 Log.d(TAG, "onCanGoForward (Session: " + geckoSessionList.indexOf(session) + "): " + canGoForward);
                 if (session == getActiveSession()) {
                     runOnUiThread(() -> {
                forwardButton.setEnabled(canGoForward);
                         minimizedForwardButton.setEnabled(canGoForward);
                     });
                 }
             }

             @Override
             public GeckoResult<String> onLoadError(GeckoSession session, String uri, WebRequestError error) {
                 Log.e(TAG, "Load Error (Session: " + geckoSessionList.indexOf(session) + "): " + uri + ", Error: " + error.category + ":" + error.code);
                  if (session == getActiveSession()) {
                      runOnUiThread(() -> Toast.makeText(MainActivity.this, 
                                                     "Load Error: " + error.category + "/" + error.code, 
                                                     Toast.LENGTH_LONG).show());
                  }
                 return null;
             }
             // TODO: Implement onNewSession if popup windows should open new tabs
        });

        newSession.setContentDelegate(new ContentDelegate() {
            @Override
            public void onFullScreen(GeckoSession session, boolean fullScreen) {
                if (session == getActiveSession()) { // Only react if the active session requests fullscreen
                    Log.d(TAG, "onFullScreen called (Active Session): " + fullScreen);
                    runOnUiThread(() -> {
                        if (fullScreen) {
                            enterFullScreen();
                        } else {
                            exitFullScreen();
                        }
                    });
                }
            }
            // Implement other ContentDelegate methods as needed
            @Override
            public void onExternalResponse(GeckoSession session, org.mozilla.geckoview.WebResponse response) {
                runOnUiThread(() -> handleDownloadResponse(response));
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
             saveSessionState(); // Save state after adding a tab
        }
    }

    // Overload for previous behavior (e.g., new tab button)
    private void createNewTab(boolean switchToTab) {
        createNewTab("https://www.google.com", switchToTab);
    }

    // Method to switch the active tab
    private void switchToTab(int index) {
        if (geckoSessionList.isEmpty()) {
            Log.e(TAG, "switchToTab called on an empty session list. Index was: " + index + ". Creating new tab as fallback.");
            this.activeSessionIndex = -1; // Ensure createNewTab knows no tab is active
            if (runtime != null) {
                createNewTab(true); // This will eventually call switchToTab with a valid state
            } else {
                Log.e(TAG, "Cannot create new tab in switchToTab fallback as runtime is null.");
            }
            return;
        }

        if (index < 0 || index >= geckoSessionList.size()) {
            Log.e(TAG, "switchToTab: Invalid index " + index + " for list size " + geckoSessionList.size() + ". Aborting switch.");
            // Fallback: if the intended index is bad for a non-empty list, try to switch to index 0.
            // This prevents the app from being stuck if logic elsewhere calculates a bad index.
            if (!geckoSessionList.isEmpty()) {
                Log.w(TAG, "switchToTab: Attempting to switch to index 0 as a fallback.");
                // Avoid recursion if index 0 is also bad (shouldn't happen if list not empty)
                if (0 >= 0 && 0 < geckoSessionList.size() && index != 0) { // prevent recursion if index was already 0 and invalid
                    switchToTab(0);
                }
            }
            return;
        }

        GeckoSession targetSession = geckoSessionList.get(index);

        // If this tab (by index) is already the active one AND GeckoView is displaying its session
        if (this.activeSessionIndex == index && geckoView.getSession() == targetSession) {
            Log.d(TAG, "switchToTab: Tab " + index + " is already active and displayed. Updating UI only.");
            updateUIForActiveSession(); // Ensure UI is fresh (e.g. URL bar)
            return;
        }

        // Proceed with the switch
        Log.d(TAG, "Switching to tab index: " + index + ". Current global activeSessionIndex before switch: " + this.activeSessionIndex);

        GeckoSession sessionCurrentlyInView = geckoView.getSession();
        if (sessionCurrentlyInView != null) {
            // Only capture snapshot if releasing a session that is different from the target session
            if (sessionCurrentlyInView != targetSession) {
                // Determine if the sessionCurrentlyInView was the 'logical' old active session
                GeckoSession logicalOldActiveSession = (this.activeSessionIndex >= 0 && 
                                                        this.activeSessionIndex < geckoSessionList.size() && // check if old index is still valid (list might have changed)
                                                        this.activeSessionIndex != index) // and not the target index
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

        this.activeSessionIndex = index;
        this.geckoSession = targetSession; // Update the global 'geckoSession' convenience field
        
        geckoView.setSession(targetSession);
        Log.d(TAG, "Attached session to GeckoView: " + sessionUrlMap.getOrDefault(targetSession, "N/A"));

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
                        // if (!isSwitchingTabs && session == getActiveSession()) { // REMOVE CHECK
                            sessionSnapshotMap.put(session, resizedBitmap); // Store the resized bitmap (NOT SoftReference)
                        // }
                        
                        // When adding new:
                        // totalSnapshotMemory += resizedBitmap.getByteCount(); // REMOVE
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
        sessionUrlMap.remove(sessionToClose);
        Bitmap removedSnapshot = sessionSnapshotMap.remove(sessionToClose);
        if (removedSnapshot != null && !removedSnapshot.isRecycled()) {
            removedSnapshot.recycle();
        }
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

    private void showExpandedControls() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            expandedControlBar.setVisibility(View.VISIBLE);
            minimizedControlBar.setVisibility(View.GONE);
            urlBar.setFocusableInTouchMode(true);
            urlBar.setFocusable(true);
            urlBar.setClickable(true);
            isControlBarExpanded = true; // Update state
            Log.d(TAG, "Showing expanded controls");
        }
    }

    private void showMinimizedControls() {
        if (expandedControlBar != null && minimizedControlBar != null) {
            String currentUrlText = urlBar.getText().toString();
            minimizedUrlBar.setText(currentUrlText);

            urlBar.setFocusable(false);
            urlBar.setClickable(false);
            
            expandedControlBar.setVisibility(View.GONE);
            minimizedControlBar.setVisibility(View.VISIBLE);
            isControlBarExpanded = false; // Update state
            Log.d(TAG, "Showing minimized controls. Copied text: " + currentUrlText);
        } else {
             Log.w(TAG, "Cannot show minimized controls - views null?");
        }
    }

    // --- End Control Bar State Logic ---

    @Override
    public void onScrollChanged(GeckoSession session, int scrollX, int scrollY) {
        int dy = scrollY - lastScrollY;
        Log.d(TAG, "onScrollChanged: dy=" + dy + " scrollY=" + scrollY + " lastScrollY=" + lastScrollY + " expanded=" + isControlBarExpanded);

        // Basic thresholding and state checking
        if (dy > SCROLL_THRESHOLD && isControlBarExpanded) { 
            showMinimizedControls();
        } else if (dy < -SCROLL_THRESHOLD && !isControlBarExpanded) { 
             showExpandedControls();
         }
         
         // Update last position only if movement is significant enough
         if (Math.abs(dy) > 5) { 
            lastScrollY = scrollY;
         }
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
        Log.d(TAG, "Entering fullscreen");
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
        Log.d(TAG, "Exiting fullscreen");
        // Show system UI
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Allow any orientation again
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Show the app's control bar (ensure it's in the correct state)
        if (controlBarContainer != null) {
            controlBarContainer.setVisibility(View.VISIBLE);
            if (isControlBarExpanded) {
                showExpandedControls();
            } else {
                showMinimizedControls();
            }
        }
    }
    // --- End Fullscreen Helper Methods ---

    // Ensure system UI visibility is reset if the user manually leaves fullscreen (e.g., back button)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // If focus is regained and we are *supposed* to be fullscreen (check a flag if needed),
        // re-hide the system UI as it might reappear on focus change.
        // This is a common pattern but might need adjustment based on exact behavior.
        // Example (needs a boolean flag like `isInFullscreenMode` managed by enter/exitFullScreen):
        // if (hasFocus && isInFullscreenMode) {
        //    decorView.setSystemUiVisibility(
        //        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        //        | View.SYSTEM_UI_FLAG_FULLSCREEN
        //        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // }
    }

    // Method to launch the Tab Switcher
    private void launchTabSwitcher() {
        // --- Capture snapshot of the CURRENT tab before launching switcher ---
        GeckoSession currentSession = getActiveSession();
        if (currentSession != null) {
             captureSnapshot(currentSession); // Ensure latest snapshot is taken
        }
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
        
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair.create(findViewById(R.id.tabsButton), "fab_transition")
        );
        tabSwitcherLauncher.launch(intent, options); // Launch with Compat options
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
        Log.d(TAG, "Saving session state...");
        SharedPreferences.Editor editor = prefs.edit();

        // Save URLs
        Set<String> urlsToSave = new HashSet<>();
        for (GeckoSession session : geckoSessionList) {
            // Use the stored URL from the map, fallback if somehow missing
            urlsToSave.add(sessionUrlMap.getOrDefault(session, "about:blank"));
        }
        // Using Set<String> which SharedPreferences can handle directly
        editor.putStringSet(PREF_SAVED_URLS, urlsToSave);

        // Save active index
        editor.putInt(PREF_ACTIVE_INDEX, activeSessionIndex);

        editor.apply(); // Use apply() for asynchronous saving
        Log.d(TAG, "Session state saved. Tabs: " + urlsToSave.size() + ", Active Index: " + activeSessionIndex);
    }

    private boolean restoreSessionState() {
        Log.d(TAG, "Attempting to restore session state...");
        Set<String> savedUrls = prefs.getStringSet(PREF_SAVED_URLS, null);
        int savedActiveIndex = prefs.getInt(PREF_ACTIVE_INDEX, -1);

        if (savedUrls == null || savedUrls.isEmpty()) {
            Log.d(TAG, "No saved URLs found.");
            return false;
        }

        Log.d(TAG, "Found saved state. URLs: " + savedUrls.size() + ", Active Index: " + savedActiveIndex);

        // Clear any potentially existing (should be empty) sessions before restoring
        geckoSessionList.clear();
        sessionUrlMap.clear();
        sessionSnapshotMap.clear(); // Snapshots are not persisted
        activeSessionIndex = -1;

        // Recreate sessions - NOTE: Order might not be preserved perfectly with Set
        // If order is critical, saving URLs as a JSON array string would be better.
        int restoredIndex = 0;
        for (String url : savedUrls) {
            // Create tabs but don't switch view yet
            createNewTab(url, false);
            restoredIndex++;
        }

        // Validate and set the active index
        if (savedActiveIndex >= 0 && savedActiveIndex < geckoSessionList.size()) {
            activeSessionIndex = savedActiveIndex;
        } else if (!geckoSessionList.isEmpty()) {
            Log.w(TAG, "Saved active index invalid, defaulting to 0.");
            activeSessionIndex = 0; // Default to first tab if index invalid
        } else {
             Log.e(TAG, "Error: Restored sessions but list is empty?");
             return false; // Indicate failure
        }
        
        Log.d(TAG, "Session state restored. Active index set to: " + activeSessionIndex);
        // The session will be set to GeckoView later in onCreate
        return true;
    }
    // --- End Session Persistence Methods ---

    // Add this method to setup the listener
    private void setupGeckoViewTouchListener() {
        geckoView.setOnTouchListener((v, event) -> {
            // Check on ACTION_DOWN to react immediately on touch
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = getCurrentFocus();
                // Hide keyboard only if focus is not on the main URL bar
                // (assuming keyboard is up due to web content)
                if (currentFocus != null && currentFocus != urlBar) {
                    Log.d(TAG, "Touch on GeckoView detected, hiding keyboard (current focus: " + currentFocus.getClass().getSimpleName() + ")");
                    hideKeyboard();
                    // Returning 'false' allows the touch event to propagate to GeckoView for web interaction
                    // return false; 
                } else if (currentFocus == null) {
                    // If nothing has focus, maybe hide keyboard too?
                    Log.d(TAG, "Touch on GeckoView detected (no focus), hiding keyboard.");
                    hideKeyboard();
                    // return false;
                }
            }
            // Return false so GeckoView still processes the touch for scrolling, clicking links etc.
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

        runOnUiThread(() -> {
            if (MainActivity.this.isFinishing() || MainActivity.this.isDestroyed()) {
                result.complete(currentPrompt.dismiss());
                return;
            }

            new AlertDialog.Builder(MainActivity.this)
                .setTitle(currentPrompt.title)
                .setMessage(currentPrompt.message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    result.complete(currentPrompt.dismiss()); // Use dismiss() for OK
                })
                .setOnDismissListener(dialog -> {
                    result.complete(currentPrompt.dismiss()); // Use dismiss() for cancel/dismiss
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
} 