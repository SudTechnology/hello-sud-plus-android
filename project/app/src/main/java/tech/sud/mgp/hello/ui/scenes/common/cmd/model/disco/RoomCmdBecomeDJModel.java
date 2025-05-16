package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 上DJ台
 */
public class RoomCmdBecomeDJModel extends RoomCmdBaseModel {

    public RoomCmdBecomeDJModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_DISCO_BECOME_DJ, sendUser);
    }

    public String userID; // 上DJ台的用户

    public static RoomCmdBecomeDJModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdBecomeDJModel.class);
    }
}
