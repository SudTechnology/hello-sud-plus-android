package tech.sud.mgp.hello.home.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.RoomItemModel;
import tech.sud.mgp.hello.home.model.SceneModel;

public class HomeManager {

    private static HomeManager homeManager;

    private GameListResp gameListResp;

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

    /**
     * 获取场景名字
     * sceneType:场景id
     */
    public String sceneName(int sceneType) {
        if (gameListResp != null && gameListResp.getSceneList() != null && gameListResp.getSceneList().size() > 0) {
            for (int i = 0; i < gameListResp.getSceneList().size(); i++) {
                SceneModel sceneModel = gameListResp.getSceneList().get(i);
                if (sceneModel.getSceneId() == sceneType) {
                    return sceneModel.getSceneName();
                }
            }
        }
        return sceneType + "";
    }

    /**
     * 模拟游戏列表返回数据
     */
    public GameListResp testGameListResp() {
        GameListResp resp = new GameListResp();
        resp.setGameList(testCreatGame());
        resp.setSceneList(testCreatScene());
        this.gameListResp = resp;
        return resp;
    }

    /**
     * 生成场景列表数据
     */
    public List<SceneModel> testCreatScene() {
        List<SceneModel> datas = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SceneModel model = new SceneModel();
            model.setSceneId(i);
            model.setSceneName("场景" + i);
            datas.add(model);
        }
        return datas;
    }

    /**
     * 生成游戏列表数据
     */
    public List<GameModel> testCreatGame() {
        List<GameModel> lists = new ArrayList<>();
        int total = new Random().nextInt(10) + 1;
        for (int i = 0; i < total; i++) {
            GameModel gameModel = new GameModel();
            gameModel.setGameId(i);
            gameModel.setGameName("Game" + i);
            lists.add(gameModel);
        }
        return lists;
    }

    /**
     * 生成房间列表数据
     */
    public List<RoomItemModel> testCreatRoom() {
        List<RoomItemModel> datas = new ArrayList<>();
        int total = new Random().nextInt(10) + 1;
        for (int i = 0; i < total; i++) {
            RoomItemModel itemModel = new RoomItemModel();
            itemModel.setRoomId(i);
            itemModel.setRoomName("Room" + i);
            itemModel.setMemberCount(i * 2);
            itemModel.setSceneType(new Random().nextInt(3));
            datas.add(itemModel);
        }
        return datas;
    }
}