package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 上麦信令
 */
public class RoomCmdUpMicModel extends RoomCmdBaseModel {
    public RoomCmdUpMicModel(UserInfo sendUser) {
        super(RoomCmd.CMD_UP_MIC_NOTIFY, sendUser);
    }

    public int micIndex;
    public String streamID;
    public int roleType;

    public static RoomCmdUpMicModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdUpMicModel.class);
    }

}
