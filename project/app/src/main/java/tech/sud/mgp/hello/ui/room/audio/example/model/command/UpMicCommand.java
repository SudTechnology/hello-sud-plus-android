package tech.sud.mgp.hello.ui.room.audio.example.model.command;

import tech.sud.mgp.hello.ui.room.audio.example.model.UserInfo;

/**
 * 上麦信令
 */
public class UpMicCommand extends BaseCommand {
    public UpMicCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_UP_MIC_NTF, sendUser);
    }

    public int micIndex;
    public String streamID;
    public int roleType;
}