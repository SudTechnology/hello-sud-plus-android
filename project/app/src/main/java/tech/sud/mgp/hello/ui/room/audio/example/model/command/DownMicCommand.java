package tech.sud.mgp.hello.ui.room.audio.example.model.command;

import tech.sud.mgp.hello.ui.room.audio.example.model.UserInfo;

/**
 * 下麦信令
 */
public class DownMicCommand extends BaseCommand {
    public DownMicCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_DOWN_MIC_NTF, sendUser);
    }

    public int micIndex;
}