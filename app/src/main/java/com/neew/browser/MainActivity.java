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

public class MainActivity extends AppCompatActivity implements ScrollDelegate {
    private static final String TAG = "MainActivity";
    private GeckoView geckoView;
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

    // Control Bar State Views
    private FrameLayout controlBarContainer;
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
    // --- End Ad Blocker Key ---
    // --- Advanced Ad Blocker Key --- // REMOVE
    // private static final String PREF_ADVANCED_AD_BLOCKER_ENABLED = "advanced_ad_blocker_enabled"; // VPN blocker
    // --- End Advanced Ad Blocker Key --- // REMOVE

    private View decorView; // To control system UI visibility

    // --- Tab Management --- 
    private List<GeckoSession> geckoSessionList = new ArrayList<>();
    private int activeSessionIndex = -1;
    private Map<GeckoSession, String> sessionUrlMap = new HashMap<>();
    private static final int MAX_SNAPSHOTS = 10; // Limit snapshots stored
    private static final int SNAPSHOT_WIDTH = 300; // Target width for resized snapshots
    
    // Use LinkedHashMap to maintain insertion order for LRU eviction
    private Map<GeckoSession, Bitmap> sessionSnapshotMap = new LinkedHashMap<GeckoSession, Bitmap>(MAX_SNAPSHOTS + 1, .75F, true) {
        // Override removeEldestEntry to automatically remove the oldest snapshot when the map exceeds the limit
        @Override
        protected boolean removeEldestEntry(Map.Entry<GeckoSession, Bitmap> eldest) {
             boolean shouldRemove = size() > MAX_SNAPSHOTS;
             if (shouldRemove) {
                  Log.d(TAG, "Removing eldest snapshot due to size limit.");
                  // Optionally recycle the bitmap here if not used elsewhere
                  // eldest.getValue().get().recycle(); // REMOVE RECYCLE FROM HERE
                  // eldest.getValue().recycle(); // Optionally add back original recycle idea if needed
             }
             return shouldRemove;
        }
    };
    private ActivityResultLauncher<Intent> tabSwitcherLauncher;
    // --- End Tab Management ---

    private LinearLayout settingsPanelLayout;
    private SwitchCompat panelCookieSwitch;
    private SwitchCompat panelAdBlockerSwitch;
    private Button panelApplyButton;
    private Button panelCancelButton;

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
        panelApplyButton = findViewById(R.id.panelApplyButton);
        panelCancelButton = findViewById(R.id.panelCancelButton);

        // Initialize Gecko Runtime (only once)
        if (runtime == null) {
            Log.d(TAG, "Creating GeckoRuntime (default settings)");

            // Create runtime with default settings
            runtime = GeckoRuntime.create(this);
            Log.i(TAG, "GeckoRuntime created with default settings.");

            applyRuntimeSettings(); // Apply dynamic settings AFTER creation using direct setters

        } else {
            Log.d(TAG, "Reusing existing GeckoRuntime");
            // If reusing, maybe re-apply dynamic settings?
            // applyRuntimeSettings(); // Consider if needed on reuse
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
                             // The flag indicates we should create and automatically switch
                             createNewTab(true); 
                        }
                    }
                }
            });

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
            if (activeSession != null) activeSession.goBack();
        });
        forwardButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) activeSession.goForward();
        });
        refreshButton.setOnClickListener(v -> {
            GeckoSession activeSession = getActiveSession();
            if (activeSession != null) activeSession.reload();
        });
        settingsButton.setOnClickListener(v -> toggleSettingsPanel());
        downloadsButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Downloads clicked (not implemented)", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Downloads button clicked.");
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
            return;
        }

        Log.d(TAG, "Creating new GeckoSession");
        GeckoSession newSession = new GeckoSession();

        // --- SET DELEGATES for the new session --- 
        // You might want to reuse delegate instances if they don't hold session-specific state
        // or create new ones as needed.
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
        });

        newSession.setScrollDelegate(this); // Use the Activity's scroll delegate

        Log.d(TAG, "Opening new GeckoSession");
        newSession.open(runtime);
        geckoSessionList.add(newSession);
        String urlToLoad = (initialUrl != null && !initialUrl.isEmpty()) ? initialUrl : "about:blank";
        sessionUrlMap.put(newSession, urlToLoad); // Use provided or default URL

        if (switchToTab) {
            // Switch index first, then load URL
            activeSessionIndex = geckoSessionList.size() - 1;
            geckoView.setSession(newSession);
            newSession.loadUri(urlToLoad);
            updateUIForActiveSession();
            Log.d(TAG, "Switched to new tab index: " + activeSessionIndex + " loading: " + urlToLoad);
        } else {
             // Just load URL without switching view or index
             newSession.loadUri(urlToLoad);
             Log.d(TAG, "Created background tab index: " + (geckoSessionList.size() - 1) + " loading: " + urlToLoad);
        }
    }

    // Overload for previous behavior (e.g., new tab button)
    private void createNewTab(boolean switchToTab) {
        createNewTab("https://www.google.com", switchToTab);
    }

    // Method to switch the active tab
    private void switchToTab(int index) {
        if (index < 0 || index >= geckoSessionList.size()) {
            Log.e(TAG, "Invalid index for switchToTab: " + index);
            return;
        }
        // REMOVED: Early return if index == activeSessionIndex
        // if (index == activeSessionIndex) {
        //     Log.d(TAG, "Already on tab index: " + index + " (but proceeding to set session and UI)"); 
        //     // return; // No longer returning early 
        // }

        Log.d(TAG, "Attempting to switch to tab index: " + index + ". Current active index before switch logic: " + activeSessionIndex);

        GeckoSession sessionToSwitchTo = geckoSessionList.get(index); 
        if (sessionToSwitchTo == null) {
            Log.e(TAG, "switchToTab: Session at target index " + index + " is null!");
            return;
        }
        String targetUrl = sessionUrlMap.getOrDefault(sessionToSwitchTo, "URL_NOT_FOUND_IN_MAP");
        Log.d(TAG, "switchToTab: Target session URL: " + targetUrl + ", IsOpen: " + sessionToSwitchTo.isOpen());

        // --- Capture snapshot of the outgoing tab BEFORE switching --- 
        GeckoSession outgoingSession = getActiveSession(); // Get current active session BEFORE changing activeSessionIndex
        // Only capture if the outgoing session is valid and different from the one we are switching to
        if (outgoingSession != null && outgoingSession != sessionToSwitchTo) { 
            captureSnapshot(outgoingSession); 
        }
        // --- End Snapshot Capture ---

        Log.d(TAG, "Switching to tab index: " + index);
        activeSessionIndex = index;
        // GeckoSession newActiveSession = getActiveSession(); // This is now sessionToSwitchTo

        // if (newActiveSession != null) { // Use sessionToSwitchTo directly
        Log.d(TAG, "switchToTab: Setting GeckoView session to index: " + activeSessionIndex);
        geckoView.setSession(sessionToSwitchTo); // Use the session we fetched and validated
        updateUIForActiveSession();
            
            // --- Diagnostic: Try a reload ---
            // Log.d(TAG, "switchToTab: Forcing reload on session index: " + activeSessionIndex);
            // sessionToSwitchTo.reload(); // Uncomment this to test if reloading helps
            // --- End Diagnostic ---

        // } else {
        //     Log.e(TAG, "switchToTab: Failed to get session at index: " + index + " after setting activeSessionIndex.");
        // }
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
        GeckoSession activeSession = getActiveSession();
        if (activeSession != null) {
             Log.d(TAG, "onBackPressed: Calling goBack() on active GeckoSession.");
             activeSession.goBack();
        } else {
             super.onBackPressed();
        }
    }

    // Method to close a tab
    private void closeTab(int index) {
        if (index < 0 || index >= geckoSessionList.size()) {
            Log.e(TAG, "Invalid index for closeTab: " + index);
            return;
        }

        Log.d(TAG, "Closing tab index: " + index);
        GeckoSession sessionToClose;
        // synchronized (geckoSessionList) { // REMOVE SYNC
            sessionToClose = geckoSessionList.remove(index);
        // }
        sessionUrlMap.remove(sessionToClose);
        Bitmap removedSnapshot = sessionSnapshotMap.remove(sessionToClose); // Remove snapshot from map
        if (removedSnapshot != null && !removedSnapshot.isRecycled()) { // Recycle removed snapshot
             removedSnapshot.recycle();
        }
        sessionToClose.close();

        // Handle switching to a different tab if the closed one was active
        if (activeSessionIndex == index) {
            // If list is now empty, create a new tab
            if (geckoSessionList.isEmpty()) {
                activeSessionIndex = -1; // Reset index
                createNewTab(true);
            } else {
                // Switch to the previous tab, or the first tab if the closed one was the first
                activeSessionIndex = Math.max(0, index - 1);
                switchToTab(activeSessionIndex);
            }
        } else if (activeSessionIndex > index) {
            // If the closed tab was before the active one, adjust the active index
            activeSessionIndex--;
            // IMPORTANT: We need to ensure GeckoView is actually displaying this session
            GeckoSession newCurrentSession = getActiveSession();
            if (newCurrentSession != null) {
                Log.d(TAG, "closeTab: Adjusting active session. New index: " + activeSessionIndex + ". Setting session on GeckoView.");
                geckoView.setSession(newCurrentSession); // Explicitly set the session
            } else {
                Log.w(TAG, "closeTab: Adjusted active session is null for index: " + activeSessionIndex);
            }
            updateUIForActiveSession(); 
        }

        // TODO: Update the Tab Switcher view if it's open
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        // Settings from config file (if loaded) are set at creation. Apply dynamic ones here.
        Log.d(TAG, "Applying dynamic runtime settings...");

        GeckoRuntimeSettings settings = runtime.getSettings(); // Get current settings object

        // --- Apply JavaScript Setting ---
        settings.setJavaScriptEnabled(true);

        ContentBlocking.Settings cbSettings = settings.getContentBlocking();

        // --- Apply Cookie Settings ---
        boolean cookiesEnabled = prefs.getBoolean(PREF_COOKIES_ENABLED, false); // Default false
        Log.d(TAG, "Applying dynamic Cookies enabled: " + cookiesEnabled);
        cbSettings.setCookieBehavior(
            cookiesEnabled ? ContentBlocking.CookieBehavior.ACCEPT_ALL
                           : ContentBlocking.CookieBehavior.ACCEPT_NONE); // Use ACCEPT_NONE which worked before

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

        // Simple slide-up animation
        settingsPanelLayout.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                settingsPanelLayout.getHeight(), // fromYDelta (start below)
                0);                        // toYDelta (end at original position)
        animate.setDuration(300);
        animate.setFillAfter(true);
        settingsPanelLayout.startAnimation(animate);
        Log.d(TAG, "Showing settings panel");
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
        Log.d(TAG, "Applying Settings from Panel: Cookies = " + cookiesChecked + ", Blocker = " + adBlockerChecked);

        // Save the new preferences
        prefs.edit()
             .putBoolean(PREF_COOKIES_ENABLED, cookiesChecked)
             .putBoolean(PREF_AD_BLOCKER_ENABLED, adBlockerChecked)
             .apply();
             
        // Apply GeckoView settings dynamically
        applyRuntimeSettings(); 

        Toast.makeText(MainActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show(); 
        hideSettingsPanel(true); // Hide panel after applying
    }
    // --- End Settings Panel Logic ---

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
} 