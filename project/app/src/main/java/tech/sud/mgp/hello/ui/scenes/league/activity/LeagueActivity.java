package tech.sud.mgp.hello.ui.scenes.league.activity;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.widget.dialog.InfoDialog;
import tech.sud.mgp.hello.service.room.repository.RoomRepository;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.ui.common.utils.DialogUtils;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.model.LeagueModel;
import tech.sud.mgp.hello.ui.scenes.league.viewmodel.LeagueGameViewModel;
import tech.sud.mgp.hello.ui.scenes.league.widget.LeagueGameSettleDialog;

/**
 * 联赛场景 页面
 */
public class LeagueActivity extends AbsAudioRoomActivity<LeagueGameViewModel> {

    public List<SudGIPAPPState.AIPlayers> robotList; // 机器人列表
    private boolean isProcessClickStartBtn; // 是否正在处理点击开始按钮

    @Override
    protected LeagueGameViewModel initGameViewModel() {
        return new LeagueGameViewModel();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameViewModel.gameConfigModel.ui.gameSettle.hide = true; // 隐藏结算界面
        gameViewModel.gameConfigModel.ui.lobby_game_setting.hide = true; // 隐藏玩法设置
        gameViewModel.gameConfigModel.ui.lobby_players.hide = false; // 展示玩家游戏位
        gameViewModel.gameConfigModel.ui.lobby_players.custom = true; // 点击大厅游戏位加入游戏
        gameViewModel.gameConfigModel.ui.join_btn.custom = true; // 接管加入按钮事件
        gameViewModel.gameConfigModel.ui.ready_btn.custom = true; // 接管准备按钮事件
        gameViewModel.gameConfigModel.ui.start_btn.custom = true; // 接管开始按钮事件

        if (!APPConfig.GAME_IS_TEST_ENV) {
            topView.setSelectGameVisible(false); // 不显示选择游戏选项
        }
    }

    @Override
    protected void initData() {
        super.initData();
        RoomRepository.robotList(this, 9, new RxCallback<RobotListResp>() {
            @Override
            public void onSuccess(RobotListResp robotListResp) {
                super.onSuccess(robotListResp);
                if (robotListResp != null) {
                    robotList = robotListResp.robotList;
                }
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.clickJoinBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                onClickJoinBtn();
            }
        });
        gameViewModel.clickReadyBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                onClickReadyBtn();
            }
        });
        gameViewModel.clickStartBtnLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                onClickStartBtn();
            }
        });
        gameViewModel.captainChangeLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                onCaptainChange();
            }
        });
        gameViewModel.gameSettleLiveData.observe(this, new Observer<SudGIPMGState.MGCommonGameSettle>() {
            @Override
            public void onChanged(SudGIPMGState.MGCommonGameSettle gameSettle) {
                onGameSettle(gameSettle);
            }
        });
        gameViewModel.gameStateLiveData.observe(this, new Observer<SudGIPMGState.MGCommonGameState>() {
            @Override
            public void onChanged(SudGIPMGState.MGCommonGameState state) {
                kickLose();
            }
        });
        gameViewModel.playerInLiveData.observe(this, new Observer<SudGIPMGState.MGCommonPlayerIn>() {
            @Override
            public void onChanged(SudGIPMGState.MGCommonPlayerIn state) {
                onPlayerInChange(state);
            }
        });
    }

    private void onPlayerInChange(SudGIPMGState.MGCommonPlayerIn state) {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        if (state != null && !state.isIn) { // 有玩家离开了
            int playerInNumber = gameViewModel.getPlayerInNumber();
            // 决赛，自己是胜者，只剩下自己时，进行弹窗提示
            if (model.schedule != 0
                    && playerInNumber == 1
                    && gameViewModel.playerIsIn(HSUserInfo.userId)
                    && model.winner.size() > 1
                    && model.winner.contains(HSUserInfo.userId + "")) {
                InfoDialog dialog = new InfoDialog(this, getString(R.string.can_not_continue_game));
                DialogUtils.safeShowDialog(this, dialog);
            }
        }
    }

    private void kickLose() {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        // 执行踢人的逻辑，保留胜者
        if (model.schedule > 0 && playingGameId > 0
                && gameViewModel.isCaptain(HSUserInfo.userId)
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.PLAYING
                && gameViewModel.getGameState() != SudGIPMGState.MGCommonGameState.LOADING) {
            HashSet<String> playerInSet = gameViewModel.getPlayerInSet();
            // 如果自己输了，那么执行自己退出游戏的逻辑
            boolean selfLose = false;
            String selfUserId = HSUserInfo.userId + "";
            for (String userId : playerInSet) {
                if (!model.winner.contains(userId)) {
                    if (selfUserId.equals(userId)) {
                        selfLose = true;
                    } else {
                        gameViewModel.notifyAPPCommonSelfKick(userId);
                    }
                }
            }
            if (selfLose) {
                gameViewModel.exitGame();
            }
        }
    }

    // 游戏结算
    private void onGameSettle(SudGIPMGState.MGCommonGameSettle gameSettle) {
        showGameSettleDialog(gameSettle);
        if (binder != null) {
            binder.onGameSettle(gameSettle);
        }
        kickLose();
    }

    /**
     * 是否可以结束游戏变化，统一不显示结束游戏按钮
     *
     * @param isShow
     */
    @Override
    protected void onShowFinishGameChange(Boolean isShow) {
        if (APPConfig.GAME_IS_TEST_ENV) {
            super.onShowFinishGameChange(isShow);
        } else {
            super.onShowFinishGameChange(false);
        }
    }

    // 展示结算弹窗
    private void showGameSettleDialog(SudGIPMGState.MGCommonGameSettle gameSettle) {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        LeagueGameSettleDialog dialog = LeagueGameSettleDialog.newInstance(gameSettle, model.copy());
        dialog.setEventListener(new LeagueGameSettleDialog.LeagueGameSettleEventListener() {
            @Override
            public void onFinish() {
                finish();
            }

            @Override
            public void onBackHomePage() {
                delayExitRoom(true);
            }
        });
        DialogUtils.safeShowDialog(this, dialog, getSupportFragmentManager(), null);
    }

    // 队长变化
    private void onCaptainChange() {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        boolean isCaptain = gameViewModel.isCaptain(HSUserInfo.userId);
        if (isCaptain) {
            if (model.schedule == 0) {
                if (robotList != null && robotList.size() > 0) {
                    gameViewModel.notifyAPPCommonGameAddAIPlayers(robotList.subList(0, 1), 1);
                }
            } else {
                List<SudGIPAPPState.AIPlayers> winRobotList = findWinRobotList(model);
                if (winRobotList.size() > 0) {
                    gameViewModel.notifyAPPCommonGameAddAIPlayers(winRobotList, 1);
                }
            }
        }
    }

    // 查询已经赢了的机器人
    private List<SudGIPAPPState.AIPlayers> findWinRobotList(LeagueModel model) {
        List<SudGIPAPPState.AIPlayers> list = new ArrayList<>();
        for (String userId : model.winner) {
            SudGIPAPPState.AIPlayers aiPlayer = findAIPlayer(userId);
            if (aiPlayer != null) {
                list.add(aiPlayer);
            }
        }
        return list;
    }

    private SudGIPAPPState.AIPlayers findAIPlayer(String userId) {
        if (userId != null && robotList != null) {
            for (SudGIPAPPState.AIPlayers aiPlayers : robotList) {
                if (userId.equals(aiPlayers.userId)) {
                    return aiPlayers;
                }
            }
        }
        return null;
    }

    // 点击了开始游戏
    private void onClickStartBtn() {
        LeagueModel model = getLeagueModel();
        if (model == null || isProcessClickStartBtn) {
            return;
        }
        isProcessClickStartBtn = true;
        if (model.schedule == 0) {
            // 第一局，补充机器人，再开始游戏
            if (robotList != null && robotList.size() > 0) {
                gameViewModel.notifyAPPCommonGameAddAIPlayers(robotList, 1);
                isProcessClickStartBtn = false;
                gameViewModel.startGame();
            } else {
                RoomRepository.robotList(this, 9, new RxCallback<RobotListResp>() {
                    @Override
                    public void onSuccess(RobotListResp robotListResp) {
                        super.onSuccess(robotListResp);
                        if (robotListResp != null) {
                            robotList = robotListResp.robotList;
                        }
                    }

                    @Override
                    public void onFinally() {
                        super.onFinally();
                        isProcessClickStartBtn = false;
                        if (robotList != null && robotList.size() > 0) {
                            gameViewModel.notifyAPPCommonGameAddAIPlayers(robotList, 1);
                            gameViewModel.startGame();
                        }
                    }
                });
            }
        } else {
            // 其他局，直接开始
            isProcessClickStartBtn = false;
            gameViewModel.startGame();
        }
    }

    // 点击了准备
    private void onClickReadyBtn() {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        if (model.schedule == 0) {
            gameViewModel.readyGame();
        } else {
            // 如果不是获取方，不能准备
            if (model.winner.contains(HSUserInfo.userId + "")) {
                gameViewModel.readyGame();
            } else {
                gameViewModel.exitGame();
                ToastUtils.showShort(R.string.cannot_join_game);
            }
        }
    }

    // 点击了加入游戏
    private void onClickJoinBtn() {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        if (model.schedule == 0) {
            gameViewModel.joinGame();
        } else {
            // 如果不是获取方，不能准备
            if (model.winner.contains(HSUserInfo.userId + "")) {
                gameViewModel.joinGame();
            } else {
                ToastUtils.showShort(R.string.cannot_join_game);
            }
        }
    }

    private LeagueModel getLeagueModel() {
        if (binder != null) {
            return binder.getLeagueModel();
        }
        return null;
    }

    @Override
    protected void autoJoinGame() {
        LeagueModel model = getLeagueModel();
        if (model == null) {
            return;
        }
        // 第一局比赛才自动加入游戏
        if (model.schedule == 0) {
            super.autoJoinGame();
        }
    }

}