package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 发送跨房PK邀请
 */
public class RoomCmdPKSendInviteModel extends RoomCmdBaseModel {

    public RoomCmdPKSendInviteModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_SEND_INVITE, sendUser);
    }

    public static RoomCmdPKSendInviteModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdPKSendInviteModel.class);
    }

}
