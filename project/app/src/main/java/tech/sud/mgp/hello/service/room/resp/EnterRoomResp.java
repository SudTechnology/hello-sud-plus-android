package tech.sud.mgp.hello.service.room.resp;

/**
 * 进入房间返回参数
 */
public class EnterRoomResp {
    public long roomId; // 房间Id
    public String roomNumber; // 房间号
    public String roomName; // 房间名称
    public long gameId; // 游戏id
    public int gameLevel; // 游戏级别(为空则说明没有游戏级别)
    public int sceneType; //（1：语聊房场景 2：1v1场景 3：才艺房场景 4：秀场场景 5：门票场景 6：竞猜 7：跨房PK 8：点单 9：语音识别 10：联赛 11：自定义场景 ）
    public int roleType; // 1:房主 0：普通用户
    public String rtcToken; // 推拉流token
    public String rtiToken; // 信令 token
    public String imToken;  // IM token
    public RoomPkModel pkResultVO; // PK 结果
    public AuthRoomInfo extraRoomVO; // 跨域房间信息
    public String streamId; // 流id(弹幕游戏用)
}