package tech.sud.mgp.hello.ui.scenes.league.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.HashSet;
import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 联赛场景游戏业务
 */
public class LeagueGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickJoinBtnLiveData = new MutableLiveData<>();
    public MutableLiveData<Object> clickReadyBtnLiveData = new MutableLiveData<>();
    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>();
    public MutableLiveData<Object> gameStartedLiveData = new MutableLiveData<>();
    public MutableLiveData<SudGIPMGState.MGCommonGameState> gameStateLiveData = new MutableLiveData<>();
    public MutableLiveData<SudGIPMGState.MGCommonPlayerIn> playerInLiveData = new MutableLiveData<>();

    // region 游戏侧回调

    /** 游戏加载完成 */
    @Override
    public void onGameStarted() {
        super.onGameStarted();
        gameStartedLiveData.setValue(null);
    }

    /**
     * 4. 加入游戏按钮点击状态
     * mg_common_self_click_join_btn
     */
    @Override
    public void onGameMGCommonSelfClickJoinBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickJoinBtn model) {
        super.onGameMGCommonSelfClickJoinBtn(handle, model);
        clickJoinBtnLiveData.setValue(null);
    }

    /**
     * 6. 准备按钮点击状态
     * mg_common_self_click_ready_btn
     */
    @Override
    public void onGameMGCommonSelfClickReadyBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickReadyBtn model) {
        super.onGameMGCommonSelfClickReadyBtn(handle, model);
        clickReadyBtnLiveData.setValue(null);
    }

    /**
     * 8. 开始游戏按钮点击状态
     * mg_common_self_click_start_btn
     */
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

    /**
     * 10. 游戏状态
     * mg_common_game_state
     */
    @Override
    public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudGIPMGState.MGCommonGameState model) {
        super.onGameMGCommonGameState(handle, model);
        gameStateLiveData.setValue(model);
    }

    // endregion 游戏侧回调

    // region 向游戏侧发送状态

    /** 加入游戏 */
    public void joinGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfInV2(true, -1, true, 1);
    }

    /** 准备 */
    public void readyGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(true);
    }

    /** 开始游戏 */
    public void startGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(true, null, null);
    }

    /**
     * 发送
     * 16. 设置游戏中的AI玩家（2022-05-11新增）
     *
     * @param aiPlayers AI玩家
     * @param isReady   机器人加入后是否自动准备 1：自动准备，0：不自动准备 默认为1
     */
    public void notifyAPPCommonGameAddAIPlayers(List<SudGIPAPPState.AIPlayers> aiPlayers, int isReady) {
        sudFSTAPPDecorator.notifyAPPCommonGameAddAIPlayers(aiPlayers, isReady);
    }

    @Override
    public void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudGIPMGState.MGCommonPlayerIn model) {
        super.onPlayerMGCommonPlayerIn(handle, userId, model);
        playerInLiveData.setValue(model);
    }
    // endregion 向游戏侧发送状态

    // region 外部调用方法

    /** 获取已经加入了游戏的用户列表 */
    public HashSet<String> getPlayerInSet() {
        return sudFSMMGDecorator.getSudFSMMGCache().getPlayerInSet();
    }
    // endregion 外部调用方法

}
