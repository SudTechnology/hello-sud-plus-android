package tech.sud.mgp.hello.service.game.req;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;

public class CreateOrderReq {
    public long gameId;
    public String roomId;

    public String cmd; // 触发的行为动作，比如打赏，购买等
    public String fromUid; // 付费用户uid
    public String toUid; // 目标用户uid
    public long value; // 所属的游戏价值
    public String payload; // 扩展数据 json 字符串, 特殊可选

    public void setCreateOrderValues(SudGIPMGState.MGCommonGameCreateOrder model) {
        if (model == null) {
            return;
        }
        cmd = model.cmd;
        fromUid = model.fromUid;
        toUid = model.toUid;
        value = model.value;
        payload = model.payload;
    }

}
