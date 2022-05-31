package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.hello.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房PK，切换游戏
 */
public class RoomCmdPKChangeGameModel extends RoomCmdBaseModel {

    public long gameID; // 游戏id

    public RoomCmdPKChangeGameModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_CHANGE_GAME, sendUser);
    }

    public static RoomCmdPKChangeGameModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKChangeGameModel.class);
    }

}
