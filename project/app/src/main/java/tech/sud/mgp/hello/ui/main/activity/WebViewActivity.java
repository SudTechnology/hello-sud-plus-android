package tech.sud.mgp.hello.ui.main.activity;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.widget.view.web.HSWebChromeClient;
import tech.sud.mgp.hello.common.widget.view.web.HSWebView;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;

/**
 * 加载url的页面
 */
public class WebViewActivity extends BaseActivity {

    private WebViewParams webViewParams;
    private HSTopBar topBar;
    private HSWebView webView;

    @Override
    protected boolean beforeSetContentView() {
        webViewParams = (WebViewParams) getIntent().getSerializableExtra("WebViewParams");
        if (webViewParams == null) return true;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = findViewById(R.id.top_bar);
        webView = findViewById(R.id.web_view);
    }

    @Override
    protected void initData() {
        super.initData();
        topBar.setTitle(webViewParams.title);
        webView.loadUrl(webViewParams.url);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        webView.setWebTitleCallback(new HSWebChromeClient.WebTitleCallBack() {
            @Override
            public void setWebTitle(String title) {
                topBar.setTitle(title);
            }
        });
    }

    public static class WebViewParams implements Serializable {
        public String title; // 标题
        public String url; // 地址
    }

}
