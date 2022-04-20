package tech.sud.mgp.hello.ui.main.settings.activity;

import android.content.Intent;
import android.view.View;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.ui.main.settings.fragment.SettingButton;

/**
 * 更多设置页面
 */
public class MoreSettingsActivity extends BaseActivity implements View.OnClickListener {

    private SettingButton btnChangeRtc;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        btnChangeRtc = findViewById(R.id.button_change_rtc);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnChangeRtc.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        btnChangeRtc.setHint(AppData.getInstance().getRtcName());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_change_rtc) {
            startActivity(new Intent(this, ChangeRtcActivity.class));
        }
    }
}
