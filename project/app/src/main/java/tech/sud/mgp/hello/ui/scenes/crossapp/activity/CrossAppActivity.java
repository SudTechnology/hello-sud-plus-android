package tech.sud.mgp.hello.ui.scenes.crossapp.activity;

import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.view.CrossAppStatusView;

/**
 * 跨APP对战 场景
 */
public class CrossAppActivity extends BaseRoomActivity<AppGameViewModel> {

    private CrossAppStatusView crossAppStatusView;

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cross_app;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        crossAppStatusView = findViewById(R.id.cross_app_status_view);

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        chatParams.setMarginEnd(DensityUtils.dp2px(60));
        chatParams.bottomToTop = R.id.room_bottom_view;
        chatView.setLayoutParams(chatParams);
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        micView.setMicView(new AudioRoomGameMicView(this));
        chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
    }

}
