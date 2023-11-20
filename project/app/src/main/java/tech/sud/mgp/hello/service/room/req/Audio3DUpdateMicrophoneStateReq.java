package tech.sud.mgp.hello.service.room.req;

public class Audio3DUpdateMicrophoneStateReq {
    public long roomId; // 房间ID
    public long userId; // 用户id
    public int micphoneState; // 麦克风状态  -1:禁麦  0:闭麦  1:开麦
}
