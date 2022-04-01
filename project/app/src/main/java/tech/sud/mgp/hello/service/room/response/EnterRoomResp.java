package tech.sud.mgp.hello.service.room.response;

/**
 * 进入房间返回参数
 */
public class EnterRoomResp {
    public long roomId;
    public String roomName;
    public long memberCount;
    public long gameId;
    public int roleType; // 1:房主 0：普通用户
    public String rtcToken; // 推拉流token
    public String rtiToken; // im token
}
