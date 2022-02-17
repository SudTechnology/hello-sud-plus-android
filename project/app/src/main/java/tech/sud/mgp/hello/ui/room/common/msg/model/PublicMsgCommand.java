package tech.sud.mgp.hello.ui.room.common.msg.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;

/**
 * 公屏消息信令
 */
public class PublicMsgCommand extends BaseCommand {
    public PublicMsgCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_PUBLIC_MSG_NTF, sendUser);
    }

    public String content;
}
