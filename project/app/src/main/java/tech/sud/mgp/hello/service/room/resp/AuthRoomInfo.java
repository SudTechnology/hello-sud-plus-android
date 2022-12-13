package tech.sud.mgp.hello.service.room.resp;

import java.io.Serializable;

/**
 * 跨域房间信息
 */
public class AuthRoomInfo implements Serializable {
    public String authSecret; // 授权码
    public String roomId; // 房间id
}
