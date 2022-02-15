package tech.sud.mgp.hello.ui.app;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEngineManager;
import tech.sud.mgp.hello.rtc.zego.ZegoAudioEngine;
import tech.sud.mgp.hello.ui.room.audio.gift.manager.GiftHelper;

public class HelloSudApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        configBugly();
        configAudioEngine();
        configGift();
        configLog();
        configToast();
        registerActivityLifecycleCallbacks(MyActivityManager.getInstance());
    }

    // Bugly config
    private void configBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        String versionAndCode = AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode();
        strategy.setAppVersion(versionAndCode);
        CrashReport.initCrashReport(getApplicationContext(), "f471ed313c", true, strategy);
    }

    private void configAudioEngine() {
        // 即构
        MediaAudioEngineManager.shared().makeEngine(ZegoAudioEngine.class);
        MediaAudioEngineManager.shared().audioEngine.config("581733944", "8d8c5698d49929056462dba41cb48cdd4d05babd8c2c68e450b3883096656b87");

        // 声网
//        MediaAudioEngineManager.shared().makeEngine(AgoraAudioEngine.class);
//        MediaAudioEngineManager.shared().audioEngine.config("fae6bf5147f740fe975dfec61013a308", "");
    }

    private void configGift() {
        GiftHelper.getInstance().creatGifts(this);
    }

    private void configLog() {
        LogUtils.Config config = LogUtils.getConfig();
        if (config != null) {
            config.setConsoleSwitch(BuildConfig.DEBUG);
            config.setSaveDays(2);
            config.setLogHeadSwitch(false);
            config.setSingleTagSwitch(true);
            config.setBorderSwitch(false);
        }
    }

    private void configToast() {
        ToastUtils.getDefaultMaker().setGravity(Gravity.CENTER, 0, 0);
    }

}
