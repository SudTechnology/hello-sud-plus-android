package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 发送跨房PK邀请
 */
public class RoomCmdPKSendInviteModel extends RoomCmdBaseModel {

    public int minuteDuration; // 持续时间；单位：分钟

    public RoomCmdPKSendInviteModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_SEND_INVITE, sendUser);
    }

    public static RoomCmdPKSendInviteModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKSendInviteModel.class);
    }

}
