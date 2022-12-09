package tech.sud.mgp.hello.ui.scenes.crossapp.activity;

import android.text.TextUtils;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.crossapp.viewmodel.CrossAppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.dialog.SelectMatchGameDialog;
import tech.sud.mgp.hello.ui.scenes.crossapp.widget.view.CrossAppStatusView;

/**
 * 跨APP对战 场景
 */
public class CrossAppActivity extends BaseRoomActivity<CrossAppGameViewModel> {

    private CrossAppStatusView crossAppStatusView;

    @Override
    protected CrossAppGameViewModel initGameViewModel() {
        return new CrossAppGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cross_app;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameViewModel.gameConfigModel.ui.game_settle_close_btn.custom = true;
        gameViewModel.gameConfigModel.ui.game_settle_again_btn.hide = true;

        crossAppStatusView = findViewById(R.id.cross_app_status_view);

        crossAppStatusView.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams chatParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        chatParams.setMarginEnd(DensityUtils.dp2px(60));
        chatParams.bottomToTop = R.id.room_bottom_view;
        chatView.setLayoutParams(chatParams);

        micView.setMicView(new AudioRoomGameMicView(this));
        chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
    }

    @Override
    protected void initData() {
        super.initData();
        crossAppStatusView.setCrossAppModel(getCrossAppModel());
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        topView.setSelectGameClickListener(v -> {
            onClickChangeMatchGame();
        });
        crossAppStatusView.setOnClickStallListener(userInfoResp -> {
            if (binder != null) {
                Integer intentIndex = null;
                if (userInfoResp != null) {
                    intentIndex = userInfoResp.index;
                }
                binder.crossAppJoinTeam(intentIndex, null);
            }
        });
        crossAppStatusView.setExitTeamOnClickListener(v -> {
            if (binder != null) {
                binder.crossAppExitTeam();
            }
        });
        crossAppStatusView.setTeamFastMatchOnClickListener(v -> {
            startMatch();
        });
        crossAppStatusView.setJoinTeamOnClickListener(v -> {
            if (binder != null) {
                binder.crossAppJoinTeam(null, null);
            }
        });
        crossAppStatusView.setCancelMatchOnClickListener(v -> {
            onClickCancelMatch();
        });
        crossAppStatusView.setChangeGameOnClickListener(v -> {
            onClickChangeMatchGame();
        });
        crossAppStatusView.setAnewMatchOnClickListener(v -> {
            startMatch();
        });

        gameViewModel.clickGameSettleCloseBtnLiveData.observe(this, model -> {
            if (binder != null) {
                binder.crossAppGameSettle();
            }
        });
    }

    private void onClickCancelMatch() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(this, getString(R.string.cancel_match_title),
                getString(R.string.do_wait), getString(R.string.confirm));
        dialog.setOnChooseListener(new SimpleChooseDialog.OnChooseListener() {
            @Override
            public void onChoose(int index) {
                if (index == 1) {
                    cancelMatch();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void cancelMatch() {
        if (binder != null) {
            binder.crossAppCancelMatch();
        }
    }

    private void startMatch() {
        if (binder != null) {
            binder.crossAppStartMatch();
        }
    }

    private void onClickChangeMatchGame() {
        showCrossAppMatchGameDialog();
    }

    private void showCrossAppMatchGameDialog() {
        SelectMatchGameDialog dialog = SelectMatchGameDialog.newInstance(SelectMatchGameDialog.MODE_CHANGE);
        dialog.setOnSelectedListener(gameModel -> {
            if (binder != null) {
                binder.crossAppChangeGame(gameModel);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        CrossAppModel crossAppModel = getCrossAppModel();
        updateStatusViewVisiable(crossAppModel);
        updateSelectGameVisiable(crossAppModel);
    }

    private void updateSelectGameVisiable(CrossAppModel model) {
        if (model != null && model.matchStatus == CrossAppMatchStatus.TEAM) {
            topView.setSelectGameVisible(model.captain == HSUserInfo.userId);
        } else {
            topView.setSelectGameVisible(false);
        }
    }

    private void updateStatusViewVisiable(CrossAppModel crossAppModel) {
        if (playingGameId > 0 || crossAppModel == null) {
            crossAppStatusView.setVisibility(View.GONE);
        } else {
            switch (crossAppModel.matchStatus) {
                case CrossAppMatchStatus.TEAM:
                case CrossAppMatchStatus.MATCHING:
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
        crossAppStatusView.setCrossAppModel(model);
        updateStatusViewVisiable(model);
        updateSelectGameVisiable(model);
    }

    @Override
    protected String getGameRoomId() {
        String matchRoomId = getMatchRoomId();
        if (TextUtils.isEmpty(matchRoomId)) {
            return super.getGameRoomId();
        } else {
            return matchRoomId;
        }
    }

    private String getMatchRoomId() {
        CrossAppModel crossAppModel = getCrossAppModel();
        if (crossAppModel != null) {
            return crossAppModel.matchRoomId;
        }
        return null;
    }

    private CrossAppModel getCrossAppModel() {
        if (binder != null) {
            return binder.getCrossAppModel();
        }
        return roomInfoModel.crossAppModel;
    }

}
