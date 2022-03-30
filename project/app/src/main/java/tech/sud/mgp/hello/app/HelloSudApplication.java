/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

import android.app.Application;
import android.view.Gravity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;

public class HelloSudApplication extends Application {

    public static HelloSudApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configBugly();
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
        CrashReport.initCrashReport(getApplicationContext(), APPConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy);
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
