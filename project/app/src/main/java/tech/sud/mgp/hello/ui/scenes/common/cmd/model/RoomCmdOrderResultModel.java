package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;

/**
 * 主播同意或者拒绝用户点单
 */
public class RoomCmdOrderResultModel extends RoomCmdBaseModel {
    public RoomCmdOrderResultModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ORDER_RESULT_NOTIFY, sendUser);
    }

    public long orderId; // 订单id
    public long gameId; //游戏id
    public String gameName; //游戏名字
    public String toUser; // 下单的用户id
    public boolean state;//true同意false拒绝

    public static RoomCmdOrderResultModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdOrderResultModel.class);
    }

}
