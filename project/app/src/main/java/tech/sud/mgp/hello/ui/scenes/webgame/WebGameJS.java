package tech.sud.mgp.hello.ui.scenes.webgame;

import android.app.Activity;
import android.webkit.JavascriptInterface;

public class WebGameJS {
    public Activity context;
    private WebGameListener mWebGameListener;

    public WebGameJS(Activity context) {
        this.context = context;
    }

    @JavascriptInterface
    public void closeGame() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 关闭游戏
                if (mWebGameListener != null) {
                    mWebGameListener.onCloseGame();
                }
            }
        });
    }

    @JavascriptInterface
    public void pay() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 充值
                if (mWebGameListener != null) {
                    mWebGameListener.onPay();
                }
            }
        });
    }

    public void setWebGameListener(WebGameListener webGameListener) {
        mWebGameListener = webGameListener;
    }

    public interface WebGameListener {
        void onCloseGame();

        void onPay();
    }

}
