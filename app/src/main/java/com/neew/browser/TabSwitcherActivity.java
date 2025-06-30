package com.neew.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewTreeObserver;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.graphics.Rect;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;

public class TabSwitcherActivity extends AppCompatActivity {

    public static final String EXTRA_TAB_URLS = "com.neew.browser.EXTRA_TAB_URLS";
    public static final String EXTRA_ACTIVE_TAB_INDEX = "com.neew.browser.EXTRA_ACTIVE_TAB_INDEX";
    public static final String EXTRA_TAB_SNAPSHOTS = "EXTRA_TAB_SNAPSHOTS"; // Key for snapshot data
    public static final String RESULT_SELECTED_TAB_INDEX = "com.neew.browser.RESULT_SELECTED_TAB_INDEX";
    public static final String RESULT_CLOSED_TAB_INDEX = "com.neew.browser.RESULT_CLOSED_TAB_INDEX";
    public static final String RESULT_CREATE_NEW_TAB = "com.neew.browser.RESULT_CREATE_NEW_TAB";
    public static final String RESULT_CLEAR_ALL_TABS = "com.neew.browser.RESULT_CLEAR_ALL_TABS";
    public static final int CREATE_NEW_TAB_REQUEST_CODE = -2;

    private RecyclerView tabsRecyclerView;
    private TabAdapter tabAdapter;
    private ArrayList<String> tabUrls;
    private ArrayList<String> tabSnapshotStrings; // To store received Base64 strings
    private int activeTabIndex;
    private FloatingActionButton addNewTabFab;
    private ImageButton clearAllTabsButton;
    private TvCursorView tabSwitcherCursorView;
    private Handler cursorHandler = new Handler();
    private int cursorStepSize = 40; // pixels per D-pad press
    private int cursorX = 0, cursorY = 0;
    private Runnable hideCursorRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_switcher);

        tabsRecyclerView = findViewById(R.id.tabsRecyclerView);
        addNewTabFab = findViewById(R.id.addNewTabFab);
        clearAllTabsButton = findViewById(R.id.clearAllTabsButton);
        tabSwitcherCursorView = findViewById(R.id.tabSwitcherCursorView);

        // Get data from MainActivity
        tabUrls = getIntent().getStringArrayListExtra(EXTRA_TAB_URLS);
        if (tabUrls == null) {
            tabUrls = new ArrayList<>(); // Avoid null pointer
        }
        activeTabIndex = getIntent().getIntExtra(EXTRA_ACTIVE_TAB_INDEX, -1);
        tabSnapshotStrings = getIntent().getStringArrayListExtra(EXTRA_TAB_SNAPSHOTS); // Get snapshot strings
        if (tabSnapshotStrings == null) {
            tabSnapshotStrings = new ArrayList<>(); // Initialize if null
        }
        // Ensure snapshot list size matches URL list size for adapter safety
        while (tabSnapshotStrings.size() < tabUrls.size()) {
            tabSnapshotStrings.add(null);
        }

        setupRecyclerView();
        setupFabListener();
        setupClearAllTabsButtonListener();

        if (isTvDevice()) {
            tabSwitcherCursorView.setVisibility(View.VISIBLE);
            tabSwitcherCursorView.bringToFront();
            // Center cursor initially
            tabSwitcherCursorView.post(() -> centerCursor());
        }
    }

    private boolean isTvDevice() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK);
    }

    private void setupRecyclerView() {
        // Using a GridLayoutManager to show tabs in a grid like the image
        // Adjust spanCount based on screen size/orientation if needed
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columns
        tabsRecyclerView.setLayoutManager(layoutManager);

        tabAdapter = new TabAdapter(tabUrls, tabSnapshotStrings, activeTabIndex, 
            // Listener for tab selection
            (position) -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_SELECTED_TAB_INDEX, position);
                setResult(Activity.RESULT_OK, resultIntent);
                finish(); // Close switcher and return result
            },
            // Listener for tab close
            (position) -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(RESULT_CLOSED_TAB_INDEX, position);
                setResult(Activity.RESULT_OK, resultIntent); 
                 finish(); 
            },
            tabsRecyclerView // Pass the RecyclerView instance here
        );
        tabsRecyclerView.setAdapter(tabAdapter);

        if (isTvDevice()) {
        // Set D-pad navigation order
        addNewTabFab.setNextFocusUpId(R.id.tabsRecyclerView);
        clearAllTabsButton.setNextFocusUpId(R.id.tabsRecyclerView);
        addNewTabFab.setNextFocusLeftId(R.id.clearAllTabsButton);
        clearAllTabsButton.setNextFocusRightId(R.id.addNewTabFab);

        // Automatically scroll to the last tab and set initial focus
        if (tabAdapter.getItemCount() > 0) {
            tabsRecyclerView.post(() -> {
                int lastPosition = tabAdapter.getItemCount() - 1;
                tabsRecyclerView.scrollToPosition(lastPosition);
                RecyclerView.ViewHolder lastViewHolder = tabsRecyclerView.findViewHolderForAdapterPosition(lastPosition);
                if (lastViewHolder != null) {
                    lastViewHolder.itemView.requestFocus();
                } else {
                    // Fallback if view holder is not immediately available
                    tabsRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                        public void onGlobalLayout() {
                            RecyclerView.ViewHolder vh = tabsRecyclerView.findViewHolderForAdapterPosition(lastPosition);
                            if (vh != null) {
                                vh.itemView.requestFocus();
                                tabsRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        }
                    });
                }
            });
        } else {
            // If there are no tabs, focus the 'Add New Tab' button
            addNewTabFab.requestFocus();
            }
        }
    }

    private void setupFabListener() {
        addNewTabFab.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_CREATE_NEW_TAB, true);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupClearAllTabsButtonListener() {
        updateClearAllTabsButtonVisibility(); // Set initial visibility

        clearAllTabsButton.setOnClickListener(v -> {
            new AlertDialog.Builder(TabSwitcherActivity.this)
                .setTitle("Clear All Tabs")
                .setMessage("This will delete all open tabs.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(RESULT_CLEAR_ALL_TABS, true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        });
    }

    private void updateClearAllTabsButtonVisibility() {
        boolean isVisible = tabUrls != null && tabUrls.size() >= 2;
        clearAllTabsButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        clearAllTabsButton.setFocusable(isVisible);
    }

    private void centerCursor() {
        // Center the cursor in the RecyclerView area
        if (tabsRecyclerView == null || tabSwitcherCursorView == null) return;
        int[] rvLoc = new int[2];
        tabsRecyclerView.getLocationOnScreen(rvLoc);
        int rvWidth = tabsRecyclerView.getWidth();
        int rvHeight = tabsRecyclerView.getHeight();
        int cursorW = tabSwitcherCursorView.getWidth();
        int cursorH = tabSwitcherCursorView.getHeight();
        cursorX = rvLoc[0] + rvWidth / 2 - cursorW / 2;
        cursorY = rvLoc[1] + rvHeight / 2 - cursorH / 2;
        updateCursorPosition();
    }

    private void updateCursorPosition() {
        if (tabSwitcherCursorView == null) return;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tabSwitcherCursorView.getLayoutParams();
        if (params == null) {
            params = new FrameLayout.LayoutParams(tabSwitcherCursorView.getWidth(), tabSwitcherCursorView.getHeight());
        }
        params.leftMargin = cursorX;
        params.topMargin = cursorY;
        tabSwitcherCursorView.setLayoutParams(params);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!isTvDevice() || tabSwitcherCursorView == null || tabSwitcherCursorView.getVisibility() != View.VISIBLE) {
            return super.dispatchKeyEvent(event);
        }
        if (event.getAction() != KeyEvent.ACTION_DOWN) return super.dispatchKeyEvent(event);
        int keyCode = event.getKeyCode();
        boolean handled = false;
        int oldX = cursorX, oldY = cursorY;
        int rvLeft = 0, rvTop = 0, rvRight = 0, rvBottom = 0;
        if (tabsRecyclerView != null) {
            int[] rvLoc = new int[2];
            tabsRecyclerView.getLocationOnScreen(rvLoc);
            rvLeft = rvLoc[0];
            rvTop = rvLoc[1];
            rvRight = rvLeft + tabsRecyclerView.getWidth();
            rvBottom = rvTop + tabsRecyclerView.getHeight();
        }
        int cursorW = tabSwitcherCursorView.getWidth();
        int cursorH = tabSwitcherCursorView.getHeight();
        int scrollAmount = cursorStepSize * 2; // Scroll more than step size for visibility
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (cursorX - cursorStepSize < rvLeft) {
                    tabsRecyclerView.scrollBy(-scrollAmount, 0);
                    cursorX = rvLeft;
                } else {
                    cursorX = Math.max(0, cursorX - cursorStepSize);
                }
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (cursorX + cursorW + cursorStepSize > rvRight) {
                    tabsRecyclerView.scrollBy(scrollAmount, 0);
                    cursorX = rvRight - cursorW;
                } else {
                    cursorX = Math.min(getWindowManager().getDefaultDisplay().getWidth() - cursorW, cursorX + cursorStepSize);
                }
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (cursorY - cursorStepSize < rvTop) {
                    if (tabsRecyclerView.canScrollVertically(-1)) {
                        tabsRecyclerView.scrollBy(0, -scrollAmount);
                        cursorY = rvTop;
                    } else {
                        cursorY = rvTop;
                    }
                } else {
                    cursorY = Math.max(0, cursorY - cursorStepSize);
                }
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                int fabY = 0, fabH = 0;
                boolean canScrollDown = tabsRecyclerView.canScrollVertically(1);
                if (addNewTabFab != null) {
                    int[] fabLoc = new int[2];
                    addNewTabFab.getLocationOnScreen(fabLoc);
                    fabY = fabLoc[1];
                    fabH = addNewTabFab.getHeight();
                }
                if (cursorY + cursorH + cursorStepSize > rvBottom) {
                    if (canScrollDown) {
                        tabsRecyclerView.scrollBy(0, scrollAmount);
                        cursorY = rvBottom - cursorH;
                    } else {
                        // Only clamp to above FAB if no more scroll is possible and cursor would overlap FAB
                        if (fabY > 0 && cursorY + cursorH + cursorStepSize > fabY) {
                            cursorY = fabY - cursorH - 8; // 8px gap
                        } else {
                            cursorY = Math.min(getWindowManager().getDefaultDisplay().getHeight() - cursorH, cursorY + cursorStepSize);
                        }
                    }
                } else {
                    cursorY = Math.min(getWindowManager().getDefaultDisplay().getHeight() - cursorH, cursorY + cursorStepSize);
                }
                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                handleCursorClick();
                handled = true;
                break;
        }
        if (handled) {
            updateCursorPosition();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleCursorClick() {
        // Find which tab item is under the cursor and simulate a click
        int[] cursorLoc = new int[2];
        tabSwitcherCursorView.getLocationOnScreen(cursorLoc);
        int cursorCenterX = cursorLoc[0] + tabSwitcherCursorView.getWidth() / 2;
        int cursorCenterY = cursorLoc[1] + tabSwitcherCursorView.getHeight() / 2;
        for (int i = 0; i < tabsRecyclerView.getChildCount(); i++) {
            View child = tabsRecyclerView.getChildAt(i);
            int[] childLoc = new int[2];
            child.getLocationOnScreen(childLoc);
            Rect rect = new Rect(childLoc[0], childLoc[1], childLoc[0] + child.getWidth(), childLoc[1] + child.getHeight());
            if (rect.contains(cursorCenterX, cursorCenterY)) {
                child.performClick();
                hideCursor();
                return;
            }
        }
        // Check addNewTabFab
        int[] fabLoc = new int[2];
        addNewTabFab.getLocationOnScreen(fabLoc);
        Rect fabRect = new Rect(fabLoc[0], fabLoc[1], fabLoc[0] + addNewTabFab.getWidth(), fabLoc[1] + addNewTabFab.getHeight());
        if (fabRect.contains(cursorCenterX, cursorCenterY)) {
            addNewTabFab.performClick();
            hideCursor();
            return;
        }
        // Check clearAllTabsButton if visible
        if (clearAllTabsButton.getVisibility() == View.VISIBLE) {
            int[] clearLoc = new int[2];
            clearAllTabsButton.getLocationOnScreen(clearLoc);
            Rect clearRect = new Rect(clearLoc[0], clearLoc[1], clearLoc[0] + clearAllTabsButton.getWidth(), clearLoc[1] + clearAllTabsButton.getHeight());
            if (clearRect.contains(cursorCenterX, cursorCenterY)) {
                clearAllTabsButton.performClick();
                hideCursor();
                return;
            }
        }
    }

    private void hideCursor() {
        if (tabSwitcherCursorView != null) {
            tabSwitcherCursorView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideCursor();
    }
} 