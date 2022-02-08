package tech.sud.mgp.hello.home.callback;

import tech.sud.mgp.common.http.use.resp.GameModel;
import tech.sud.mgp.common.http.use.resp.SceneModel;

public interface GameItemCallback {
    void gameClick(SceneModel sceneModel, GameModel gameModel);
}
