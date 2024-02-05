package tech.sud.mgp.hello.ui.scenes.webgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.WebGameTokenResp;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;

public class WebGameFragment extends BaseFragment {

    private WebView webView;
    private View rootView;
    private GameModel mGameModel;

    public static WebGameFragment newInstance(GameModel gameModel) {
        Bundle args = new Bundle();
        args.putSerializable("GameModel", gameModel);
        WebGameFragment fragment = new WebGameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mGameModel = (GameModel) arguments.getSerializable("GameModel");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        rootView = findViewById(R.id.root_view);
        webView = findViewById(R.id.web_view);

        Activity context = requireActivity();

        WebSettings sets = this.webView.getSettings();
        sets.setJavaScriptEnabled(true);
        sets.setCacheMode(WebSettings.LOAD_DEFAULT);

        sets.setDomStorageEnabled(true);
        sets.setDatabaseEnabled(true);
        sets.setDatabasePath(context.getFilesDir().getAbsolutePath());
        sets.setAppCacheEnabled(true);
        sets.setAppCachePath(context.getFilesDir().getAbsolutePath());
        sets.setMediaPlaybackRequiresUserGesture(true);

        // LingxianAndroid 这是游戏调用的类
        WebGameJS webGameJS = new WebGameJS(context);
        webGameJS.setWebGameListener(mWebGameListener);
        webView.addJavascriptInterface(webGameJS, "LingxianAndroid");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.d("shouldOverrideUrlLoading:" + url);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtils.d("onProgressChanged:" + newProgress);
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
        });

        // 设置背景色
        webView.setBackgroundColor(0);
        // 设置透明度 范围：0-255
        webView.getBackground().setAlpha(0);
    }

    @Override
    protected void initData() {
        super.initData();
        RoomRepository.webGameToken(this, getRoomId(), getGameId(), new RxCallback<WebGameTokenResp>() {
            @Override
            public void onSuccess(WebGameTokenResp webGameTokenResp) {
                super.onSuccess(webGameTokenResp);
                if (webGameTokenResp.scale > 0) {
                    ViewUtils.setHeight(rootView, (int) (DensityUtils.getAppScreenWidth() * webGameTokenResp.scale));
                }
                webView.loadUrl(webGameTokenResp.gameUrl);
            }
        });
    }

    private long getGameId() {
        return mGameModel.gameId;
    }

    private String getRoomId() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseRoomActivity) {
            BaseRoomActivity<?> baseRoomActivity = (BaseRoomActivity<?>) activity;
            return baseRoomActivity.getGameRoomId();
        }
        return null;
    }

    private WebGameJS.WebGameListener mWebGameListener = new WebGameJS.WebGameListener() {
        @Override
        public void onCloseGame() {
            LogUtils.d("onCloseGame");
            FragmentActivity activity = getActivity();
            if (activity instanceof BaseRoomActivity) {
                BaseRoomActivity<?> baseRoomActivity = (BaseRoomActivity<?>) activity;
                baseRoomActivity.webGameOnCloseGame();
            }
        }

        @Override
        public void onPay() {
            LogUtils.d("onPay");
            FragmentActivity activity = getActivity();
            if (activity instanceof BaseRoomActivity) {
                BaseRoomActivity<?> baseRoomActivity = (BaseRoomActivity<?>) activity;
                baseRoomActivity.webGameOnPay(new BaseRoomActivity.WebGameOnPayListener() {
                    @Override
                    public void onSuccess() {
                        String jsCode = "updateCoin()";
                        webView.evaluateJavascript(jsCode, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                LogUtils.d("onReceiveValue:" + value);
                                // 处理JavaScript方法的返回值（如果有）
                            }
                        });
                    }
                });
            }
        }
    };

}
