package tech.sud.mgp.audio.example.model.command;

/**
 * 信令消息的其类
 */
public class BaseCommand {
    public int cmd;
    public SendUser sendUser;

    public BaseCommand(int cmd, SendUser sendUser) {
        this.cmd = cmd;
        this.sendUser = sendUser;
    }
}
