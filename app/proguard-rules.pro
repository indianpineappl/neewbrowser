# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep GeckoView classes
-keep class org.mozilla.geckoview.** { *; }

# Ignore GeckoView's debug config class (which references snakeyaml)
-dontwarn org.mozilla.gecko.util.DebugConfig
-dontwarn org.yaml.snakeyaml.** 