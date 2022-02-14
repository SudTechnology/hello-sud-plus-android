package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * 全局使用的SharedPreferences
 */
public class GlobalSP {

    public static SPUtils getSP() {
        return SPUtils.getInstance("global.sp");
    }

}
