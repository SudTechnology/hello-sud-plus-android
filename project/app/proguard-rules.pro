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

# json序列化的混淆
-keep class tech.sud.mgp.hello.ui.scenes.ticket.model.** {*;}

# 腾讯bugly的混淆
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# 声网的混淆
-keep class io.agora.**{*;}

# 融云的混淆
-keepattributes Exceptions,InnerClasses

-keepattributes Signature
#RongRTCLib
-keep public class cn.rongcloud.** {*;}

#RongIMLib
-keep class io.rong.** {*;}
-keep class cn.rongcloud.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

-ignorewarnings

# voiceroom 混淆配置
-keep class cn.rongcloud.voiceroom.api.** {*;}
-keep class cn.rongcloud.voiceroom.model.** {*;}
-keep class cn.rongcloud.voiceroom.utils.** {*;}
-keep class cn.rongcloud.messager.** {*;}
