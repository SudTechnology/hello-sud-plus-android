package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiDialog;

/**
 * custom配置
 */
public class CustomConfigActivity extends BaseActivity {
    private TextView gameTipTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom_config;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameTipTv = findViewById(R.id.game_tip_tv);
        gameTipTv.setOnClickListener(v -> {
            CustomApiDialog dialog = new CustomApiDialog();
            dialog.show(getSupportFragmentManager(), null);
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
    }
}
