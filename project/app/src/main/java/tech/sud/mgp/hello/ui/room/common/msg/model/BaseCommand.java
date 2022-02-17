package tech.sud.mgp.hello.ui.room.common.msg.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;

/**
 * 信令消息的其类
 */
public class BaseCommand {
    public int cmd;
    public UserInfo sendUser;

    public BaseCommand(int cmd, UserInfo sendUser) {
        this.cmd = cmd;
        this.sendUser = sendUser;
    }
}
