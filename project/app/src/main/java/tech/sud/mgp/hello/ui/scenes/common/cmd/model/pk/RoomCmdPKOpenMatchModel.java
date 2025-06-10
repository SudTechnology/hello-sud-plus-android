package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 开启匹配跨房PK
 */
public class RoomCmdPKOpenMatchModel extends RoomCmdBaseModel {

    public RoomCmdPKOpenMatchModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_OPEN_MATCH, sendUser);
    }

    public static RoomCmdPKOpenMatchModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKOpenMatchModel.class);
    }

}
