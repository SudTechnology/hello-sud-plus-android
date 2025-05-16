package tech.sud.mgp.hello.ui.scenes.quiz.viewmodel;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tech.sud.gip.SudGIPWrapper.decorator.SudFSMMGCache;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 竞猜场景内的游戏业务逻辑
 */
public class QuizGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>(); // 开始按钮点击事件
    public MutableLiveData<Object> clickJoinBtnLiveData = new MutableLiveData<>(); // 加入游戏按钮点击事件
    public MutableLiveData<Boolean> selfIsInLiveData = new MutableLiveData<>(); // 自己是否已经加入了游戏

    // 玩家加入状态
    @Override
    public void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudGIPMGState.MGCommonPlayerIn model) {
        super.onPlayerMGCommonPlayerIn(handle, userId, model);
        selfIsInLiveData.setValue(isSelfInGame());
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
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

    @Override
    public void onGameMGCommonSelfClickJoinBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickJoinBtn model) {
        super.onGameMGCommonSelfClickJoinBtn(handle, model);
        clickJoinBtnLiveData.setValue(null);
    }

}
