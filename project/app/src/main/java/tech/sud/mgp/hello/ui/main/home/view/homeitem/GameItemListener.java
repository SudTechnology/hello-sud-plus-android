package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 点击了某个游戏的监听
 */
public interface GameItemListener {
    void onGameClick(SceneModel sceneModel, GameModel gameModel);
}
