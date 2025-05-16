package tech.sud.mgp.hello.ui.scenes.common.cmd.model.audio3d;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

public class Audio3DCmdFaceNotifyModel extends RoomCmdBaseModel {

    public int type; // 操作类型 0:表情, 1:爆灯
    public int faceId; // 表情ID
    public int seatIndex; // 位置索引

    public Audio3DCmdFaceNotifyModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_3D_SEND_FACE_NOTIFY, sendUser);
    }

    public static Audio3DCmdFaceNotifyModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, Audio3DCmdFaceNotifyModel.class);
    }

}
