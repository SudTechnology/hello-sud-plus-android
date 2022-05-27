package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房PK对手房间关闭消息
 */
public class RoomCmdPKRivalExitModel extends RoomCmdBaseModel {

    public RoomCmdPKRivalExitModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_RIVAL_EXIT, sendUser);
    }

    public static RoomCmdPKRivalExitModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdPKRivalExitModel.class);
    }
}
