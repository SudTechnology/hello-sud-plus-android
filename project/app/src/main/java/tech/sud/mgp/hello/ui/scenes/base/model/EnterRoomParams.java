package tech.sud.mgp.hello.ui.scenes.base.model;

import tech.sud.mgp.hello.ui.main.constant.SceneType;

/**
 * 进房请求时的参数
 */
public class EnterRoomParams {
    public long roomId; // 房间id
    public int sceneType = SceneType.UNDEFINED; // 场景类型
    public Integer gameLevel; // 游戏等级，例如门票场景
}
