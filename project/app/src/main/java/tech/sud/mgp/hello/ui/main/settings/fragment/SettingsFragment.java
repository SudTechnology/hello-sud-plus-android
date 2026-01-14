package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.view.View;

import com.gyf.immersionbar.ImmersionBar;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.settings.activity.AboutActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.SettingsActivity;

/**
 * 首页当中的设置页
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private SettingButton btnSettings;
    private SettingButton btnAbout;
    private MainUserInfoView userInfoView;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        userInfoView = findViewById(R.id.user_info_view);
        btnSettings = findViewById(R.id.button_settings);
        btnAbout = findViewById(R.id.button_about);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_gray);
        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfoView.updateUserInfo();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        setClickListeners();
    }

    private void setClickListeners() {
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        userInfoView.setUnbindOnClickListener(v -> {
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSettings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        } else if (v == btnAbout) {
            startActivity(new Intent(getContext(), AboutActivity.class));
        }
    }

}