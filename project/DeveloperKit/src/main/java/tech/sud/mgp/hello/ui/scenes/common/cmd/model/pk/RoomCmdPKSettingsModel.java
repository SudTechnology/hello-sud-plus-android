package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房PK设置
 */
public class RoomCmdPKSettingsModel extends RoomCmdBaseModel {

    public int minuteDuration; // 持续时间；单位：分钟

    public RoomCmdPKSettingsModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_SETTINGS, sendUser);
    }

    public static RoomCmdPKSettingsModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKSettingsModel.class);
    }

}
