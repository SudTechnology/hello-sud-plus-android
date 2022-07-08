package tech.sud.mgp.hello.ui.main.home.view.homeitem;

import tech.sud.mgp.hello.service.main.resp.GameModel;
import tech.sud.mgp.hello.service.main.resp.SceneModel;

/**
 * 点击了创建房间
 */
public interface CreatRoomClickListener {
    void onCreateRoomClick(SceneModel sceneModel, GameModel gameModel);
}
