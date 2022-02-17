package tech.sud.mgp.hello.ui.room.common.cmd.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;
import tech.sud.mgp.hello.ui.room.audio.utils.HSJsonUtils;

/**
 * 游戏切换信令
 */
public class RoomCmdChangeGameModel extends RoomCmdBaseModel {

    public RoomCmdChangeGameModel(UserInfo sendUser) {
        super(RoomCmd.CMD_CHANGE_GAME_NOTIFY, sendUser);
    }

    public long gameID; // 游戏id，如果为0，则表示没有游戏

    public static RoomCmdChangeGameModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdChangeGameModel.class);
    }

}
