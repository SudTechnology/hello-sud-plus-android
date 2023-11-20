package tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

public class Audio3DCmdMicChangModel extends RoomCmdBaseModel {

    public SudMGPAPPState.AppCustomCrSetSeats content;

    public Audio3DCmdMicChangModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_3D_MIC_STATE_CHANGE_NOTIFY, sendUser);
    }

    public static Audio3DCmdMicChangModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, Audio3DCmdMicChangModel.class);
    }

}
