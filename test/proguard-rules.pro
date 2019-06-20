# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontwarn javax.annotation.**
#
## Retrofit
#-keepattributes Signature, InnerClasses, EnclosingMethod
#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-dontwarn kotlin.Unit
#-dontwarn retrofit2.-KotlinExtensions
#
## Retrolambda
#-dontwarn java.lang.invoke.*
#
## okhttp
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#-dontwarn okhttp3.internal.platform.ConscryptPlatform

# 对于R（资源）下的所有类及其方法，都不混淆
#-keep class **.R$* {
#    *;
#}
#
#
#-dontwarn **.R$*
#-dontwarn **.R

# Add *one* of the following rules to your Proguard configuration file.
# Alternatively, you can annotate classes and class members with @android.support.annotation.Keep

# keep the class and specified members from being removed or renamed
-keep class R$styleable { *; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class R$styleable { *; }

# keep the class and specified members from being renamed only
-keepnames class R$styleable { *; }

# keep the specified class members from being renamed only
-keepclassmembernames class R$styleable { *; }


# keep the class and specified members from being removed or renamed
-keep class com.uxin.usedcar.R$layout { int item_detail; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class com.uxin.usedcar.R$layout { int item_detail; }

# keep the class and specified members from being renamed only
-keepnames class com.uxin.usedcar.R$layout { int item_detail; }

# keep the specified class members from being renamed only
-keepclassmembernames class com.uxin.usedcar.R$layout { int item_detail; }
