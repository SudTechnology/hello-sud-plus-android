package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 踢出房间
 */
public class RoomCmdKickOutRoomModel extends RoomCmdBaseModel {

    public String userID; // 被踢出的用户

    public RoomCmdKickOutRoomModel(UserInfo sendUser) {
        super(RoomCmd.CMD_KICK_OUT_ROOM, sendUser);
    }

    public static RoomCmdKickOutRoomModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdKickOutRoomModel.class);
    }

}
