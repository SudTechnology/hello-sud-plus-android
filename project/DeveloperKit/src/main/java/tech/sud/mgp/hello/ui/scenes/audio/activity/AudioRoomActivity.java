package tech.sud.mgp.hello.ui.scenes.audio.activity;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.DeveloperKitGameViewModel;

/**
 * 语聊房页面
 */
public class AudioRoomActivity extends AbsAudioRoomActivity<DeveloperKitGameViewModel> {

    @Override
    protected DeveloperKitGameViewModel initGameViewModel() {
        return new DeveloperKitGameViewModel();
    }

}