package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.util.Log;

import tech.sud.gip.core.ISudAPPD;
import tech.sud.gip.core.SudGIP;
import tech.sud.mgp.hello.BuildConfig;

public class EnvUtils {

    public static void initMgpEnv() {
        // region 为demo代码，直接忽略
        if ("dev".equalsIgnoreCase(BuildConfig.mgpEnv)) {
            ISudAPPD.e(4);
            ISudAPPD.d();
        } else if ("fat".equalsIgnoreCase(BuildConfig.mgpEnv)) {
            ISudAPPD.e(3);
            ISudAPPD.d();
            SudGIP.setLogLevel(Log.DEBUG);
        }
        // endregion 为demo代码，直接忽略
    }

}
