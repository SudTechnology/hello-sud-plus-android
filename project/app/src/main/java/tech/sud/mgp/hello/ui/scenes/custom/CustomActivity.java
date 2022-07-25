package tech.sud.mgp.hello.ui.scenes.custom;

import android.widget.TextView;

import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.custom.dialog.CustomApiDialog;
import tech.sud.mgp.hello.ui.scenes.custom.viewmodel.CustomGameViewModel;

/**
 * custom场景
 */
public class CustomActivity extends AbsAudioRoomActivity<CustomGameViewModel> {
    private TextView apiTv;

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }
    
    @Override
    protected CustomGameViewModel initGameViewModel() {
        return new CustomGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        apiTv = findViewById(R.id.api_btn);
        Object configModel = GlobalCache.getInstance().getSerializable(GlobalCache.CUSTOM_CONFIG_KEY);
        if (configModel instanceof GameConfigModel) {
            gameViewModel.gameConfigModel = (GameConfigModel) configModel;
        } else {
            gameViewModel.gameConfigModel = new GameConfigModel();
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        apiTv.setOnClickListener(v -> {
            CustomApiDialog dialog = CustomApiDialog.newInstance();
            dialog.setListener(i -> {
                switch (i) {
                    case 0: {
                        gameViewModel.joinGame();
                        break;
                    }
                    case 1: {
                        gameViewModel.readyGame();
                        break;
                    }
                    case 2: {
                        gameViewModel.startGame();
                        break;
                    }
                    case 3: {
                        gameViewModel.exitGame();
                        break;
                    }
                    case 4: {
                        gameViewModel.cancelReadyGame();
                        break;
                    }
                    case 5: {
                        gameViewModel.escapeGame();
                        break;
                    }
                    case 6: {
                        gameViewModel.finishGame();
                        break;
                    }
                }
                dialog.dismiss();
            });
            dialog.show(getSupportFragmentManager(), null);
        });
    }
}
