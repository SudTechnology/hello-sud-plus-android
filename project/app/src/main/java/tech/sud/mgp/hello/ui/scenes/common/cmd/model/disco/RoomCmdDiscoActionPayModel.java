package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 蹦迪动作付费
 */
public class RoomCmdDiscoActionPayModel extends RoomCmdBaseModel {

    public int price; // 价格

    public RoomCmdDiscoActionPayModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_DISCO_ACTION_PAY, sendUser);
    }

    public static RoomCmdDiscoActionPayModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdDiscoActionPayModel.class);
    }
}
