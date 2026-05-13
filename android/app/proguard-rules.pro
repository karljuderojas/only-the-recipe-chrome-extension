# Strip Log.v/d/i/w/e calls from release builds — also makes R8 prune
# any branches whose only effect was logging.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# The extractor JS calls Android.onRecipeExtracted(String) via JavascriptInterface.
# Without this keep, R8 renames the method and the WebView bridge breaks silently.
-keepclassmembers class com.onlytherecipe.RecipeActivity$Bridge {
    @android.webkit.JavascriptInterface <methods>;
}

# Material / AppCompat / RecyclerView ship their own consumer rules — no extra
# keeps needed. Add new sections here as deps grow (Billing, AdMob, etc.).
