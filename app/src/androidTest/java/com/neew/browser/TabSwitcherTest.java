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
        scenario = ActivityScenario.launch(TabSwitcherActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) scenario.close();
    }

    @Test
    public void addNewTab_click_succeeds_andRecyclerVisible() {
        // Assert Recycler is visible
        onView(withId(R.id.tabsRecyclerView)).check(matches(isDisplayed()));
        // Click FAB to add a tab; verify no crash and UI remains visible
        onView(withId(R.id.addNewTabFab)).perform(click());
        onView(withId(R.id.tabsRecyclerView)).check(matches(isDisplayed()));
    }
}
