package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 用户进入房间通知
 */
public class RoomCmdEnterRoomModel extends RoomCmdBaseModel {
    public RoomCmdEnterRoomModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ENTER_ROOM_NOTIFY, sendUser);
    }

    public static RoomCmdEnterRoomModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdEnterRoomModel.class);
    }

}
