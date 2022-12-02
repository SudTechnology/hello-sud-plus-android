package tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨域匹配人数变更通知
 */
public class CrossAppCmdUsersChangeModel extends RoomCmdBaseModel {

    public String groupId; // 匹配组id
    public int curNum; // 已匹配人数
    public int totalNum; // 需匹配总人数
    public List<String> userIds; // 已匹配的用户id（其他App内用户id不在此列表中）

    public CrossAppCmdUsersChangeModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_EXTRA_MATCH_USERS_CHANGED_NOTIFY, sendUser);
    }

    public static CrossAppCmdUsersChangeModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, CrossAppCmdUsersChangeModel.class);
    }

}
