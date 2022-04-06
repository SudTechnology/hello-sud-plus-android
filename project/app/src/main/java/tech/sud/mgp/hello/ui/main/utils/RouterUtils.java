package tech.sud.mgp.hello.ui.main.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.ActivityUtils;

import tech.sud.mgp.hello.app.MyActivityManager;
import tech.sud.mgp.hello.ui.main.activity.WebViewActivity;

/**
 * 跳转相关
 */
public class RouterUtils {

    /**
     * 打开一个网页资源地址
     *
     * @param context 上下文
     * @param title   标题
     * @param url     地址
     */
    public static void openUrl(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        WebViewActivity.WebViewParams webViewParams = new WebViewActivity.WebViewParams();
        webViewParams.title = title;
        webViewParams.url = url;
        intent.putExtra("WebViewParams", webViewParams);
        context.startActivity(intent);
    }

    /**
     * 默认打开一个网页资源地址
     *
     * @param context 上下文
     * @param link    地址
     */
    public static boolean openUrlPage(Context context, String link) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            if (context != null) {
                context.startActivity(intent);
                return true;
            } else {
                Context topContext = ActivityUtils.getTopActivity();
                topContext.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
