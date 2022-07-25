package tech.sud.mgp.hello.ui.scenes.asr;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AudioRoomActivity;

/**
 * asr类场景
 */
public class ASRActivity extends AudioRoomActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_asr;
    }

    @Override
    protected boolean beforeSetContentView() {
        roomConfig.isSupportAddRobot = true;
        return super.beforeSetContentView();
    }
    
}