package com.neew.browser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.hamcrest.Matchers.allOf;

import android.app.Instrumentation;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
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
public class BrowserFlowsTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        Intents.init();
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
        Intents.release();
    }

    @Test
    public void onCreate_onResume_doNotCrash_andControlsVisible() {
        // Launched in RESUMED
        onView(withId(R.id.controlBarContainer)).check(matches(isDisplayed()));

        // Move to STARTED and back to RESUMED to exercise onResume
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.controlBarContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void newTabButton_click_doesNotCrash_andGeckoViewVisible() {
        onView(withId(R.id.newTabButton)).perform(click());
        // At minimum, GeckoView should remain visible
        onView(withId(R.id.geckoView)).check(matches(isDisplayed()));
    }

    @Test
    public void downloadsButton_opensDownloadsActivity() {
        Intents.intending(anyIntent()).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.downloadsButton)).perform(click());
        intended(hasComponent(DownloadsActivity.class.getName()));
    }

    @Test
    public void toggleDesktopMode_viaSettingsPanel_apply_noCrash() {
        // Open settings panel
        onView(withId(R.id.settingsButton)).perform(click());
        // Toggle Desktop Mode
        onView(withId(R.id.panelDesktopModeSwitch)).perform(scrollTo(), click());
        // Apply
        onView(withId(R.id.panelApplyButton)).perform(scrollTo(), click());
        // Controls still visible
        onView(withId(R.id.controlBarContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void toggleImmersiveMode_viaSettingsPanel_apply_noCrash() {
        // Open settings panel
        onView(withId(R.id.settingsButton)).perform(click());
        // Toggle Immersive Mode
        onView(withId(R.id.panelImmersiveSwitch)).perform(scrollTo(), click());
        // Apply
        onView(withId(R.id.panelApplyButton)).perform(scrollTo(), click());
        // GeckoView remains visible
        onView(withId(R.id.geckoView)).check(matches(isDisplayed()));
    }

    @Test
    public void controlBar_hide_onSwipeUp_and_unhide_onSwipeDown() throws Exception {
        // Skip on TV devices where UI and layout behavior differ significantly
        Assume.assumeFalse(com.neew.browser.testutil.DeviceUtil.isTelevision());
        // Use UiAutomator to perform large swipes on the GeckoView area
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        int w = device.getDisplayWidth();
        int h = device.getDisplayHeight();

        // Swipe up from lower-middle to upper-middle to simulate content scroll downwards (hide controls)
        int startX = w / 2;
        int startY = (int) (h * 0.8);
        int endY = (int) (h * 0.2);
        device.swipe(startX, startY, startX, endY, 40);
        device.swipe(startX, startY, startX, endY, 40);

        // Expect expanded control bar becomes hidden (GONE or INVISIBLE)
        onView(withId(R.id.expandedControlBar))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));

        // Swipe down to bring controls back (simulate scroll upwards)
        device.swipe(startX, endY, startX, startY, 40);
        device.swipe(startX, endY, startX, startY, 40);

        // Expect it visible again
        onView(withId(R.id.expandedControlBar))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
    }
}
