package tech.sud.mgp.hello.ui.room.audio.example.http.response;

public class EnterRoomResp {
    public long roomId;
    public String roomName;
    public long memberCount;
    public long gameId;
    public int roleType; // 1:房主 0：普通用户
}
