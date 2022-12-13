package tech.sud.mgp.hello.service.main.req;

/**
 * 跨域匹配房间 请求参数
 */
public class AuthMatchRoomReq {
    public String authSecret; // app授权码
    public String roomId; // 房间id，对方app房间id
}
