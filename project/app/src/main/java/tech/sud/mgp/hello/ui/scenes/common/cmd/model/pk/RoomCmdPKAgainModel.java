package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 再来一轮PK
 */
public class RoomCmdPKAgainModel extends RoomCmdBaseModel {

    public UserInfo srcUser; // 邀请者信息
    public UserInfo destUser; // 被邀请者信息
    public String pkId; // pkId

    public RoomCmdPKAgainModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_AGAIN, sendUser);
    }

    public static RoomCmdPKAgainModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdPKAgainModel.class);
    }

}
