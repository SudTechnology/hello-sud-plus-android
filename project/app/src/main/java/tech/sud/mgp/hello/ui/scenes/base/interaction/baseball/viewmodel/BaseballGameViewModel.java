package tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.viewmodel;

import androidx.fragment.app.FragmentActivity;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 棒球 游戏逻辑
 */
public class BaseballGameViewModel extends AppGameViewModel {

    public FragmentActivity fragmentActivity;
    public long roomId;

    /** 启动棒球 */
    public void startBaseball() {
        switchGame(fragmentActivity, getGameRoomId(), GameIdCons.BASEBALL);
    }

    @Override
    public void switchGame(FragmentActivity activity, long gameRoomId, long gameId) {
        if (!isRunning) {
            return;
        }
        if (playingGameId == gameId && this.gameRoomId == gameRoomId) {
            return;
        }
        destroyMG();
        this.gameRoomId = gameRoomId;
        playingGameId = gameId;
        login(activity, gameId);
    }

    // region 棒球回调

    /**
     * 1. 查询排行榜数据(棒球)
     * mg_baseball_ranking
     */
    @Override
    public void onGameMGBaseballRanking(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballRanking model) {
        super.onGameMGBaseballRanking(handle, model);
        GameRepository.baseballRanking(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppBaseballRanking>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppBaseballRanking resp) {
                super.onSuccess(resp);
                notifyAppBaseballRanking(resp);
            }
        });
    }

    /**
     * 2. 查询我的排名(棒球)
     * mg_baseball_my_ranking
     */
    @Override
    public void onGameMGBaseballMyRanking(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballMyRanking model) {
        super.onGameMGBaseballMyRanking(handle, model);
        GameRepository.baseballMyRanking(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppBaseballMyRanking>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppBaseballMyRanking resp) {
                super.onSuccess(resp);
                notifyAppBaseballMyRanking(resp);
            }
        });
    }

    /**
     * 3. 查询当前距离我的前后玩家数据(棒球)
     * mg_baseball_range_info
     */
    @Override
    public void onGameMGBaseballRangeInfo(ISudFSMStateHandle handle, SudMGPMGState.MGBaseballRangeInfo model) {
        super.onGameMGBaseballRangeInfo(handle, model);
        GameRepository.baseballRangeInfo(fragmentActivity, model, new RxCallback<SudMGPAPPState.AppBaseballRangeInfo>() {
            @Override
            public void onSuccess(SudMGPAPPState.AppBaseballRangeInfo resp) {
                super.onSuccess(resp);
                notifyAppBaseballRangeInfo(resp);
            }
        });
    }
    // endregion 棒球回调

    // region 调用棒球

    /**
     * 1. 下发游戏客户端查询排行榜数据(棒球)
     * app_baseball_ranking
     */
    public void notifyAppBaseballRanking(SudMGPAPPState.AppBaseballRanking model) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_BASEBALL_RANKING, model);
    }

    /**
     * 2. 下发游戏客户端查询我的排名数据(棒球)
     * app_baseball_my_ranking
     */
    public void notifyAppBaseballMyRanking(SudMGPAPPState.AppBaseballMyRanking model) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_BASEBALL_MY_RANKING, model);
    }

    /**
     * 3. 下发游戏客户端查询排在自己前后的玩家数据(棒球)
     * app_baseball_range_info
     */
    public void notifyAppBaseballRangeInfo(SudMGPAPPState.AppBaseballRangeInfo model) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_BASEBALL_RANGE_INFO, model);
    }
    // endregion 调用棒球

}
