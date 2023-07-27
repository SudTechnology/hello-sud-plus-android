package tech.sud.mgp.hello.service.game.resp;

import java.util.List;
import java.util.Map;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;

/**
 * 发射火箭 返回参数
 */
public class RocketFireResp {

    /**
     * 用户对应订单列表
     * map的key为userId
     * value集合中，对应该用户的所有订单
     */
    public Map<String, List<String>> userOrderIdsMap;
    public SudMGPAPPState.AppCustomRocketPlayModelList.InteractConfigModel interactConfig;
    public List<SudMGPAPPState.AppCustomRocketPlayModelList.ComponentModel> componentList; // 组件列表
}
