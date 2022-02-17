package tech.sud.mgp.hello.ui.room.common.cmd.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.room.audio.utils.HSJsonUtils;

/**
 * 用户进入房间通知
 */
public class RoomCmdEnterRoomModel extends RoomCmdBaseModel {
    public RoomCmdEnterRoomModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ENTER_ROOM_NOTIFY, sendUser);
    }

    public static RoomCmdEnterRoomModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdEnterRoomModel.class);
    }

}
