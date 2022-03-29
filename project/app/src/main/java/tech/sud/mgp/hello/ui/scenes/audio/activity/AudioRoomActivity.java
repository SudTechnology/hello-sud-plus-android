package tech.sud.mgp.hello.ui.scenes.audio.activity;

import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseSceneActivity;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;

/**
 * 语聊房页面
 */
public class AudioRoomActivity extends BaseSceneActivity {

    private AudioRoomMicStyle audioRoomMicStyle;

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0) { // 玩着游戏
            setMicStyle(AudioRoomMicStyle.GAME);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            setMicStyle(AudioRoomMicStyle.NORMAL);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
        }
    }

    private void setMicStyle(AudioRoomMicStyle micStyle) {
        if (audioRoomMicStyle == micStyle) {
            return;
        }
        audioRoomMicStyle = micStyle;
        BaseMicView baseMicView;
        switch (micStyle) {
            case NORMAL:
                baseMicView = new AudioRoomMicView(this);
                break;
            case GAME:
                baseMicView = new AudioRoomGameMicView(this);
                break;
            default:
                return;
        }
        micView.setMicView(baseMicView);
    }

    public enum AudioRoomMicStyle {
        NORMAL, // 1+8麦位样式
        GAME    // 游戏样式
    }

}