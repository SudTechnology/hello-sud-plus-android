package tech.sud.mgp.hello.ui.main.activity;

import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.widget.view.web.HSWebChromeClient;
import tech.sud.mgp.hello.common.widget.view.web.HSWebView;

/**
 * 加载url的页面
 */
public class WebViewActivity extends BaseActivity {

    private WebViewParams webViewParams;
    private TextView tvTitle;
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
        tvTitle = findViewById(R.id.tv_title);
        webView = findViewById(R.id.web_view);
    }

    @Override
    protected void initData() {
        super.initData();
        tvTitle.setText(webViewParams.title);
        webView.loadUrl(webViewParams.url);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.setWebTitleCallback(new HSWebChromeClient.WebTitleCallBack() {
            @Override
            public void setWebTitle(String title) {
                tvTitle.setText(title);
            }
        });
    }

    public static class WebViewParams implements Serializable {
        public String title; // 标题
        public String url; // 地址
    }

}
