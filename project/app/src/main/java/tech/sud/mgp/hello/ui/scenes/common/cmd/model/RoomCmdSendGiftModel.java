package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 送礼消息信令
 */
public class RoomCmdSendGiftModel extends RoomCmdBaseModel {
    public RoomCmdSendGiftModel(UserInfo sendUser) {
        super(RoomCmd.CMD_SEND_GIFT_NOTIFY, sendUser);
    }

    public long giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public UserInfo toUser; // 收礼人
    public int type; // 1.4.0新增:礼物类型 0：内置礼物 1：后端配置礼物
    public String giftName; // 1.4.0新增：礼物名称
    public String giftUrl; // 1.4.0新增：礼物图片
    public String animationUrl; // 1.4.0新增：礼物动图

    public static RoomCmdSendGiftModel fromJson(String json) {
        return SudJsonUtils.fromJson(json, RoomCmdSendGiftModel.class);
    }

}
