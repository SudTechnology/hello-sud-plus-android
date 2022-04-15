package tech.sud.mgp.hello.service.main.manager;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;

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
    public List<GameModel> getSceneGame(SceneModel model) {
        if (gameListResp == null) {
            return null;
        }
        return gameListResp.getGameList(model.getSceneId());
    }

    /**
     * 敬请期待数据
     */
    public List<GameModel> getSceneEmptyGame(Context context, SceneModel model) {
        List<GameModel> gameModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GameModel game = new GameModel();
            game.setGameId(-1);
            game.setGameName(context.getString(R.string.coming_soon));
            game.setHomeGamePic("");
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
