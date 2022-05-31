package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import java.util.List;

import tech.sud.mgp.hello.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房pk游戏结算消息通知
 */
public class RoomCmdPKSettleModel extends RoomCmdBaseModel {

    public Content content;

    public RoomCmdPKSettleModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_SETTLE, sendUser);
    }

    public static RoomCmdPKSettleModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKSettleModel.class);
    }

    public static class Content {
        public SettleInfo srcPkGameSettleInfo; // 邀请者的结算
        public SettleInfo destPkGameSettleInfo; // 被邀请者的结算
        public List<RankInfo> userRankInfoList; // 排名
    }

    public static class SettleInfo {
        public long roomId; // 房间id
        public long totalScore; // 总得分
    }

    public static class RankInfo {
        public long userId; // 用户id
        public String nickname; // 昵称
        public long roomId; // 房间id
        public int rank; // 排名，从1开始
        public long winScore; // 赢得的分数
    }

}
