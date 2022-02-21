package tech.sud.mgp.hello.service.room.response;

public class EnterRoomResp {
    public long roomId;
    public String roomName;
    public long memberCount;
    public long gameId;
    public int roleType; // 1:房主 0：普通用户
}
