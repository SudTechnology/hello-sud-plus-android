package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

/**
 * 查询竞猜场景游戏玩家列表（房间内） 返回参数
 */
public class QuizGamePlayerResp {
    public List<Player> player;

    public static class Player{
        public long userId; // 用户id
        public String header; // 头像
        public boolean supported; // 是否已支持（true: 已支持， false: 未支持）
    }

}
