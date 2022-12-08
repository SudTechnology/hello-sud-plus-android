package tech.sud.mgp.hello.ui.scenes.common.cmd.model.crossapp;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.service.room.model.CrossAppMatchStatus;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 跨域匹配状态变更通知
 */
public class CrossAppCmdStatusChangeModel extends RoomCmdBaseModel {

    public Content content;

    public CrossAppCmdStatusChangeModel(UserInfo sendUser) {
        super(RoomCmd.CMD_GAME_EXTRA_MATCH_STATUS_CHANGED_NOTIFY, sendUser);
    }

    public static CrossAppCmdStatusChangeModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, CrossAppCmdStatusChangeModel.class);
    }

    public static class Content {
        public String groupId; // 匹配组id
        public String matchRoomId; // 匹配房间id(用于后续加载游戏)，匹配状态为成功时该字段才有值
        /** 匹配状态{@link CrossAppMatchStatus} */
        public int matchStatus;
    }

}
