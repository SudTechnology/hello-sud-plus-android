package tech.sud.mgp.hello.ui.scenes.base.model;

import java.io.Serializable;

import tech.sud.mgp.hello.service.room.resp.CrossAppModel;
import tech.sud.mgp.hello.ui.main.base.constant.SceneType;

/**
 * 进房请求时的参数
 */
public class EnterRoomParams implements Serializable {
    public long roomId; // 房间id
    public int sceneType = SceneType.UNDEFINED; // 场景类型
    public Integer gameLevel; // 游戏等级，例如门票场景

    public CrossAppModel crossAppModel; // 跨房匹配信息
}
