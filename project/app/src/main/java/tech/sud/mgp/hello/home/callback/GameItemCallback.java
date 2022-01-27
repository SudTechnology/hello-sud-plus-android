package tech.sud.mgp.hello.home.callback;

import tech.sud.mgp.hello.home.model.GameModel;
import tech.sud.mgp.hello.home.model.SceneModel;

public interface GameItemCallback {
    void gameClick(SceneModel sceneModel, GameModel gameModel);
}
