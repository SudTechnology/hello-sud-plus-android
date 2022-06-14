package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

/**
 * 查询竞猜场景游戏玩家列表（房间内） 返回参数
 */
public class QuizGamePlayerResp {
    public List<Player> playerList;

    public static class Player {
        public long userId; // 用户id
        public String header; // 头像
        public String nickname; // 昵称
        public boolean support; // 是否已支持（true: 已支持， false: 未支持）
        public int supportedUserCount; // 支持的人数

        // app自定义添加的
        public boolean isSelected;
    }

}
