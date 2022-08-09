package tech.sud.mgp.hello.ui.main.settings.activity;

import android.view.View;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.IntentUtils;
import tech.sud.mgp.hello.ui.main.settings.fragment.SettingButton;
import tech.sud.mgp.hello.ui.main.utils.RouterUtils;

/**
 * 关于页面
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private SettingButton btnGitHub;
    private SettingButton btnOpenSource;
    private SettingButton btnUserAgreement;
    private SettingButton btnPrivacyPolicy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        btnGitHub = findViewById(R.id.button_github);
        btnOpenSource = findViewById(R.id.button_open_source_licenses);
        btnUserAgreement = findViewById(R.id.button_user_agreement);
        btnPrivacyPolicy = findViewById(R.id.button_privacy_policy);
    }

    @Override
    protected void initData() {
        super.initData();
        btnGitHub.setHint("hello-sud");
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnGitHub.setOnClickListener(this);
        btnOpenSource.setOnClickListener(this);
        btnUserAgreement.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnGitHub) { // github
            IntentUtils.openUrl(this, APPConfig.GIT_HUB_URL);
        } else if (v == btnOpenSource) { // 开源协议
            RouterUtils.openUrl(this, getString(R.string.user_agreement_title), APPConfig.APP_LICENSE_URL);
        } else if (v == btnUserAgreement) { // 用户协议
            RouterUtils.openUrl(this, getString(R.string.user_agreement_title), APPConfig.USER_PROTOCAL_URL);
        } else if (v == btnPrivacyPolicy) { // 隐私政策
//            RouterUtils.openUrl(this, getString(R.string.user_privacy_title), APPConfig.USER_PRIVACY_URL);
            RouterUtils.openUrl(this, "", "https://example.walletconnect.org/");
        }
    }

}
