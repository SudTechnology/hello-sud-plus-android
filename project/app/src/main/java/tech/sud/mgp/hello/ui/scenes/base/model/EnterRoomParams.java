package tech.sud.mgp.hello.ui.scenes.base.model;

import tech.sud.mgp.hello.ui.main.constant.SceneType;

/**
 * 进房请求时的参数
 */
public class EnterRoomParams {
    public long roomId;
    public int sceneType = SceneType.UNDEFINED;
    public Integer gameLevel;
}
