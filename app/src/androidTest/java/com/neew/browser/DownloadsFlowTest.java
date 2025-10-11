package com.neew.browser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;

import android.app.Instrumentation;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DownloadsFlowTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void openDownloads_showsToolbarAndList_orEmptyState() {
        // Launch MainActivity and click Downloads
        androidx.test.core.app.ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.downloadsButton)).perform(click());
        intended(hasComponent(DownloadsActivity.class.getName()));

        // Verify key UI on downloads screen
        onView(withId(R.id.toolbar_downloads)).check(matches(isDisplayed()));
        // Either list or empty state exists; check at least one is displayed
        try {
            onView(withId(R.id.recyclerViewDownloads)).check(matches(isDisplayed()));
        } catch (AssertionError e) {
            // Fallback to empty state text when there are no downloads
            onView(withId(R.id.textViewNoDownloads)).check(matches(isDisplayed()));
        }
    }
}
