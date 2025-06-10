package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 公屏消息信令V2
 */
public class RoomCmdChatMediaModel extends RoomCmdBaseModel {
    public RoomCmdChatMediaModel(UserInfo sendUser) {
        super(RoomCmd.CMD_CHAT_MEDIA_NOTIFY, sendUser);
    }

    public static final int MSG_TYPE_INACTIVE_TYPE = 1; // 为不可播放的语音消息

    public int msgType; // 1为不可播放的语音消息
    public String content; // 消息内容

    public static RoomCmdChatMediaModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdChatMediaModel.class);
    }
}
