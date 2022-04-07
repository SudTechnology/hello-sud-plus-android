package tech.sud.mgp.hello.service.room.response;

/**
 * 进入房间返回参数
 */
public class EnterRoomResp {
    public long roomId;
    public String roomName;
    public long memberCount;
    public long gameId;
    public int gameLevel;//游戏级别(为空则说明没有游戏级别)
    public int sceneType;//（1：语聊房场景 2：1v1场景 3：才艺房场景 4：秀场场景 5：门票场景 6：竞猜 7：跨房PK 8：点单 9：语音识别 10：联赛 11：自定义场景 ）
    public int roleType; // 1:房主 0：普通用户
    public String rtcToken; // 推拉流token
    public String rtiToken; // im token
}
