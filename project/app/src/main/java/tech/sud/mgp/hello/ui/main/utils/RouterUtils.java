package tech.sud.mgp.hello.ui.main.utils;

import android.content.Context;
import android.content.Intent;

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

}
