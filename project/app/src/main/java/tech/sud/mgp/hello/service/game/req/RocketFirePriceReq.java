package tech.sud.mgp.hello.service.game.req;

import java.util.List;

/**
 * 火箭发射价格 请求参数
 */
public class RocketFirePriceReq {

    public List<ComponentModel> componentList; // 组件列表

    public static class ComponentModel {
        public String itemId; // 已购买的唯一标识
    }
    
}
