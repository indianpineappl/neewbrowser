package com.neew.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SmokeTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void appContext_isCorrect() {
        Context appContext = ApplicationProvider.getApplicationContext();
        assertNotNull(appContext);
        assertEquals("com.neew.browser", appContext.getPackageName());
    }

    @Test
    public void launchMainActivity_withoutCrashing() {
        activityRule.getScenario().onActivity(activity -> {
            // Basic sanity: activity launched and not null
            assertNotNull(activity);
        });
    }
}
