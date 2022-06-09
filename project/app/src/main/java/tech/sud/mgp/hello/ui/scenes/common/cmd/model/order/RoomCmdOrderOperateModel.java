package tech.sud.mgp.hello.ui.scenes.common.cmd.model.order;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmd;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.RoomCmdBaseModel;

/**
 * 主播同意或者拒绝用户点单
 */
public class RoomCmdOrderOperateModel extends RoomCmdBaseModel {
    public RoomCmdOrderOperateModel(UserInfo sendUser) {
        super(RoomCmd.CMD_ORDER_OPERATE_NOTIFY, sendUser);
    }

    public long orderId; // 订单id
    public long gameId; //游戏id
    public String gameName; //游戏名字
    public String toUser; // 下单的用户id
    public boolean operate;//true同意false拒绝

    public static RoomCmdOrderOperateModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdOrderOperateModel.class);
    }

}
