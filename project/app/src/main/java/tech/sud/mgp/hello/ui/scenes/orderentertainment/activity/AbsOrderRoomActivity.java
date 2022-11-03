package tech.sud.mgp.hello.ui.scenes.orderentertainment.activity;

import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.activity.BaseRocketRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;

/**
 * 点单房间页面抽象类
 */
public abstract class AbsOrderRoomActivity<T extends AppRocketGameViewModel> extends BaseRocketRoomActivity<T> {

    private volatile int isInit = 0;

    @Override
    protected void updatePageStyle() {
        super.updatePageStyle();
        if (isInit == 0) {
            micView.setMicView(new AudioRoomMicView(this));
            isInit = 1;
        }
        if (playingGameId > 0) { // 玩着游戏
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.GAME);
        } else {
            chatView.setChatStyle(SceneRoomChatView.AudioRoomChatStyle.NORMAL);
        }
    }

    /**
     * 手动控制公屏的样式
     */
    public void switchChatViewStyle(SceneRoomChatView.AudioRoomChatStyle style) {
        chatView.setChatStyle(style);
    }

}