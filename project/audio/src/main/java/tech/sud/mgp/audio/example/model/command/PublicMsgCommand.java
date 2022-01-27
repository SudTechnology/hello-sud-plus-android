package tech.sud.mgp.audio.example.model.command;

import tech.sud.mgp.audio.example.model.UserInfo;

/**
 * 公屏消息信令
 */
public class PublicMsgCommand extends BaseCommand {
    public PublicMsgCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_PUBLIC_MSG_NTF, sendUser);
    }

    public String content;
}
