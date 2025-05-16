package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;

/**
 * 机器人列表 返回参数
 */
public class RobotListResp {
    public List<SudGIPAPPState.AIPlayers> robotList; // 机器人列表
}
