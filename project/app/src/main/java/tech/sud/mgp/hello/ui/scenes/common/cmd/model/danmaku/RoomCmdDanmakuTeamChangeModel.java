package tech.sud.mgp.hello.ui.scenes.common.cmd.model.danmaku;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 弹幕游戏战队变更通知
 */
public class RoomCmdDanmakuTeamChangeModel extends RoomCmdBaseModel {

    public Content content;

    public RoomCmdDanmakuTeamChangeModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_BULLET_MATCH_NOTIFY, sendUser);
    }

    public static RoomCmdDanmakuTeamChangeModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdDanmakuTeamChangeModel.class);
    }

    public static class Content {
        public long userId;
        public String content;
    }
}
