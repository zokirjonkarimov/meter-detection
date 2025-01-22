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
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.slf4j.impl.StaticLoggerBinder
#-keep class com.auth0.** { *; }
#-keep class io.jsonwebtoken.** { *; }
#-keep class com.example.jwt.** { *; }
#-keep class io.jsonwebtoken.** { *; }

# Keep the generated serialization metadata for Kotlinx Serialization
-keepnames class kotlinx.serialization.** { *; }
-keepnames class kotlin.Metadata { *; }

# Keep all @Serializable classes and their companion objects
-keep class * extends kotlinx.serialization.KSerializer { *; }
-keepclassmembers class ** {
    kotlinx.serialization.KSerializer SERIALIZER();
}

-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keep @com.fasterxml.jackson.annotation.* class *
-keep class com.fasterxml.jackson.** { *; }

#-keep class uz.veolia.cabinet.data.remote** { *; }
#-keep class uz.veolia.cabinet.data.model** { *; }
# Prevent Jackson from using Java7 features that require java.beans
-dontwarn com.fasterxml.jackson.databind.ext.Java7SupportImpl
-dontwarn java.beans.**
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepnames class kotlinx.serialization.** { *; }
-keepnames class kotlin.Metadata { *; }

# Keep all @Serializable classes and their companion objects
-keep class * extends kotlinx.serialization.KSerializer { *; }
-keepclassmembers class ** {
    kotlinx.serialization.KSerializer SERIALIZER();
}
