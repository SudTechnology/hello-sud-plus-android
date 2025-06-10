package tech.sud.mgp.hello.ui.scenes.base.interaction.racecar.viewmodel;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.InteractionGameViewModel;

/**
 * 疯狂赛车 游戏逻辑
 */
public class RacecarGameViewModel extends InteractionGameViewModel {

    public FragmentActivity fragmentActivity;
    public long roomId;

    private boolean gameIsReady; // 游戏是否已加载
    private boolean isShowingGameScene; // 游戏的主界面是否已显示

    public MutableLiveData<Object> gamePrepareCompletedLiveData = new MutableLiveData<>(); // 游戏准备完成
    public MutableLiveData<Object> destroyInteractionGameLiveData = new MutableLiveData<>(); // 销毁通知
    public MutableLiveData<SudGIPMGState.MGCommonSetClickRect> gameClickRectLiveData = new MutableLiveData<>(); // 点击区域
    public MutableLiveData<SudGIPMGState.MGCommonUsersInfo> gameGetUserInfoListLiveData = new MutableLiveData<>(); // 游戏获取用户信息

    // region 游戏回调

    /**
     * 41. 设置app提供给游戏可点击区域(赛车)
     * mg_common_set_click_rect
     */
    @Override
    public void onGameMGCommonSetClickRect(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSetClickRect model) {
        super.onGameMGCommonSetClickRect(handle, model);
        gameClickRectLiveData.setValue(model);
    }

    /**
     * 42. 通知app提供对应uids列表玩家的数据(赛车)
     * mg_common_users_info
     */
    @Override
    public void onGameMGCommonUsersInfo(ISudFSMStateHandle handle, SudGIPMGState.MGCommonUsersInfo model) {
        super.onGameMGCommonUsersInfo(handle, model);
        gameGetUserInfoListLiveData.setValue(model);
    }

    /**
     * 43. 通知app游戏前期准备完成(赛车)
     * mg_common_game_prepare_finish
     */
    @Override
    public void onGameMGCommonGamePrepareFinish(ISudFSMStateHandle handle, SudGIPMGState.MGCommonGamePrepareFinish model) {
        super.onGameMGCommonGamePrepareFinish(handle, model);
        gameIsReady = true;
        gamePrepareCompletedLiveData.setValue(null);

        // 发送自定义帮助内容
        SudGIPAPPState.APPCommonCustomHelpInfo helpInfo = new SudGIPAPPState.APPCommonCustomHelpInfo();
        helpInfo.content = new ArrayList<>();
        helpInfo.content.add(fragmentActivity.getString(R.string.racecar_rule_1));
        helpInfo.content.add(fragmentActivity.getString(R.string.racecar_rule_2));
        notifyAPPCommonCustomHelpInfo(helpInfo);
    }

    /**
     * 44. 通知app游戏主界面已显示(赛车)
     * mg_common_show_game_scene
     */
    @Override
    public void onGameMGCommonShowGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGCommonShowGameScene model) {
        super.onGameMGCommonShowGameScene(handle, model);
        isShowingGameScene = true;
    }

    /**
     * 45. 通知app游戏主界面已隐藏(赛车)
     * mg_common_hide_game_scene
     */
    @Override
    public void onGameMGCommonHideGameScene(ISudFSMStateHandle handle, SudGIPMGState.MGCommonHideGameScene model) {
        super.onGameMGCommonHideGameScene(handle, model);
        isShowingGameScene = false;
//        checkDestroyBaseball();
    }

    // endregion 游戏回调

    // region 调用游戏

    /**
     * 27. app通知游戏玩家信息列表 (赛车)
     * app_common_users_info
     */
    public void notifyAppCommonUsersInfo(SudGIPAPPState.APPCommonUsersInfo model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_COMMON_USERS_INFO, model);
    }

    /**
     * 28. app通知游戏自定义帮助内容 (赛车)
     * app_common_custom_help_info
     */
    public void notifyAPPCommonCustomHelpInfo(SudGIPAPPState.APPCommonCustomHelpInfo model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_COMMON_CUSTOM_HELP_INFO, model);
    }

    /**
     * 29. app主动调起主界面(赛车)
     * app_common_show_game_scene
     */
    public void notifyAppCommonShowGameScene(SudGIPAPPState.APPCommonShowGameScene model) {
        sudFSTAPPDecorator.notifyStateChange(SudGIPAPPState.APP_COMMON_SHOW_GAME_SCENE, model);
    }

    // endregion 调用游戏

    /** 是否已准备就绪 */
    public boolean interactionGameIsReady() {
        if (playingGameId == GameIdCons.BASEBALL && gameIsReady) {
            return true;
        }
        return false;
    }

    @Override
    protected void destroyMG() {
        super.destroyMG();
        gameIsReady = false;
        isShowingGameScene = false;
    }

    /** 判断是否要销毁交互游戏 */
    private void checkDestroyInteractionGame() {
        if (isShowingGameScene) {
            return;
        }
        destroyInteractionGameLiveData.setValue(null);
    }

}
