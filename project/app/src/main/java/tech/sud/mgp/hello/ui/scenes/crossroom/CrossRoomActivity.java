package tech.sud.mgp.hello.ui.scenes.crossroom;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.audio.activity.AbsAudioRoomActivity;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;

/**
 * 跨房互动类场景
 */
public class CrossRoomActivity extends BaseRoomActivity<GameViewModel> {

    private AbsAudioRoomActivity.AudioRoomMicStyle audioRoomMicStyle;

    @Override
    protected GameViewModel initGameViewModel() {
        return new GameViewModel();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        roomConfig.isShowGameNumber = false; // 不显示游戏人数
        roomConfig.isShowASRTopHint = false; // 右上角不展示ASR提示
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cross_room;
    }

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (playingGameId > 0) { // 玩着游戏
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.GAME);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle.NORMAL);
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
        }
    }

    private void setMicStyle(AbsAudioRoomActivity.AudioRoomMicStyle micStyle) {
        if (audioRoomMicStyle == micStyle) {
            return;
        }
        audioRoomMicStyle = micStyle;
        BaseMicView<?> baseMicView;
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

}