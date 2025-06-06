package tech.sud.mgp.hello.ui.scenes.common.cmd.model.pk;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨房PK邀请应答
 */
public class RoomCmdPKAnswerModel extends RoomCmdBaseModel {

    public boolean isAccept; // 是否接受
    public UserInfo otherUser; // 邀请方
    public String pkId; // pkId

    public RoomCmdPKAnswerModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_PK_ANSWER, sendUser);
    }

    public static RoomCmdPKAnswerModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdPKAnswerModel.class);
    }

}
