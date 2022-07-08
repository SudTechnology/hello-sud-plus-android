package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 结束跨房PK
 */
public class RoomCmdPKFinishModel extends RoomCmdBaseModel {

    public RoomCmdPKFinishModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_FINISH, sendUser);
    }

    public static RoomCmdPKFinishModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKFinishModel.class);
    }

}
