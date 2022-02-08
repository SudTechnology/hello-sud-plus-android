package tech.sud.mgp.hello;

import android.app.Application;
import android.net.http.HttpResponseCache;

import com.blankj.utilcode.util.AppUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import tech.sud.mgp.audio.gift.manager.GiftHelper;
import tech.sud.mgp.audio.middle.MediaAudioEngineManager;
import tech.sud.mgp.audio.middle.impl.ZegoAudioEngine;

public class HelloSudApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        configBugly();
        configAudioEngine();
        configGift();
    }

    // Bugly config
    private void configBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        String versionAndCode = AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode();
        strategy.setAppVersion(versionAndCode);
        CrashReport.initCrashReport(getApplicationContext(), "f471ed313c", true, strategy);
    }

    private void configAudioEngine() {
        // 使用zego语音引擎
        MediaAudioEngineManager.shared().makeEngine(ZegoAudioEngine.class);
        // 初始化引擎SDK
        MediaAudioEngineManager.shared().audioEngine.config("581733944", "8d8c5698d49929056462dba41cb48cdd4d05babd8c2c68e450b3883096656b87");
    }

    private void configGift() {
        GiftHelper.getInstance().creatGifts();
    }

}
