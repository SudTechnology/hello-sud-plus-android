package tech.sud.mgp.hello.ui.scenes.disco.viewmodel;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 蹦迪场景的游戏业务
 */
public class DiscoGameViewModel extends AppGameViewModel {

    private final DiscoActionHelper helper = new DiscoActionHelper();

    /**
     * 清场，强制所有人离开舞池，全部变成观众
     */
    public void clearSite() {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.clearSite());
    }

    /**
     * 加入主播位
     *
     * @param position field1:0-0号主播位；1-1号主播位；2-2号主播位；3-3号主播位；4-4号主播位；5-5号主播位；6-6号主播位；7-7号主播位；-1-随机，默认随机
     */
    public void joinAnchor(String position) {
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.joinAnchor(position));
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.danceMode(1));
        sudFSTAPPDecorator.notifyStateChange(SudMGPAPPState.APP_COMMON_GAME_DISCO_ACTION, helper.joinDancingFloor(null));
    }

}
