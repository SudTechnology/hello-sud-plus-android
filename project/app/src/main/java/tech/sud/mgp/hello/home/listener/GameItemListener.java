package tech.sud.mgp.hello.home.listener;

import tech.sud.mgp.common.http.use.resp.GameModel;
import tech.sud.mgp.common.http.use.resp.SceneModel;

public interface GameItemListener {
    void onGameClick(SceneModel sceneModel, GameModel gameModel);
}
