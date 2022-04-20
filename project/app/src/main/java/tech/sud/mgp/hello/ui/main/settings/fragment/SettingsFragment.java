package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.utils.IntentUtils;
import tech.sud.mgp.hello.ui.main.settings.activity.LanguageActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.MoreSettingsActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.VersionInfoActivity;
import tech.sud.mgp.hello.ui.main.utils.RouterUtils;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.FileUtils;

/**
 * 首页当中的设置页
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private AppSizeView appSizeView;
    private TextView tvTotalSize;
    private View viewLineMoreSettings;
    private View containerTopOccupySize;
    private SettingButton btnVersionInfo;
    private SettingButton btnMoreSettings;
    private SettingButton btnChangeLanguage;
    private SettingButton btnGitHub;
    private SettingButton btnOpenSource;
    private SettingButton btnUserAgreement;
    private SettingButton btnPrivacyPolicy;

    private int clickCount = 0;
    private long clickTimestamp = 0;
    private boolean isShowMoreSettings = false;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        appSizeView = findViewById(R.id.app_size_view);
        btnVersionInfo = findViewById(R.id.button_version_info);
        btnMoreSettings = findViewById(R.id.button_more_settings);
        btnChangeLanguage = findViewById(R.id.button_change_language);
        btnGitHub = findViewById(R.id.button_github);
        btnOpenSource = findViewById(R.id.button_open_source_licenses);
        btnUserAgreement = findViewById(R.id.button_user_agreement);
        btnPrivacyPolicy = findViewById(R.id.button_privacy_policy);
        tvTotalSize = findViewById(R.id.tv_total_size);
        viewLineMoreSettings = findViewById(R.id.view_line_more_settings);
        containerTopOccupySize = findViewById(R.id.container_top_occupy_size);
        initMoreSettings();
    }

    private void initMoreSettings() {
        if (isShowMoreSettings) {
            btnMoreSettings.setVisibility(View.VISIBLE);
            viewLineMoreSettings.setVisibility(View.VISIBLE);
        } else {
            btnMoreSettings.setVisibility(View.GONE);
            viewLineMoreSettings.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        initAppSize();
        btnGitHub.setHint("hello-sud");
    }

    private void initAppSize() {
        List<AppSizeView.AppSizeModel> list = new ArrayList<>();
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#fc955b"), "SudMGP Core", APPConfig.SudMGPCoreSize));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#fc5bca"), "SudMGP ASR", APPConfig.SudMGPASRSize));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#614bff"), "HelloSud", APPConfig.HelloSudSize));
//        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#33000000"), "Zego RTC SDK", APPConfig.ZegoRTCSDKSize));
//        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#1a000000"), "Agora RTC SDK", APPConfig.AgoraRTCSDKSize));
        list.add(new AppSizeView.AppSizeModel(Color.parseColor("#1a000000"), "RTC SDK", APPConfig.RTCSDKSize));
        appSizeView.setDatas(list);
        long totalSize = 0;
        for (AppSizeView.AppSizeModel appSizeModel : list) {
            totalSize += appSizeModel.size;
        }
        tvTotalSize.setText(getString(R.string.total_value, FileUtils.formatFileSize(totalSize)));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnVersionInfo.setOnClickListener(this);
        btnMoreSettings.setOnClickListener(this);
        btnChangeLanguage.setOnClickListener(this);
        btnGitHub.setOnClickListener(this);
        btnOpenSource.setOnClickListener(this);
        btnUserAgreement.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
        containerTopOccupySize.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVersionInfo) { // 版本信息
            startActivity(new Intent(requireContext(), VersionInfoActivity.class));
        } else if (v == btnMoreSettings) { // 更多设置
            startActivity(new Intent(requireContext(), MoreSettingsActivity.class));
        } else if (v == btnChangeLanguage) { // 切换语言
//            ToastUtils.showShort(R.string.be_making);
            startActivity(new Intent(requireContext(), LanguageActivity.class));
        } else if (v == btnGitHub) { // github
            IntentUtils.openUrl(getContext(), APPConfig.GIT_HUB_URL);
        } else if (v == btnOpenSource) { // 开源协议
            RouterUtils.openUrl(getContext(), getString(R.string.user_agreement_title), APPConfig.APP_LICENSE_URL);
        } else if (v == btnUserAgreement) { // 用户协议
            RouterUtils.openUrl(getContext(), getString(R.string.user_agreement_title), APPConfig.USER_PROTOCAL_URL);
        } else if (v == btnPrivacyPolicy) { // 隐私政策
            RouterUtils.openUrl(getContext(), getString(R.string.user_privacy_title), APPConfig.USER_PRIVACY_URL);
        } else if (v == containerTopOccupySize) { // 点击了顶部的占用大小
            onClickTopOccupySize();
        }
    }

    // 点击了顶部的"占用大小"区域
    private void onClickTopOccupySize() {
        long timestamp = System.currentTimeMillis();
        if (Math.abs(timestamp - clickTimestamp) < 1000) {
            clickCount++;
            if (clickCount >= 3) {
                isShowMoreSettings = true;
                initMoreSettings();
            }
        } else {
            clickCount = 1;
        }
        clickTimestamp = timestamp;
    }

}