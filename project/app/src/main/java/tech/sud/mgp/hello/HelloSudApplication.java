package tech.sud.mgp.hello;

import android.app.Application;

import com.blankj.utilcode.util.AppUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class HelloSudApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        configBugly();
    }

    // Bugly config
    private void configBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        String versionAndCode = AppUtils.getAppVersionName() + AppUtils.getAppVersionCode();
        strategy.setAppVersion(versionAndCode);
        CrashReport.initCrashReport(getApplicationContext(), "2224b284b5", true, strategy);
    }
}
