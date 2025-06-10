package tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 响应蹦迪信息
 */
public class RoomCmdDiscoInfoRespModel extends RoomCmdBaseModel {

    public RoomCmdDiscoInfoRespModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ROOM_DISCO_INFO_RESP, sendUser);
    }

    public List<DanceModel> dancingMenu; // 跳舞节目单
    public List<ContributionModel> contribution; // 贡献排行
    public boolean isEnd; // 是否是最后一条消息

    public static RoomCmdDiscoInfoRespModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdDiscoInfoRespModel.class);
    }
}
