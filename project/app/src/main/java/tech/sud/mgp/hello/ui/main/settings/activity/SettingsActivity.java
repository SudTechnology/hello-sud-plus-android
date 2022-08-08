package tech.sud.mgp.hello.ui.main.settings.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.main.settings.fragment.AppSizeView;
import tech.sud.mgp.hello.ui.main.settings.fragment.SettingButton;
import tech.sud.mgp.hello.ui.scenes.common.gift.utils.FileUtils;

/**
 * 设置 页面
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private AppSizeView appSizeView;
    private TextView tvTotalSize;
    private View viewLineMoreSettings;
    private View containerTopOccupySize;
    private SettingButton btnVersionInfo;
    private SettingButton btnMoreSettings;
    private SettingButton btnChangeLanguage;

    private int clickCount = 0;
    private long clickTimestamp = 0;
    private boolean isShowMoreSettings = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        appSizeView = findViewById(R.id.app_size_view);
        btnVersionInfo = findViewById(R.id.button_version_info);
        btnMoreSettings = findViewById(R.id.button_more_settings);
        btnChangeLanguage = findViewById(R.id.button_change_language);
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
        containerTopOccupySize.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVersionInfo) { // 版本信息
            startActivity(new Intent(this, VersionInfoActivity.class));
        } else if (v == btnMoreSettings) { // 更多设置
            startActivity(new Intent(this, MoreSettingsActivity.class));
        } else if (v == btnChangeLanguage) { // 切换语言
            startActivity(new Intent(this, LanguageActivity.class));
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
