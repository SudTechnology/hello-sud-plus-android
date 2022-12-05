package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.mgp.hello.service.main.resp.GameModeModel;
import tech.sud.mgp.hello.service.main.resp.GameModel;

public class GameUtils {

    /** 获取游戏最大可支持人数 */
    public static int getGameMaxNumber(GameModel gameModel) {
        if (gameModel != null && gameModel.gameModeList != null && gameModel.gameModeList.size() > 0) {
            GameModeModel gameModeModel = gameModel.gameModeList.get(0);
            if (gameModeModel.mode != 1) {
                for (GameModeModel model : gameModel.gameModeList) {
                    if (model.mode == 1) {
                        gameModeModel = model;
                    }
                }
            }
            if (gameModeModel.count.length >= 2) {
                return gameModeModel.count[1];
            }
        }
        return 0;
    }

}
