package tech.sud.mgp.hello.ui.main.settings.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;

/**
 * 用户协议页面
 */
public class UserAgreementActivity extends BaseActivity {

    public final static String AGREEMENTTYPE = "agreementtype";

    private TextView titleTv, contentTv;
    private ImageView backIv;
    private int agreementType = 0;
    private WebView contentWeb;

    @Override
    protected void initWidget() {
        super.initWidget();
        titleTv = findViewById(R.id.title_tv);
        contentTv = findViewById(R.id.content_tv);
        backIv = findViewById(R.id.back_iv);
        contentWeb = findViewById(R.id.content_web);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        agreementType = getIntent().getIntExtra(AGREEMENTTYPE, 0);
        if (agreementType == 1) {
            titleTv.setText(getString(R.string.user_privacy_title));
            contentWeb.loadUrl("file:///android_asset/user_privacy.html");
        } else {
            titleTv.setText(getString(R.string.user_agreement_title));
            contentWeb.loadUrl("file:///android_asset/user_protocol.html");
        }
        contentWeb.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_agreement;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contentWeb.destroy();
    }
}
