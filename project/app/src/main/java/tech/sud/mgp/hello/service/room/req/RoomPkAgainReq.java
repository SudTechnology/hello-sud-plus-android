package tech.sud.mgp.hello.service.room.req;

/**
 * pk再来一局 请求参数
 */
public class RoomPkAgainReq {
    public long srcRoomId; // PK发起房间id
    public long destRoomId; // PK受邀房间id
    public int minute; // 一轮pk的时长(分钟)
}
