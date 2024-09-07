package tech.sud.mgp.hello.ui.scenes.audio.activity;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 语聊房页面
 */
public class AudioRoomActivity extends AbsAudioRoomActivity<AppGameViewModel> {

    @Override
    protected AppGameViewModel initGameViewModel() {
        return new AppGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameViewModel.gameConfigModel.ui.lobby_players.hide = false; // 展示玩家游戏位
    }

}