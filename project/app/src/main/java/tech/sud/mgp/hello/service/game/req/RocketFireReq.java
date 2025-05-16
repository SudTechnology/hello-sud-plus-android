package tech.sud.mgp.hello.service.game.req;

import java.util.List;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;

/**
 * 发射火箭 请求参数
 */
public class RocketFireReq {
    public long roomId; // 房间Id
    public List<Long> receiverList; // 收礼人id
    public long number; // 个数
    public List<SudGIPMGState.MGCustomRocketFireModel.ComponentModel> componentList; // 组件列表
}