/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

import android.app.Activity;
import android.app.Application;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.common.utils.DensityUtils;
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
            configGift();
            configLog();
            configToast();
            registerActivityLifecycleCallbacks(MyActivityManager.getInstance());
            initMinClient();
            configAutoSize();
            initCrashHandler();
            configBugly();
            LogUtils.file("onCreate");
        }
    }

    private void initCrashHandler() {
        CrashHandler instance = CrashHandler.getInstance();
        instance.init(this);
    }

    private void configAutoSize() {
        AutoSize.checkAndInit(this);
        AutoSizeConfig.getInstance().setUseDeviceSize(true);
        AutoSizeConfig.getInstance().setExcludeFontScale(true); //屏蔽系统设置对字体大小的影响
        AutoSizeConfig.getInstance().setOnAdaptListener(new onAdaptListener() {
            @Override
            public void onAdaptBefore(Object target, Activity activity) {
                setAutoSizeScreenSize();
            }

            @Override
            public void onAdaptAfter(Object target, Activity activity) {
                LogUtils.d("dp2px density=" + activity.getResources().getDisplayMetrics().density);
            }
        });
    }

    /**
     * @link ScreenUtils.getScreenSize()
     * 在某些机型上面，竖屏切换到横屏时，适配框架里面的获取屏幕宽高的方法有问题，拿到的宽高值还没有变换过来
     * 所以使用下面的方法重新设置框架内的屏蔽宽高值
     */
    public void setAutoSizeScreenSize() {
        Point appScreenSize = DensityUtils.getAppScreenSize();
        AutoSizeConfig.getInstance().setScreenWidth(appScreenSize.x);
        AutoSizeConfig.getInstance().setScreenHeight(appScreenSize.y);
    }

    // 网易云信RTC初始化
    private void initMinClient() {
        SDKOptions options = new SDKOptions();
        options.reducedIM = true;
        options.disableAwake = true;
        NIMClient.config(getApplicationContext(), null, options);
    }

    // Bugly config
    private void configBugly() {
        if (BuildConfig.DEBUG) {
            return;
        }
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        String versionAndCode = AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode();
        strategy.setAppVersion(versionAndCode);
        strategy.setAppChannel(BuildConfig.CHANNEL);
        strategy.setDeviceID(DeviceUtils.getUniqueDeviceId());
        CrashReport.initCrashReport(getApplicationContext(), APPConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy);
    }

    private void configGift() {
        GiftHelper.getInstance().creatGifts(this);
    }

    private void configLog() {
        LogUtils.Config config = LogUtils.getConfig();
        if (config != null) {
            config.setConsoleSwitch(BuildConfig.DEBUG || BuildConfig.gameIsTestEnv || "sim".equalsIgnoreCase(BuildConfig.baseUrlConfig));
            config.setSaveDays(2);
            config.setLogHeadSwitch(false);
            config.setSingleTagSwitch(true);
            config.setBorderSwitch(false);
            config.setDir(getExternalFilesDir(null) + File.separator + "helloLogs");
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
