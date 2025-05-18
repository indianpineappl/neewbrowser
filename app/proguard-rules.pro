# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ab_1/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep GeckoView classes
-keep class org.mozilla.geckoview.** { *; }

# Ignore GeckoView's debug config class (which references snakeyaml)
-dontwarn org.mozilla.gecko.util.DebugConfig
-dontwarn org.yaml.snakeyaml.**

# ProGuard rules for GeckoView PromptDelegate inner classes
-keep class org.mozilla.geckoview.GeckoSession$PromptDelegate$* {
    public *;
}

# Add any other project specific rules here... 