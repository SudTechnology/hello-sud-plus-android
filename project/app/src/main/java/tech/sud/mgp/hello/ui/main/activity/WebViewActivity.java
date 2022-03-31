package tech.sud.mgp.hello.ui.main.activity;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.trello.rxlifecycle4.android.ActivityEvent;

import java.io.Serializable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            for (int i = 0; i < 100; i++) {
                emitter.onNext(i + "");
                SystemClock.sleep(1000);
            }
        }).compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String o) {
                        LogUtils.d("rxjava:" + o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
