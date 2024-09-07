package tech.sud.mgp.hello.ui.scenes.audio.activity;

import androidx.fragment.app.FragmentTransaction;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomGameMicView;
import tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic.AudioRoomMicView;
import tech.sud.mgp.hello.ui.scenes.base.interaction.base.activity.BaseInteractionRoomActivity;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.chat.SceneRoomChatView;
import tech.sud.mgp.hello.ui.scenes.base.widget.view.mic.BaseMicView;
import tech.sud.mgp.hello.ui.scenes.webgame.WebGameFragment;

/**
 * 语聊房页面抽象类
 */
public abstract class AbsAudioRoomActivity<T extends AppGameViewModel> extends BaseInteractionRoomActivity<T> {

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

    public enum AudioRoomMicStyle {
        NORMAL, // 1+8麦位样式
        GAME    // 游戏样式
    }

    @Override
    protected void callSudSwitchGame(String gameRoomId, long gameId, int loadMGMode, String authorizationSecret) {
//        super.callSudSwitchGame(gameRoomId, gameId, loadMGMode, authorizationSecret);
        if (gameId == 0) {
            switchInteractionGame(gameId);
            gameViewModel.switchGame(this, getGameRoomId(), gameId, getLoadMGMode(), getAuthorizationSecret());
            destroyWebGame();
            return;
        }

        GameModel gameModel = viewModel.getGameModel(gameId);
        if (gameModel == null) { // 暂不考虑异常获取不到的情况
            return;
        }
        switch (gameModel.loadType) {
            case GameModel.LOAD_TYPE_SDK:
            case GameModel.LOAD_TYPE_REYOU_SDK:
                if (GameIdCons.isInteractionGame(gameId)) {
                    switchInteractionGame(gameId);
                    gameViewModel.switchGame(this, getGameRoomId(), 0, getLoadMGMode(), getAuthorizationSecret(), gameModel.loadType);
                } else {
                    switchInteractionGame(0);
                    gameViewModel.switchGame(this, getGameRoomId(), gameId, getLoadMGMode(), getAuthorizationSecret(), gameModel.loadType);
                }
                destroyWebGame();
                break;
            case GameModel.LOAD_TYPE_H5:
                switchInteractionGame(0);
                gameViewModel.switchGame(this, getGameRoomId(), 0, getLoadMGMode(), getAuthorizationSecret());
                loadWebGame(gameModel);
                break;
            case GameModel.LOAD_TYPE_RTMP:
                break;
        }
    }

    private void destroyWebGame() {
        webGameContainer.removeAllViews();
    }

    private void loadWebGame(GameModel gameModel) {
        WebGameFragment webGameFragment = WebGameFragment.newInstance(gameModel);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(webGameContainer.getId(), webGameFragment);
        transaction.commit();
    }

}