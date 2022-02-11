package tech.sud.mgp.hello.home.manager;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.common.http.use.resp.GameListResp;
import tech.sud.mgp.common.http.use.resp.GameModel;
import tech.sud.mgp.common.http.use.resp.SceneModel;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;

public class HomeManager {

    private static HomeManager homeManager;

    private GameListResp gameListResp;
    private RoomListResp roomListResp;

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

    public void updateGameList(GameListResp gameListResp) {
        this.gameListResp = gameListResp;
    }

    public void updateRoomList(RoomListResp roomListResp) {
        this.roomListResp = roomListResp;
    }

    /**
     * 获取场景名字
     * sceneType:场景id
     */
    public String sceneName(int sceneType) {
        if (gameListResp != null && gameListResp.getSceneList() != null && gameListResp.getSceneList().size() > 0) {
            for (int i = 0; i < gameListResp.getSceneList().size(); i++) {
                SceneModel sceneModel = gameListResp.getSceneList().get(i);
                if (sceneModel.getSceneId() == sceneType) {
                    String name = sceneModel.getSceneName();
                    if (name.contains("场景")) {
                        return name.replace("场景", "");
                    }
                    return name;
                }
            }
        }
        return sceneType + "";
    }

    /**
     * 获取场景下可用的游戏
     */
    public List<GameModel> getSceneGame(SceneModel model) {
        List<GameModel> gameModels = new ArrayList<>();
        if (gameListResp != null && gameListResp.getGameList() != null && gameListResp.getGameList().size() > 0) {
            for (int i = 0; i < gameListResp.getGameList().size(); i++) {
                GameModel game = gameListResp.getGameList().get(i);
                List<Integer> suitScene = game.getSuitScene();
                if (suitScene.contains(model.getSceneId())) {
                    gameModels.add(game);
                }
            }
        }
        return gameModels;
    }
}
