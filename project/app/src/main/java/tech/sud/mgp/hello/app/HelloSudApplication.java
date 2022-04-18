/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

import android.app.Application;
import android.text.TextUtils;
import android.view.Gravity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.GiftHelper;

public class HelloSudApplication extends Application {

    public static HelloSudApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(android.os.Process.myPid());
        LogUtils.d("processName:" + processName);
        if (getPackageName().equals(processName)) {//只有是当前进程下才需要初始化如下参数
            instance = this;
            configBugly();
            configGift();
            configLog();
            configToast();
            registerActivityLifecycleCallbacks(MyActivityManager.getInstance());
            initMinClient();
        }
    }

    private void initMinClient() {
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

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
