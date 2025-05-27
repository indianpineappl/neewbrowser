package com.neew.browser;

import android.app.Application;
import android.util.Log;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;

public class IndicBrowserApplication extends Application {

    private static final String TAG = "IndicBrowserApp";
    private GeckoRuntime mRuntime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate - Initializing GeckoRuntime");

        // Use absolutely minimal GeckoRuntime creation - no custom settings at all
        try {
            mRuntime = GeckoRuntime.create(this);
            Log.i(TAG, "GeckoRuntime created successfully with default settings.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to create GeckoRuntime in Application class.", e);
            mRuntime = null;
        }
    }

    public GeckoRuntime getGeckoRuntime() {
        if (mRuntime == null) {
            Log.w(TAG, "getGeckoRuntime called but mRuntime is null. Attempting emergency initialization.");
            try {
                mRuntime = GeckoRuntime.create(this);
                Log.i(TAG, "Emergency GeckoRuntime initialized successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to emergency-initialize GeckoRuntime. Browser may not work.", e);
            }
        }
        return mRuntime;
    }
} 