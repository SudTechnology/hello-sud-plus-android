package tech.sud.mgp.audio.example.model.command;

/**
 * 上麦信令
 */
public class UpMicCommand extends BaseCommand {
    public UpMicCommand(SendUser sendUser) {
        super(CommandCmd.CMD_UP_MIC_NTF, sendUser);
    }

    public int micIndex;
}
