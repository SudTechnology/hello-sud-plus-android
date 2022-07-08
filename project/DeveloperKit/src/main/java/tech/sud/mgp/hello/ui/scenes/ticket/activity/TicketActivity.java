package tech.sud.mgp.hello.ui.scenes.ticket.activity;

import android.view.View;

import androidx.lifecycle.Observer;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.main.resp.TicketConfirmJoinResp;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.ReportGameInfoModel;
import tech.sud.mgp.hello.ui.scenes.ticket.viewmodel.TicketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.ticket.viewmodel.TicketViewModel;
import tech.sud.mgp.hello.ui.scenes.ticket.widget.JoinTicketConfirmDialog;

/**
 * 门票制场景
 */
public class TicketActivity extends AbsAudioRoomActivity<TicketGameViewModel> {

    private final TicketViewModel viewModel = new TicketViewModel();
    private TicketConfirmJoinResp ticketConfirmJoinResp;

    @Override
    protected TicketGameViewModel initGameViewModel() {
        return new TicketGameViewModel();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameViewModel.gameConfigModel.ui.start_btn.custom = true; // 接管游戏的开始按钮事件
        gameViewModel.gameConfigModel.ui.ready_btn.custom = true; // 接管游戏的准备按钮事件
        gameViewModel.gameConfigModel.ui.game_settle_again_btn.custom = true; // 接管游戏的再来一局按钮事件
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.clickStartBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                clickStartGame();
            }
        });
        gameViewModel.clickReadyBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                clickReadyGame();
            }
        });
        viewModel.ticketConfirmJoinMutableLiveData.observe(this, new Observer<TicketConfirmJoinResp>() {
            @Override
            public void onChanged(TicketConfirmJoinResp resp) {
                if (gameViewModel.getGameState() == SudMGPMGState.MGCommonGameState.IDLE) {
                    ticketConfirmJoinResp = resp;
                    gameViewModel.notifyAPPCommonSelfReady(true);
                }
            }
        });
    }

    // 点击了开始游戏
    private void clickStartGame() {
        ReportGameInfoModel reportGameInfoModel = new ReportGameInfoModel();
        if (ticketConfirmJoinResp != null) {
            reportGameInfoModel.gameSessionId = ticketConfirmJoinResp.gameSessionId;
        }
        reportGameInfoModel.sceneId = roomInfoModel.sceneType;
        gameViewModel.notifyAPPCommonSelfPlaying(true, SudJsonUtils.toJson(reportGameInfoModel));
    }

    // 点击了准备游戏
    private void clickReadyGame() {
        boolean joinTicketNoRemind = AppData.getInstance().isJoinTicketNoRemind();
        if (joinTicketNoRemind) {
            viewModel.sendJoinMsg(this, roomInfoModel);
        } else {
            JoinTicketConfirmDialog dialog = JoinTicketConfirmDialog.getInstance(roomInfoModel.gameLevel);
            dialog.show(getSupportFragmentManager(), null);
            dialog.setClickConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.sendJoinMsg(TicketActivity.this, roomInfoModel);
                }
            });
        }
    }

}