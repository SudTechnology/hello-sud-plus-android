package tech.sud.mgp.hello.common.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.Utils;

public class DensityUtils {

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth() {
        return getDisplayMetrics(Utils.getApp()).widthPixels;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics(Utils.getApp()).heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

}
