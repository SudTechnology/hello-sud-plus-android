package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.ui.scenes.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.audio.utils.HSJsonUtils;

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
        return HSJsonUtils.fromJson(json, RoomCmdUpMicModel.class);
    }
    
}
