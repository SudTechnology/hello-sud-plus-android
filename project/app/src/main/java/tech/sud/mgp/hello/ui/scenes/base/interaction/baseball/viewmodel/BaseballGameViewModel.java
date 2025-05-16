package tech.sud.mgp.hello.ui.scenes.base.interaction.baseball.viewmodel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.InteractionGameViewModel;

/**
 * 棒球 游戏逻辑
 */
public class BaseballGameViewModel extends InteractionGameViewModel {

    public FragmentActivity fragmentActivity;
    public long roomId;

    private boolean baseballIsReady; // 棒球游戏是否已加载
    private boolean isShowingBaseballScene; // 棒球的主界面是否已显示

    public MutableLiveData<Object> baseballPrepareCompletedLiveData = new MutableLiveData<>(); // 棒球准备完成
    public MutableLiveData<Object> destroyBaseballLiveData = new MutableLiveData<>(); // 销毁通知
    public MutableLiveData<SudGIPMGState.MGBaseballSetClickRect> baseballClickRectLiveData = new MutableLiveData<>(); // 棒球点击区域

    // region 棒球回调

    /**
     * 1. 查询排行榜数据(棒球)
     * mg_baseball_ranking
     */
    @Override
    public void onGameMGBaseballRanking(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballRanking model) {
        super.onGameMGBaseballRanking(handle, model);
        GameRepository.baseballRanking(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppBaseballRanking>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppBaseballRanking resp) {
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
    public void onGameMGBaseballMyRanking(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballMyRanking model) {
        super.onGameMGBaseballMyRanking(handle, model);
        GameRepository.baseballMyRanking(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppBaseballMyRanking>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppBaseballMyRanking resp) {
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
    public void onGameMGBaseballRangeInfo(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballRangeInfo model) {
        super.onGameMGBaseballRangeInfo(handle, model);
        GameRepository.baseballRangeInfo(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppBaseballRangeInfo>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppBaseballRangeInfo resp) {
                super.onSuccess(resp);
                notifyAppBaseballRangeInfo(resp);
            }
        });
    }

    /**
     * 4. 设置app提供给游戏可点击区域(棒球)
     * mg_baseball_set_click_rect
     */
    @Override
    public void onGameMGBaseballSetClickRect(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballSetClickRect model) {
        super.onGameMGBaseballSetClickRect(handle, model);
        baseballClickRectLiveData.setValue(model);
    }

    /**
     * 5. 前期准备完成(棒球)
     * mg_baseball_prepare_finish
     */
    @Override
    public void onGameMGBaseballPrepareFinish(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballPrepareFinish model) {
        super.onGameMGBaseballPrepareFinish(handle, model);
        baseballIsReady = true;
        baseballPrepareCompletedLiveData.setValue(null);
    }

    /**
     * 6. 主界面已显示(棒球)
     * mg_baseball_show_game_scene
     */
    @Override
    public void onGameMGBaseballShowGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballShowGameScene model) {
        super.onGameMGBaseballShowGameScene(handle, model);
        isShowingBaseballScene = true;
    }

    /**
     * 7. 主界面已隐藏(棒球)
     * mg_baseball_hide_game_scene
     */
    @Override
    public void onGameMGBaseballHideGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballHideGameScene model) {
        super.onGameMGBaseballHideGameScene(handle, model);
        isShowingBaseballScene = false;
//        checkDestroyBaseball();
    }

    /**
     * 8. 获取文本配置数据(棒球)
     * mg_baseball_text_config
     */
    @Override
    public void onGameMGBaseballTextConfig(ISudFSMStateHandle handle, SudGIPMGState.MGBaseballTextConfig model) {
        super.onGameMGBaseballTextConfig(handle, model);
        GameRepository.baseballTextConfig(fragmentActivity, model, new RxCallback<SudGIPAPPState.AppBaseballTextConfig>() {
            @Override
            public void onSuccess(SudGIPAPPState.AppBaseballTextConfig resp) {
                super.onSuccess(resp);
                notifyAppBaseballTextConfig(resp);
            }
        });
    }
    // endregion 棒球回调

    // region 调用棒球

    /**
     * 1. 下发游戏客户端查询排行榜数据(棒球)
     * app_baseball_ranking
     */
    public void notifyAppBaseballRanking(SudGIPAPPState.AppBaseballRanking model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_RANKING, model);
    }

    /**
     * 2. 下发游戏客户端查询我的排名数据(棒球)
     * app_baseball_my_ranking
     */
    public void notifyAppBaseballMyRanking(SudGIPAPPState.AppBaseballMyRanking model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_MY_RANKING, model);
    }

    /**
     * 3. 下发游戏客户端查询排在自己前后的玩家数据(棒球)
     * app_baseball_range_info
     */
    public void notifyAppBaseballRangeInfo(SudGIPAPPState.AppBaseballRangeInfo model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_RANGE_INFO, model);
    }

    /**
     * 4. app主动调起主界面(棒球)
     * app_baseball_show_game_scene
     */
    public void notifyAppBaseballShowGameScene(SudGIPAPPState.AppBaseballShowGameScene model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_SHOW_GAME_SCENE, model);
    }

    /**
     * 5. app主动隐藏主界面(棒球)
     * app_baseball_hide_game_scene
     */
    public void notifyAppBaseballHideGameScene(SudGIPAPPState.AppBaseballHideGameScene model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_HIDE_GAME_SCENE, model);
    }

    /**
     * 6. app推送需要的文本数据(棒球)
     * app_baseball_text_config
     */
    public void notifyAppBaseballTextConfig(SudGIPAPPState.AppBaseballTextConfig model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_BASEBALL_TEXT_CONFIG, model);
    }
    // endregion 调用棒球

    /** 是否已准备就绪 */
    public boolean baseballIsReady() {
        if (playingGameId == GameIdCons.BASEBALL && baseballIsReady) {
            return true;
        }
        return false;
    }

    @Override
    protected void destroyMG() {
        super.destroyMG();
        baseballIsReady = false;
        isShowingBaseballScene = false;
    }

    /** 判断是否要销毁棒球 */
    private void checkDestroyBaseball() {
        if (isShowingBaseballScene) {
            return;
        }
        destroyBaseballLiveData.setValue(null);
    }

}
