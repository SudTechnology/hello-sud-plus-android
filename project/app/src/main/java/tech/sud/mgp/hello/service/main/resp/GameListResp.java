package tech.sud.mgp.hello.service.main.resp;

import java.util.ArrayList;
import java.util.List;

public class GameListResp {
    public List<GameModel> gameList;
    public List<SceneModel> sceneList;

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

    /**
     * 获取该场景下的游戏列表
     *
     * @param sceneType 场景类型
     */
    public List<GameModel> getGameList(int sceneType) {
        if (gameList == null || gameList.size() == 0) {
            return null;
        }
        List<GameModel> list = new ArrayList<>();
        for (int i = 0; i < gameList.size(); i++) {
            GameModel model = gameList.get(i);
            if (model.suitScene.contains(sceneType)) {
                list.add(model);
            }
        }
        return list;
    }

}
