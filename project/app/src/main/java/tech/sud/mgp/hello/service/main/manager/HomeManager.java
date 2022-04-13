package tech.sud.mgp.hello.service.main.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.service.main.resp.SceneModel;
import tech.sud.mgp.hello.ui.main.constant.SceneType;
import tech.sud.mgp.hello.ui.scenes.asr.ASRActivity;
import tech.sud.mgp.hello.ui.scenes.ticket.activity.TicketActivity;

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
     * 获取场景TAG
     * sceneType:场景id
     * 暂时先每个都列出来，后面ui需要修改就替换颜色
     */
    public SceneTagColor sceneTagResId(int sceneType) {
        SceneTagColor color = new SceneTagColor();
        switch (sceneType) {
            case SceneType.ASR:
                color.colorBg = Color.parseColor("#9622C1");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.TICKET:
                color.colorBg = Color.parseColor("#E35017");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.TALENT:
                color.colorBg =  Color.parseColor("#F7268B");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.CROSS_ROOM:
                color.colorBg = Color.parseColor("#504EEB");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.ONE_ONE:
                color.colorBg = Color.parseColor("#1378F1");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.ORDER_ENTERTAINMENT:
                color.colorBg = Color.parseColor("#27B7E8");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.QUIZ:
                color.colorBg = Color.parseColor("#FDAB26");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.SHOW:
                color.colorBg = Color.parseColor("#EC5420");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            case SceneType.AUDIO:
                color.colorBg = Color.parseColor("#8324DF");
                color.colorText = Color.parseColor("#FFFFFF");
                break;
            default:
                color.colorBg = Color.parseColor("#f5f5f5");
                color.colorText = Color.parseColor("#999999");
                break;
        }
        return color;
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
     *
     * @param gameId
     * @return
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

    /**
     * 根据场景id，返回场景信息的model
     */
    public class SceneTagColor {
        public int colorBg;
        public int colorText;
    }

}
