package tech.sud.mgp.hello.service.game.req;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;

/**
 * 发射火箭 请求参数
 */
public class RocketFireReq {
    public long roomId; // 房间Id
    public List<String> receiver; // 收礼人id
    public long number; // 个数
    public List<SudMGPMGState.MGCustomRocketFireModel.ComponentModel> componentList; // 组件列表
}