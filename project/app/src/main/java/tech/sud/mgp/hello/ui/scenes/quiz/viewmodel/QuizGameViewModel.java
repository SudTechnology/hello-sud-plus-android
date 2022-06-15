package tech.sud.mgp.hello.ui.scenes.quiz.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGCache;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 竞猜场景内的游戏业务逻辑
 */
public class QuizGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>(); // 开始按钮点击事件
    public MutableLiveData<Boolean> selfIsInLiveData = new MutableLiveData<>(); // 自己是否已经加入了游戏
    public MutableLiveData<Boolean> gameLoadingCompletedLiveData = new MutableLiveData<>(); // 游戏是否已加载完成
    public MutableLiveData<Integer> gameStateChangedLiveData = new MutableLiveData<>(); // 游戏状态变化
    public MutableLiveData<SudMGPMGState.MGCommonGameSettle> gameSettleLiveData = new MutableLiveData<>(); // 游戏结算

    // 游戏加载完成
    @Override
    public void onGameStarted() {
        super.onGameStarted();
        gameLoadingCompletedLiveData.setValue(true);
    }

    @Override
    protected void destroyMG() {
        super.destroyMG();
        if (getPlayingGameId() > 0) {
            gameLoadingCompletedLiveData.setValue(false);
        }
    }

    // 玩家加入状态
    @Override
    public void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerIn model) {
        super.onPlayerMGCommonPlayerIn(handle, userId, model);
        selfIsInLiveData.setValue(isSelfInGame());
    }

    // 游戏状态
    @Override
    public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
        super.onGameMGCommonGameState(handle, model);
        gameStateChangedLiveData.setValue(model.gameState);
    }

    /** 游戏是否已加载完成 */
    public boolean gameLoadingCompleted() {
        Boolean loadingCompleted = gameLoadingCompletedLiveData.getValue();
        return loadingCompleted != null && loadingCompleted;
    }

    /** 获取已经加入了游戏的用户id列表 */
    public List<Long> getPlayers() {
        SudFSMMGCache sudFSMMGCache = sudFSMMGDecorator.getSudFSMMGCache();
        HashSet<String> playerInSet = sudFSMMGCache.getPlayerInSet();
        if (playerInSet == null) {
            return null;
        }
        List<Long> list = new ArrayList<>();
        for (String userId : playerInSet) {
            try {
                list.add(Long.parseLong(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // 开始按钮点击事件
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

    @Override
    public void onGameMGCommonGameSettle(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameSettle model) {
        super.onGameMGCommonGameSettle(handle, model);
        gameSettleLiveData.setValue(model);
    }

}
