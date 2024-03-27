package tech.sud.mgp.hello.ui.scenes.audio.activity;

import androidx.fragment.app.FragmentTransaction;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.ui.main.base.constant.GameIdCons;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;
import tech.sud.mgp.hello.ui.scenes.webgame.WebGameFragment;

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