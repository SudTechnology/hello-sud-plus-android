package tech.sud.mgp.common.http.use.resp;

import java.util.List;


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
