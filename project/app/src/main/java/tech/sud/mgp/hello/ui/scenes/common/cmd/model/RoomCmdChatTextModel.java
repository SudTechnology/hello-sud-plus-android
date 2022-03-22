package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;

/**
 * 公屏消息信令
 */
public class RoomCmdChatTextModel extends RoomCmdBaseModel {
    public RoomCmdChatTextModel(UserInfo sendUser) {
        super(RoomCmd.CMD_CHAT_TEXT_NOTIFY, sendUser);
    }

    public String content; // 消息内容

    public static RoomCmdChatTextModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdChatTextModel.class);
    }
}
