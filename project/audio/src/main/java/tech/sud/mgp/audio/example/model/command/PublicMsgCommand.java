package tech.sud.mgp.audio.example.model.command;

/**
 * 公屏消息信令
 */
public class PublicMsgCommand extends BaseCommand {
    public PublicMsgCommand(SendUser sendUser) {
        super(CommandCmd.CMD_PUBLIC_MSG_NTF, sendUser);
    }

    public String content;
}
