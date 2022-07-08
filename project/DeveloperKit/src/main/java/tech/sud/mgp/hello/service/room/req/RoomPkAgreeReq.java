package tech.sud.mgp.hello.service.room.req;

/**
 * 同意跨房PK 请求参数
 */
public class RoomPkAgreeReq {
    public long srcRoomId; //  PK发起房间id
    public long destRoomId; //  PK受邀房间id
}