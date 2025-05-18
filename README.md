# NeewBrowser Project

This is the README for the NeewBrowser Android application.

## Development Notes

### GeckoView: CTA Failures and `window.arguments` Error

**Symptom:**
- Call-to-Action (CTA) buttons or other interactive elements on some websites (e.g., `https://webflow.com/made-in-webflow/example`) may not function correctly.
- Logcat may show a `TypeError: window.arguments is undefined` error originating from GeckoView's internal script `chrome://geckoview/content/geckoview.js` (around line 52, in `_initData`).

**Cause (Hypothesis):**
- This issue appears to be related to an incomplete or incorrect initialization within GeckoView when it's not running in a more debug-friendly mode. The `window.arguments` object, expected by GeckoView's internal scripts, might not be properly set up. This issue was present before uBlock Origin integration.

**Solution/Workaround:**
The following combination of settings resolved the issue, allowing CTAs to function correctly and eliminating the `window.arguments` error:

1.  **Enable Debuggable Release Builds (Locally):**
    Add the following line to your project's `local.properties` file (create the file if it doesn't exist at the project root):
    ```properties
    android.buildTypes.release.debuggable=true
    ```
    *Note: `local.properties` is typically not checked into version control and affects local builds.*

2.  **Enable Remote Debugging in GeckoRuntimeSettings:**
    In `MainActivity.java`, within the `applyRuntimeSettings()` method (or wherever `GeckoRuntimeSettings` are configured), enable remote debugging:
    ```java
    GeckoRuntimeSettings settings = runtime.getSettings();
    // ... other settings ...
    settings.setRemoteDebuggingEnabled(true); // Enable remote debugging
    // ... other settings ...
    ```

**Outcome:**
- With these settings, GeckoView appears to initialize more completely, resolving the internal error and allowing web content to function as expected.
- It's recommended to keep `settings.setRemoteDebuggingEnabled(true);` for debug builds. The `local.properties` change will make local release builds debuggable. If building release versions for distribution through a CI/CD system, this `local.properties` change won't apply, and the issue might reappear unless the root cause in GeckoView is fixed in a future version.

---
*(You can add more sections to this README as your project develops.)* 