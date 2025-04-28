package tech.sud.mgp.hello.ui.scenes.base.utils;

import android.util.Log;

import tech.sud.mgp.core.ISudAPPD;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.nft.core.ISudNFTD;

public class EnvUtils {

    public static void initMgpEnv() {
        // region 为demo代码，直接忽略
        if ("dev".equalsIgnoreCase(BuildConfig.mgpEnv)) {
            ISudAPPD.e(4);
            ISudAPPD.d();
        } else if ("fat".equalsIgnoreCase(BuildConfig.mgpEnv)) {
            ISudAPPD.e(3);
            ISudAPPD.d();
            SudMGP.setLogLevel(Log.DEBUG);
        }
        // endregion 为demo代码，直接忽略
    }

    public static void initNftEnv() {
        // region 为demo代码，直接忽略
        if ("dev".equalsIgnoreCase(BuildConfig.nftEnv)) {
            ISudNFTD.e(4);
            ISudNFTD.d();
        } else if ("fat".equalsIgnoreCase(BuildConfig.nftEnv)) {
            ISudNFTD.e(3);
        }
        // endregion 为demo代码，直接忽略
    }

}
