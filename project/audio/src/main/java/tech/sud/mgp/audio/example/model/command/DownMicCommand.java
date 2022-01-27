package tech.sud.mgp.audio.example.model.command;

/**
 * 下麦信令
 */
public class DownMicCommand extends BaseCommand {
    public DownMicCommand(SendUser sendUser) {
        super(CommandCmd.CMD_DOWN_MIC_NTF, sendUser);
    }

    public int micIndex;
}
