package tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨域匹配队伍变更通知
 */
public class CrossAppCmdTeamChangeModel extends RoomCmdBaseModel {

    public long captain; // 队长id
    public List<UserInfoResp> userList; // 组队用户列表

    public CrossAppCmdTeamChangeModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_EXTRA_TEAM_CHANGED_NOTIFY, sendUser);
    }

    public static CrossAppCmdTeamChangeModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, CrossAppCmdTeamChangeModel.class);
    }

}
