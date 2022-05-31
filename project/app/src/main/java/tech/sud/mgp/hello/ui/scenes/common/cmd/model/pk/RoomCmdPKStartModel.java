package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.hello.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 开始跨房PK
 */
public class RoomCmdPKStartModel extends RoomCmdBaseModel {

    public int minuteDuration; // 持续时间；单位：分钟
    public String pkId; // pkId

    public RoomCmdPKStartModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_START, sendUser);
    }

    public static RoomCmdPKStartModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKStartModel.class);
    }

}
