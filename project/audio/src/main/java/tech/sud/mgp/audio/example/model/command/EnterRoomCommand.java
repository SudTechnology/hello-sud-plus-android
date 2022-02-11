package tech.sud.mgp.audio.example.model.command;

import tech.sud.mgp.audio.example.model.UserInfo;

/**
 * 用户进入房间通知
 */
public class EnterRoomCommand extends BaseCommand {
    public EnterRoomCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_ENTER_ROOM_NTF, sendUser);
    }
}
