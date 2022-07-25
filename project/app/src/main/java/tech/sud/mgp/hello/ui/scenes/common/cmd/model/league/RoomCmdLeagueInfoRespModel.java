package tech.sud.mgp.hello.ui.scenes.common.cmd.model.league;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 响应联赛信息
 */
public class RoomCmdLeagueInfoRespModel extends RoomCmdBaseModel {

    public int schedule; // 已经进行了几局比赛，0开始
    public List<String> winner = new ArrayList<>(); // 进行比赛之后的胜出者

    public RoomCmdLeagueInfoRespModel(UserInfo sendUser) {
        super(RoomCmd.CMD_LEAGUE_INFO_RESP, sendUser);
    }

    public static RoomCmdLeagueInfoRespModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdLeagueInfoRespModel.class);
    }

}
