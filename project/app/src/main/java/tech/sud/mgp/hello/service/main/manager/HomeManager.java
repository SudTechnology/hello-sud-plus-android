package tech.sud.mgp.hello.service.main.manager;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;

public class HomeManager {

    private static HomeManager homeManager;

    public GameListResp gameListResp;
    public RoomListResp roomListResp;

    private HomeManager() {
    }

    public static HomeManager getInstance() {
        if (homeManager == null) {
            synchronized (HomeManager.class) {
                if (homeManager == null) {
                    homeManager = new HomeManager();
                }
            }
        }
        return homeManager;
    }

    /**
     * 获取场景下可用的游戏
     */
    public List<GameModel> getSceneGame(int sceneId) {
        if (gameListResp == null) {
            return null;
        }
        return gameListResp.getGameList(sceneId);
    }

    /**
     * 敬请期待数据
     */
    public List<GameModel> getSceneEmptyGame(Context context) {
        List<GameModel> gameModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GameModel game = new GameModel();
            game.gameId = -1;
            game.gameName = context.getString(R.string.coming_soon);
            game.homeGamePic = "";
            gameModels.add(game);
        }
        return gameModels;
    }

    /**
     * 根据游戏id获取gameModel
     */
    public GameModel getGameModel(long gameId) {
        GameListResp resp = gameListResp;
        if (resp != null && resp.gameList != null) {
            for (GameModel gameModel : resp.gameList) {
                if (gameModel.gameId == gameId) {
                    return gameModel;
                }
            }
        }
        return null;
    }

}
