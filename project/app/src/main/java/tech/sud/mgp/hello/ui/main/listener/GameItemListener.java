package tech.sud.mgp.hello.ui.main.listener;

import tech.sud.mgp.hello.ui.main.http.resp.GameModel;
import tech.sud.mgp.hello.ui.main.http.resp.SceneModel;

public interface GameItemListener {
    void onGameClick(SceneModel sceneModel, GameModel gameModel);
}
