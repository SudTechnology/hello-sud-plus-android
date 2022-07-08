package tech.sud.mgp.hello.service.room.req;

import java.util.List;

/**
 * 查询竞猜场景游戏玩家列表（房间内） 请求参数
 */
public class QuizGamePlayerReq {
    public long roomId; // 房间id
    public List<Long> playerList; // 玩家列表
}
