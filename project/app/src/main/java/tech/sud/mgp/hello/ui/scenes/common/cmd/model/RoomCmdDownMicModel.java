package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.ui.scenes.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.audio.utils.HSJsonUtils;

/**
 * 下麦信令
 */
public class RoomCmdDownMicModel extends RoomCmdBaseModel {
    public RoomCmdDownMicModel(UserInfo sendUser) {
        super(RoomCmd.CMD_DOWN_MIC_NOTIFY, sendUser);
    }

    public int micIndex;

    public static RoomCmdDownMicModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdDownMicModel.class);
    }

}
