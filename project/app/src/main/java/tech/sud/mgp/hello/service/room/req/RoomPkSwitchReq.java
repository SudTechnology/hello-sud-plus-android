package tech.sud.mgp.hello.service.room.req;

/**
 * 设置跨房pk开关 请求参数
 */
public class RoomPkSwitchReq {
    public long roomId; // 房间id
    public boolean pkSwitch; // true: 开启PK  false:关闭PK
}
