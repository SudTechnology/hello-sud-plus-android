package tech.sud.mgp.hello.ui.room.audio.example.http.response;

public class RoomMicResp {
    public long userId;
    public int micIndex;
    public int roleType; // --1:房主 0：普通用户
    public String streamId;
}
