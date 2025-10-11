package com.neew.browser.testutil;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;

import androidx.test.platform.app.InstrumentationRegistry;

public final class DeviceUtil {
    private DeviceUtil() {}

    public static boolean isTelevision() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UiModeManager uiModeManager = (UiModeManager) ctx.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            int modeType = uiModeManager.getCurrentModeType();
            if (modeType == Configuration.UI_MODE_TYPE_TELEVISION) return true;
        }
        // Fallback check
        return (ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK)
                == Configuration.UI_MODE_TYPE_TELEVISION;
    }
}
