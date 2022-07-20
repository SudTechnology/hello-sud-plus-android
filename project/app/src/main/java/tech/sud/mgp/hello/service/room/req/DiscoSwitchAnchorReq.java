package tech.sud.mgp.hello.service.room.req;

/**
 * 上/下主播位 请求参数
 */
public class DiscoSwitchAnchorReq {
    public long roomId; // 房间Id
    public int handleType; // 1上，2下
    public long userId; // 用户Id
}
