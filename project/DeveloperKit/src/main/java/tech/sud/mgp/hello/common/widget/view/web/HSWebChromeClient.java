package tech.sud.mgp.hello.common.widget.view.web;

import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;

public class HSWebChromeClient extends WebChromeClient {

    private final ProgressBar progressBar;
    private WebTitleCallBack webTitleCallBack;

    public HSWebChromeClient(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress == 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE); //开始加载网页时显示进度条
            progressBar.setProgress(newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (webTitleCallBack != null) {
            webTitleCallBack.setWebTitle(title);
        }
    }

    public void setWebTitleCallback(WebTitleCallBack callback) {
        webTitleCallBack = callback;
    }

    public interface WebTitleCallBack {
        void setWebTitle(String title);
    }

    /**
     * 打印web端的console日志
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtils.d("webview message=" + consoleMessage.message() + "   ,lineNumber= "
                + consoleMessage.lineNumber() + " sourceId "
                + consoleMessage.sourceId()
        );
        return true;
    }

}
