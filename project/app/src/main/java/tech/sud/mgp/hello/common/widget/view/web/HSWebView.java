package tech.sud.mgp.hello.common.widget.view.web;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.R;

/**
 * 封装的webView
 */
public class HSWebView extends WebView {

    private HSWebChromeClient hsWebChromeClient;

    public HSWebView(@NonNull Context context) {
        this(context, null);
    }

    public HSWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HSWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal); //自定义WebView顶部加载进度条
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 6);
        progressBar.setLayoutParams(params);
        ClipDrawable progress = new ClipDrawable(ContextCompat.getDrawable(context, R.drawable.web_view_progress_layer_list), Gravity.START, ClipDrawable.HORIZONTAL);
        GradientDrawable background = new GradientDrawable();
        LayerDrawable pd = new LayerDrawable(new Drawable[]{background, progress});
        progressBar.setProgressDrawable(pd);
        addView(progressBar);
        hsWebChromeClient = new HSWebChromeClient(progressBar);
        setWebChromeClient(hsWebChromeClient);
        WebSettings webViewSettings = getSettings();
        // 缩放设置
        webViewSettings.setSupportZoom(false);
        // 自适应屏幕大小
//        webViewSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webViewSettings.setLoadWithOverviewMode(true);
        // 设置是否允许 WebView 使用 File 协议
        webViewSettings.setAllowFileAccess(true);
        //允许加载不安全来源
        webViewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //设置缓存模式
        webViewSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启 DOM 存储功能
        webViewSettings.setDomStorageEnabled(true);
        //开启 数据库 存储功能
        webViewSettings.setDatabaseEnabled(true);
        //缓存策略
        if (BuildConfig.DEBUG) {
            webViewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    }

    /**
     * 设置网页标题回调
     */
    public void setWebTitleCallback(HSWebChromeClient.WebTitleCallBack callback) {
        hsWebChromeClient.setWebTitleCallback(callback);
    }

}
