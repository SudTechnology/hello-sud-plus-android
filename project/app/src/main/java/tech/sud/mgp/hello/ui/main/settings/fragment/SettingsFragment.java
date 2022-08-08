package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.view.View;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.ui.main.settings.activity.AboutActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.SettingsActivity;
import tech.sud.mgp.hello.ui.main.settings.widget.WalletListView;

/**
 * 首页当中的设置页
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private WalletListView walletListView;
    private SettingButton btnSettings;
    private SettingButton btnAbout;

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
        walletListView = findViewById(R.id.wallet_list_view);
        btnSettings = findViewById(R.id.button_settings);
        btnAbout = findViewById(R.id.button_about);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
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