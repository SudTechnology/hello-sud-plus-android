package tech.sud.mgp.hello.ui.scenes.crossapp.activity;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.crossapp.viewmodel.CrossAppViewModel;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.view.CrossAppStatusView;

/**
 * 跨APP对战 场景
 */
public class CrossAppActivity extends BaseRoomActivity<AppGameViewModel> {

    private CrossAppStatusView crossAppStatusView;
    private CrossAppModel crossAppModel;
    private CrossAppViewModel crossAppViewModel = new CrossAppViewModel();

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

        crossAppStatusView.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        chatParams.setMarginEnd(DensityUtils.dp2px(60));
        chatParams.bottomToTop = R.id.room_bottom_view;
        chatView.setLayoutParams(chatParams);

        topView.setSelectGameVisible(false);

        micView.setMicView(new AudioRoomGameMicView(this));
        chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        topView.setSelectGameClickListener(v -> {
            // TODO: 2022/12/2  
        });
        crossAppStatusView.setOnClickStallListener(userInfoResp -> {
            if (binder != null) {
                binder.crossAppJoinTeam(userInfoResp);
            }
        });
        crossAppStatusView.setExitTeamOnClickListener(v -> {
            if (binder != null) {
                binder.crossAppExitTeam();
            }
        });
        crossAppStatusView.setTeamFastMatchOnClickListener(v -> {
            if (binder != null) {
                binder.crossAppTeamFastMatch();
            }
        });
        crossAppStatusView.setJoinTeamOnClickListener(v -> {
            if (binder != null) {
                binder.crossAppJoinTeam(null);
            }
        });
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        updateStatusViewVisiable();
    }

    private void updateStatusViewVisiable() {
        if (playingGameId > 0 || crossAppModel == null) {
            crossAppStatusView.setVisibility(View.GONE);
        } else {
            switch (crossAppModel.matchStatus) {
                case CrossAppMatchStatus.TEAM:
                case CrossAppMatchStatus.MATCHING:
                case CrossAppMatchStatus.MATCH_SUCCESS:
                case CrossAppMatchStatus.MATCH_FAILED:
                    crossAppStatusView.setVisibility(View.VISIBLE);
                    break;
                default:
                    crossAppStatusView.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void onUpdateCrossApp(CrossAppModel model) {
        super.onUpdateCrossApp(model);
        crossAppModel = model;
        crossAppStatusView.setCrossAppModel(model);
        updateStatusViewVisiable();
    }

}
