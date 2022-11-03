package tech.sud.mgp.hello.ui.scenes.audio.activity;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;

/**
 * 语聊房页面
 */
public class AudioRoomActivity extends AbsAudioRoomActivity<AppRocketGameViewModel> {

    @Override
    protected AppRocketGameViewModel initGameViewModel() {
        return new AppRocketGameViewModel();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }
}