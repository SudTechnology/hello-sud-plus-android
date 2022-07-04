package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 请求蹦迪信息
 */
public class RoomCmdDiscoInfoReqModel extends RoomCmdBaseModel {
    public RoomCmdDiscoInfoReqModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_DISCO_INFO_REQ, sendUser);
    }

    public static RoomCmdDiscoInfoReqModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdDiscoInfoReqModel.class);
    }
}
