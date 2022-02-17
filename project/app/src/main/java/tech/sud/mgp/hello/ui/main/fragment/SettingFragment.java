package tech.sud.mgp.hello.ui.main.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.utils.HsIntentUtils;
import tech.sud.mgp.hello.ui.main.activity.ChangeRtcActivity;
import tech.sud.mgp.hello.ui.main.activity.UserAgreementActivity;
import tech.sud.mgp.hello.ui.main.activity.VersionInfoActivity;
import tech.sud.mgp.hello.ui.main.view.AppSizeView;
import tech.sud.mgp.hello.ui.main.view.SettingButton;

/**
 * 首页当中的设置页
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private AppSizeView appSizeView;
    private SettingButton btnVersionInfo;
    private SettingButton btnChangeRtc;
    private SettingButton btnChangeLanguage;
    private SettingButton btnGitHub;
    private SettingButton btnOpenSource;
    private SettingButton btnUserAgreement;
    private SettingButton btnPrivacyPolicy;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        appSizeView = findViewById(R.id.app_size_view);
        btnVersionInfo = findViewById(R.id.button_version_info);
        btnChangeRtc = findViewById(R.id.button_change_rtc);
        btnChangeLanguage = findViewById(R.id.button_change_language);
        btnGitHub = findViewById(R.id.button_github);
        btnOpenSource = findViewById(R.id.button_open_source_licenses);
        btnUserAgreement = findViewById(R.id.button_user_agreement);
        btnPrivacyPolicy = findViewById(R.id.button_privacy_policy);
    }

    @Override
    protected void initData() {
        initAppSize();
    }

    private void initAppSize() {
        List<AppSizeView.AppSizeModel> list = new ArrayList<>();
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#fc955b"), "SudMGP Core", 113240));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#fc5bca"), "SudMGP ASR", 1761234));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#614bff"), "HelloSud", 1777234));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#33000000"), "Zego RTC SDK", 152345));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#1a000000"), "Agora RTC SDK", 1234500));
        appSizeView.setDatas(list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnVersionInfo.setOnClickListener(this);
        btnChangeRtc.setOnClickListener(this);
        btnChangeLanguage.setOnClickListener(this);
        btnGitHub.setOnClickListener(this);
        btnOpenSource.setOnClickListener(this);
        btnUserAgreement.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVersionInfo) { // 版本信息
            startActivity(new Intent(requireContext(), VersionInfoActivity.class));
        } else if (v == btnChangeRtc) { // 切换RTC
            startActivity(new Intent(requireContext(), ChangeRtcActivity.class));
        } else if (v == btnChangeLanguage) { // 切换语言
            ToastUtils.showShort(R.string.coming_soon);
        } else if (v == btnGitHub) { // github
            HsIntentUtils.openUrl(getContext(), "https://github.com/SudTechnology/hello-sud-android");
        } else if (v == btnOpenSource) { // 开源协议
            HsIntentUtils.openUrl(getContext(), "https://github.com/SudTechnology/hello-sud-android/license.txt");
        } else if (v == btnUserAgreement) { // 用户协议
            Intent intent = new Intent(requireContext(), UserAgreementActivity.class);
            intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 0);
            startActivity(intent);
        } else if (v == btnPrivacyPolicy) { // 隐私政策
            Intent intent = new Intent(requireContext(), UserAgreementActivity.class);
            intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 1);
            startActivity(intent);
        }
    }
}