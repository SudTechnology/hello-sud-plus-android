package tech.sud.mgp.hello.service.room.req;

public class Audio3DLockMicReq {
    public long roomId; // 房间ID
    public int micIndex; // 麦位index
    public int handleType; // 锁麦或解麦(0：锁麦，1：解麦)
}
