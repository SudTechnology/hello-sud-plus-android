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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# json序列化的混淆
-keep class tech.sud.mgp.hello.ui.scenes.ticket.model.** {*;}
-keep class tech.sud.mgp.hello.common.http.param.BaseBody {*;}
-keep class tech.sud.mgp.hello.common.http.param.BaseResponse {*;}
-keep class tech.sud.mgp.hello.service.game.req.** {*;}
-keep class tech.sud.mgp.hello.service.game.resp.** {*;}
-keep class tech.sud.mgp.hello.service.login.req.** {*;}
-keep class tech.sud.mgp.hello.service.login.resp.** {*;}
-keep class tech.sud.mgp.hello.service.main.req.** {*;}
-keep class tech.sud.mgp.hello.service.main.resp.** {*;}
-keep class tech.sud.mgp.hello.service.main.config.** {*;}
-keep class tech.sud.mgp.hello.service.room.req.** {*;}
-keep class tech.sud.mgp.hello.service.room.resp.** {*;}
-keep class tech.sud.mgp.hello.service.room.model.** {*;}
-keep class tech.sud.mgp.hello.ui.main.home.model.** {*;}
-keep class tech.sud.mgp.hello.ui.scenes.base.model.** {*;}
-keep class tech.sud.mgp.hello.ui.scenes.common.cmd.** {*;}
-keep class tech.sud.mgp.hello.ui.scenes.custom.model.** {*;}
-keep class tech.sud.mgp.hello.ui.scenes.orderentertainment.model.** {*;}
-keep class tech.sud.mgp.hello.ui.main.settings.model.** {*;}
-keep class tech.sud.mgp.hello.ui.main.nft.model.** {*;}
-keep class tech.sud.mgp.hello.common.event.model.** {*;}

# 腾讯bugly的混淆
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# 即构的混淆
-keep class **.zego.**{*;}

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

# 网易云信的混淆
-dontwarn com.netease.**
-keep class com.netease.** {*;}

# 腾讯云
-keep class com.tencent.** { *; }

# EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# If using AsyncExecutord, keep required constructor of default event used.
# Adjust the class name if a custom failure event type is used.
-keepclassmembers class org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Accessed via reflection, avoid renaming or removal
-keep class org.greenrobot.eventbus.android.AndroidComponentsImpl

# 禁止优化掉代码，因为声网播放器registerPlayerObserver在混淆时可能被优化掉了一些东西，导致没有回调回来，可能跟Proguard/R8版本也有关系，未找到声网官方解决方法，先禁用全局优化
-dontoptimize