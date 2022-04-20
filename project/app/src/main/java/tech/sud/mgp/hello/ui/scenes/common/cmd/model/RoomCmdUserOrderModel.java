package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;

/**
 * 用户点单信令
 */
public class RoomCmdUserOrderModel extends RoomCmdBaseModel {
    public RoomCmdUserOrderModel(UserInfo sendUser) {
        super(RoomCmd.CMD_USER_ORDER_NOTIFY, sendUser);
    }

    public long orderId; // 订单id
    public long gameId; //游戏id
    public String gameName; //游戏名字
    public List<String> toUsers; // 下单邀请的主播id列表

    public static RoomCmdUserOrderModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdUserOrderModel.class);
    }

}
