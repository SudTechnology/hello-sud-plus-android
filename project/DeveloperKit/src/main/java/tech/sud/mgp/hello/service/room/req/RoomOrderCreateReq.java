package tech.sud.mgp.hello.service.room.req;

import java.util.List;

/**
 * 用户点单
 */
public class RoomOrderCreateReq {
    public long roomId; // 房间id
    public List<Long> userIdList; // 受邀主播用户id
    public long gameId; // 游戏id
}
