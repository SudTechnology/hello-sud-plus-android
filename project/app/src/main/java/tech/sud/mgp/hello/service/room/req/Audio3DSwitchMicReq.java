package tech.sud.mgp.hello.service.room.req;

public class Audio3DSwitchMicReq {
    public long roomId; // 房间ID
    public long userId; // 用户id
    public int micIndex; // 麦位index
    public int handleType; // 上麦或下麦(0:上麦，1：下麦)
}
