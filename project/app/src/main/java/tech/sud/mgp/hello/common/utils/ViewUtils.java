package tech.sud.mgp.hello.common.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;

import java.util.Locale;

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

    /** 设置MarginTop */
    public static void setMarginTop(View view, int value) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
            marginLayoutParams.topMargin = value;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    /** 获取MarginTop */
    public static int getMarginTop(View view) {
        if (view == null) return 0;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
            return marginLayoutParams.topMargin;
        }
        return 0;
    }

    /** 设置Margin */
    public static void setMargin(View view, int value) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
            marginLayoutParams.topMargin = value;
            marginLayoutParams.bottomMargin = value;
            marginLayoutParams.leftMargin = value;
            marginLayoutParams.rightMargin = value;
            view.setLayoutParams(marginLayoutParams);
        }
    }

    /** 获取Margin */
    public static int getMargin(View view) {
        if (view == null) return 0;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
            return marginLayoutParams.topMargin;
        }
        return 0;
    }

    /** 设置宽高 */
    public static void setSize(View view, int size) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = size;
            params.height = size;
            view.setLayoutParams(params);
        }
    }

    /** 设置宽度 */
    public static void setWidth(View view, int width) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = width;
            view.setLayoutParams(params);
        }
    }

    /** 设置高度 */
    public static void setHeight(View view, int height) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    /** 设置paddingStart */
    public static void setPaddingStart(View view, int value) {
        if (view == null) return;
        view.setPaddingRelative(value, view.getPaddingTop(), view.getPaddingEnd(), view.getPaddingBottom());
    }

    /** 设置paddingEnd */
    public static void setPaddingEnd(View view, int value) {
        if (view == null) return;
        view.setPaddingRelative(view.getPaddingStart(), view.getPaddingTop(), value, view.getPaddingBottom());
    }

    /** 设置paddingHorizontal */
    public static void setPaddingHorizontal(View view, int value) {
        if (view == null) return;
        view.setPaddingRelative(value, view.getPaddingTop(), value, view.getPaddingBottom());
    }

    public static void setTranslationX(View view, int value) {
        if (view == null) return;
        view.setTranslationX(value);
    }

    /** 打印输入模式 */
    public static void logSoftInputMode(int softInputMode) {
        if (softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED) {
            LogUtils.d("logSoftInputMode:SOFT_INPUT_ADJUST_UNSPECIFIED");
        }
        logSoftInputMode(softInputMode, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE, "SOFT_INPUT_ADJUST_RESIZE");
        logSoftInputMode(softInputMode, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN, "SOFT_INPUT_ADJUST_PAN");
        logSoftInputMode(softInputMode, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING, "SOFT_INPUT_ADJUST_NOTHING");
    }

    public static void logSoftInputMode(int softInputMode, int checkMode, String name) {
        if ((softInputMode & checkMode) == checkMode) {
            LogUtils.d("logSoftInputMode:" + name);
        }
    }

    /**
     * 设置textview富文本，关键字可自定义颜色
     */
    public static void setTextKeywordColor(Context context, TextView tv, String str, @ColorRes int keywordResid, String... keys) {
        if (keys == null || keys.length == 0) {
            tv.setText(str);
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        int startIndex = 0;
        String lowerStr = str.toLowerCase(Locale.getDefault());
        for (String key : keys) {
            String lowerKey = key.toLowerCase(Locale.getDefault());
            int start = lowerStr.indexOf(lowerKey, startIndex);
            if (start >= 0) {
                int end = start + key.length();
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, keywordResid)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        tv.setText(builder);
    }

    /**
     * 设置textview富文本，关键字可自定义颜色
     */
    public static CharSequence getTextKeywordColor(Context context, String str, @ColorRes int keywordResid, String... keys) {
        if (keys == null || keys.length == 0) {
            return str;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        int startIndex = 0;
        String lowerStr = str.toLowerCase(Locale.getDefault());
        for (String key : keys) {
            String lowerKey = key.toLowerCase(Locale.getDefault());
            int start = lowerStr.indexOf(lowerKey, startIndex);
            if (start >= 0) {
                int end = start + key.length();
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, keywordResid)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

}
