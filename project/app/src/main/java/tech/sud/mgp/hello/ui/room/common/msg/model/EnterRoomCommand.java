package tech.sud.mgp.hello.ui.room.common.msg.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;

/**
 * 用户进入房间通知
 */
public class EnterRoomCommand extends BaseCommand {
    public EnterRoomCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_ENTER_ROOM_NTF, sendUser);
    }
}
