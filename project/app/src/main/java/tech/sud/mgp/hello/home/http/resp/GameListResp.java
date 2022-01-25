package tech.sud.mgp.hello.home.http.resp;

import java.util.List;

import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.SceneModel;

public class GameListResp {
    private List<GameModel> gameList;
    private List<SceneModel> sceneList;

    public List<GameModel> getGameList() {
        return gameList;
    }

    public void setGameList(List<GameModel> gameList) {
        this.gameList = gameList;
    }

    public List<SceneModel> getSceneList() {
        return sceneList;
    }

    public void setSceneList(List<SceneModel> sceneList) {
        this.sceneList = sceneList;
    }
}
