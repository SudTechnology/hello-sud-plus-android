package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.TextView;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiDialog;
import tech.sud.mgp.hello.ui.scenes.custom.viewmodel.CustomViewModel;

/**
 * custom场景
 */
public class CustomActivity extends AbsAudioRoomActivity<CustomViewModel> {
    private TextView apiTv;

    @Override
    protected CustomViewModel initGameViewModel() {
        return new CustomViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        apiTv = findViewById(R.id.api_btn);
        Object configModel  = GlobalCache.getInstance().getSerializable(GlobalCache.CUSTOM_CONFIG_KEY);
        if (configModel instanceof GameConfigModel){
            gameViewModel.gameConfigModel = (GameConfigModel) configModel;
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        apiTv.setOnClickListener(v -> {
            CustomApiDialog dialog = new CustomApiDialog();
            dialog.show(getSupportFragmentManager(),null);
        });
    }
}
