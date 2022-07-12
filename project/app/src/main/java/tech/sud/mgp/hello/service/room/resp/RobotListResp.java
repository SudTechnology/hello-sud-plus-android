package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;

/**
 * 机器人列表 返回参数
 */
public class RobotListResp {
    public List<SudMGPAPPState.AIPlayers> robotList; // 机器人列表
}
