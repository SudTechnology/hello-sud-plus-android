package tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

public class Audio3DCmdConfigChangModel extends RoomCmdBaseModel {

    public SudGIPAPPState.AppCustomCrSetRoomConfig content;

    public Audio3DCmdConfigChangModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_3D_CONFIG_CHANGE_NOTIFY, sendUser);
    }

    public static Audio3DCmdConfigChangModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, Audio3DCmdConfigChangModel.class);
    }

}
