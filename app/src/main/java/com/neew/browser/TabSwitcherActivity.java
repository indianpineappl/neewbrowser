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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_switcher);

        tabsRecyclerView = findViewById(R.id.tabsRecyclerView);
        addNewTabFab = findViewById(R.id.addNewTabFab);
        clearAllTabsButton = findViewById(R.id.clearAllTabsButton);

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
                // Optionally update adapter here or let MainActivity handle it on result
                 tabUrls.remove(position);
                 // Adjust active index if necessary before returning
                 if (position < activeTabIndex) {
                      activeTabIndex--;
                 } else if (position == activeTabIndex) {
                     activeTabIndex = -1; // Indicate active tab was closed
                     // MainActivity will need to handle switching to a new active tab
                 }
                 tabAdapter.notifyItemRemoved(position);
                 tabAdapter.notifyItemRangeChanged(position, tabUrls.size()); 
                 // Don't finish immediately, let MainActivity handle the close and potential switch
                 // We send the result back so MainActivity knows which one *to* close.
                 // Re-setting result allows sending multiple close events if needed, though
                 // typically we might finish after the first close action is sent.
                 // For now, let MainActivity handle closing the session based on this index.
                 setResult(Activity.RESULT_OK, resultIntent); 
                 // Consider if we should finish() here or allow multiple closes?
                 // Finishing might be simpler for now.
                 finish(); 
                 // Adjust snapshot list when removing item locally (optional, depends on exact flow)
                 if (position < tabSnapshotStrings.size()) {
                     tabSnapshotStrings.remove(position);
                 }
                 // After removing a tab, re-check visibility of clear all button
                 updateClearAllTabsButtonVisibility(); 
            }
        );
        tabsRecyclerView.setAdapter(tabAdapter);
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
        if (tabUrls != null && tabUrls.size() >= 2) {
            clearAllTabsButton.setVisibility(View.VISIBLE);
        } else {
            clearAllTabsButton.setVisibility(View.GONE);
        }
    }
} 