package tech.sud.mgp.hello.common.utils;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    /**
     * 对view的MarginTop增加设定的值
     *
     * @param view  操作的View
     * @param value 需要增加的值
     */
    public static void addMarginTop(View view, int value) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
            marginLayoutParams.topMargin = marginLayoutParams.topMargin + value;
            view.setLayoutParams(marginLayoutParams);
        }
    }

}
