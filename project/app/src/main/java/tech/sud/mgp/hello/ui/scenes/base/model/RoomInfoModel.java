package tech.sud.mgp.hello.ui.scenes.base.model;

import java.io.Serializable;

import tech.sud.mgp.hello.service.room.resp.AuthRoomInfo;
import tech.sud.mgp.hello.service.room.resp.RoomPkModel;

/**
 * 房间信息模型
 */
public class RoomInfoModel implements Serializable {
    public int sceneType; // 场景类型
    public Long roomId; // 房间id
    public String roomNumber; // 房间号
    public String roomName; // 房间名称
    public long gameId; // 当前加载的游戏id
    public int roleType; // 1:房主 0：普通用户
    public String rtcToken; // 推拉流token
    public String rtiToken; // 信令 token
    public String imToken;  // IM token
    public Integer gameLevel; // 门票场景游戏级别 从1开始
    public RoomPkModel roomPkModel; // PK 结果
    public String streamId; // 流id(弹幕游戏用)
    public AuthRoomInfo authRoomInfo; // 跨域房间信息

    public long initGameId; // 用于记录初始的游戏id
}
