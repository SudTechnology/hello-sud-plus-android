package tech.sud.mgp.hello.ui.scenes.audio.activity;

import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;

/**
 * 语聊房页面
 */
public class AudioRoomActivity extends AbsAudioRoomActivity<GameViewModel> {

    @Override
    protected GameViewModel initGameViewModel() {
        return new GameViewModel();
    }

}