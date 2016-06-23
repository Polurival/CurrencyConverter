# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.github.polurival.cc.** {
    *;
}

-keep public class com.github.polurival.cc.model.db.DBHelper {
    private static final *** *;
    public static final ** *;
}
-keep public class com.github.polurival.cc.model.updater.CustomRateUpdaterMock {
    public void fillCurrencyMapFromSource(java.lang.Object);
}
-keep public class com.github.polurival.cc.model.updater.RateUpdater {
    public abstract void fillCurrencyMapFromSource(java.lang.Object);
}
-keep public class com.github.polurival.cc.util.Constants {
     public static final ** *;
}

-keep public class com.github.polurival.cc.util.DateUtil {
    public DateUtil();
    public static ** *();
    public static ** *(***);
}

-keep public class com.github.polurival.cc.util.Logger {
    public Logger();
    public static void logD(java.lang.String,java.lang.String);
    public static java.lang.String getTag();
}

-keep public final class com.github.polurival.cc.BuildConfig {
    public static final *** *;
}

-keep public final class com.github.polurival.cc.R
-keep public final class com.github.polurival.cc.R$* {
    *;
}

-keep class fr.castorflex.** {
    *;
}

-keep class org.joda.time** {
    *;
}

-keep class org.apache.commons.io.** {
    *;
}

-keep class uk.co.senab.actionbarpulltorefresh.** {
    *;
}

-keep class net.danlew.android.joda.** {
    *;
}

-keep class android.support.** {
    *;
}
