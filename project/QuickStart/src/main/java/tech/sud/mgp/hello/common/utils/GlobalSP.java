package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * 全局使用的SharedPreferences
 */
public class GlobalSP {
    
    private final static String SP_NAME = "global.sp";

    public static SPUtils getSP() {
        return SPUtils.getInstance(SP_NAME);
    }

}
