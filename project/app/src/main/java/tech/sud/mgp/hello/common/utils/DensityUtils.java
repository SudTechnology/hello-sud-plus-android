package tech.sud.mgp.hello.common.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.app.HelloSudApplication;

public class DensityUtils {

    /** dp转px */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** dp转px */
    public static int dp2px(float dpValue) {
        return dp2px(HelloSudApplication.instance, dpValue);
    }

    /** sp转px */
    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }

    /** sp转px */
    public static int sp2px(float sp) {
        return sp2px(HelloSudApplication.instance, sp);
    }

//    /**
//     * sp转px
//     *
//     * @param sp
//     * @return
//     */
//    fun sp2px(sp: Float): Int {
//        return sp2px(Utils.getApp(), sp)
//    }
//
//    fun sp2px(context: Context, sp: Float): Int {
//        return TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP,
//                sp, context.resources.displayMetrics
//        ).toInt()
//    }

    /** px转dp */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /** 获取屏幕真实宽度 */
    public static int getScreenWidth() {
        return getScreenSize().x;
    }

    /** 获取屏幕真实高度 */
    public static int getScreenHeight() {
        return getScreenSize().y;
    }

    /** 获取屏幕真实宽度 */
    public static int getScreenWidth(Context context) {
        return getScreenSize(context).x;
    }

    /** 获取屏幕真实高度 */
    public static int getScreenHeight(Context context) {
        return getScreenSize(context).y;
    }

    /** 获取应用展示宽度 */
    public static int getAppScreenWidth() {
        return getAppScreenSize().x;
    }

    /** 获取应用展示高度 */
    public static int getAppScreenHeight() {
        return getAppScreenSize().y;
    }

    /** 获取app展示的宽高 */
    public static Point getAppScreenSize() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }

    /** 获取屏幕真实宽高 */
    public static Point getScreenSize() {
        return getScreenSize(Utils.getApp());
    }

    /** 获取屏幕真实宽高 */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point;
    }

}
