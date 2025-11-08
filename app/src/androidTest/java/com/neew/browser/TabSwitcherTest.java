package com.neew.browser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TabSwitcherTest {

    private ActivityScenario<TabSwitcherActivity> scenario;

    @Before
    public void setUp() {
        // Skip on TV form factor where UI differs
        Assume.assumeFalse(com.neew.browser.testutil.DeviceUtil.isTelevision());
        android.content.Intent intent = new android.content.Intent(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(), TabSwitcherActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        java.util.ArrayList<String> urls = new java.util.ArrayList<>();
        urls.add("https://example.com");
        urls.add("https://mozilla.org");
        java.util.ArrayList<String> snaps = new java.util.ArrayList<>();
        snaps.add(null);
        snaps.add(null);
        intent.putStringArrayListExtra(TabSwitcherActivity.EXTRA_TAB_URLS, urls);
        intent.putStringArrayListExtra(TabSwitcherActivity.EXTRA_TAB_SNAPSHOTS, snaps);
        intent.putExtra(TabSwitcherActivity.EXTRA_ACTIVE_TAB_INDEX, 0);
        scenario = ActivityScenario.launch(intent);
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED);
        // Brief settle to ensure views inflated and visible
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        // Verify view present in activity before Espresso asserts
        scenario.onActivity(activity -> {
            android.view.View v = activity.findViewById(R.id.tabsRecyclerView);
            org.junit.Assert.assertNotNull(v);
        });
    }

    @After
    public void tearDown() {
        if (scenario != null) scenario.close();
    }

    @Test
    public void addNewTab_click_succeeds_andRecyclerVisible() {
        scenario.onActivity(activity -> {
            android.view.View rv = activity.findViewById(R.id.tabsRecyclerView);
            org.junit.Assert.assertNotNull(rv);
            org.junit.Assert.assertEquals(android.view.View.VISIBLE, rv.getVisibility());
            android.view.View fab = activity.findViewById(R.id.addNewTabFab);
            org.junit.Assert.assertNotNull(fab);
            fab.performClick();
            // After click, activity may finish with result; if still alive, recycler should be visible
            android.view.View rv2 = activity.findViewById(R.id.tabsRecyclerView);
            if (rv2 != null) {
                org.junit.Assert.assertEquals(android.view.View.VISIBLE, rv2.getVisibility());
            }
        });
    }
}
