package tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨域匹配游戏切换通知
 */
public class CrossAppCmdGameSwitchModel extends RoomCmdBaseModel {

    public Content content;

    public CrossAppCmdGameSwitchModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_EXTRA_GAME_SWITCH_NOTIFY, sendUser);
    }

    public static CrossAppCmdGameSwitchModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, CrossAppCmdGameSwitchModel.class);
    }

    public static class Content {
        public long gameId; // 游戏id
        public List<String> userList; // 组队用户列表
    }
}
