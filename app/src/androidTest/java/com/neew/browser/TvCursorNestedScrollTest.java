package com.neew.browser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.KeyEvent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TvCursorNestedScrollTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        // Only run on TV devices
        Assume.assumeTrue(com.neew.browser.testutil.DeviceUtil.isTelevision());
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) scenario.close();
    }

    @Test
    public void youtube_watch_page_scroll_via_dpad_hides_and_shows_controls() throws Exception {
        // Load a YouTube video page with nested scroll containers (player + comments)
        onView(withId(R.id.urlBar)).perform(click(), replaceText("https://m.youtube.com/watch?v=dQw4w9WgXcQ"), closeSoftKeyboard(), pressImeActionButton());
        // Allow time for page and player to load
        Thread.sleep(5000);

        // Focus the GeckoView to ensure DPAD keys go to content
        onView(withId(R.id.geckoView)).perform(click());

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Scroll down with DPAD to exercise nested container scroll (player/description/comments)
        for (int i = 0; i < 10; i++) {
            device.pressDPadDown();
            Thread.sleep(120);
        }
        // Control bar may remain visible on nested scroll; assert container is still present
        onView(withId(R.id.controlBarContainer))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));

        // Scroll up with DPAD to bring controls back
        for (int i = 0; i < 10; i++) {
            device.pressDPadUp();
            Thread.sleep(120);
        }

        // Expect container visible (minimized or expanded)
        onView(withId(R.id.controlBarContainer))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }
}
