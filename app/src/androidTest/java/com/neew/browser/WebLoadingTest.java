package com.neew.browser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WebLoadingTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) scenario.close();
    }

    @Test
    public void enterUrl_and_loads_exampleDotCom_progressBarHides() {
        // Ensure main UI shows
        onView(withId(R.id.geckoView)).check(matches(isDisplayed()));

        // Enter URL in the urlBar and press GO
        onView(withId(R.id.urlBar)).perform(click(), replaceText("https://example.com"), pressImeActionButton());

        // Wait for progress bar to become GONE (page loaded) within timeout
        waitForVisibility(withId(R.id.progressBar), Visibility.GONE, 20000);

        // GeckoView should still be visible
        onView(withId(R.id.geckoView)).check(matches(isDisplayed()));
    }

    // Simple polling wait for a view's effective visibility
    private void waitForVisibility(Matcher<View> viewMatcher, Visibility visibility, long timeoutMs) {
        long start = System.currentTimeMillis();
        AssertionError lastError = null;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                onView(viewMatcher).check(matches(withEffectiveVisibility(visibility)));
                return; // success
            } catch (AssertionError e) {
                lastError = e;
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        if (lastError != null) throw lastError;
    }
}
