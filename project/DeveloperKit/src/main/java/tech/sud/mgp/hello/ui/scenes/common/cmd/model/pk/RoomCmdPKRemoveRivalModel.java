package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房PK，移除对手
 */
public class RoomCmdPKRemoveRivalModel extends RoomCmdBaseModel {

    public RoomCmdPKRemoveRivalModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_REMOVE_RIVAL, sendUser);
    }

    public static RoomCmdPKRemoveRivalModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKRemoveRivalModel.class);
    }

}
