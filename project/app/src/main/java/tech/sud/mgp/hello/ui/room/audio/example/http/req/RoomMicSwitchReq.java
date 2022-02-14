package tech.sud.mgp.hello.ui.room.audio.example.http.req;

public class RoomMicSwitchReq {
    public long roomId;// 房间ID
    public int micIndex;// 麦位索引
    public int handleType;// 0：上麦 1：下麦
}
