package tech.sud.mgp.hello.ui.room.audio.example.model.command;

import tech.sud.mgp.hello.ui.room.audio.example.model.UserInfo;

/**
 * 游戏切换信令
 */
public class GameChangeCommand extends BaseCommand {

    public GameChangeCommand(UserInfo sendUser) {
        super(CommandCmd.CMD_GAME_CHANGE, sendUser);
    }

    public long gameID; // 游戏id，如果为0，则表示没有游戏

}
