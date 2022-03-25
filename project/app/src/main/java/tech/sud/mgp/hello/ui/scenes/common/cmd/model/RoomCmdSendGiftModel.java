package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.HSJsonUtils;

/**
 * 送礼消息信令
 */
public class RoomCmdSendGiftModel extends RoomCmdBaseModel {
    public RoomCmdSendGiftModel(UserInfo sendUser) {
        super(RoomCmd.CMD_SEND_GIFT_NOTIFY, sendUser);
    }

    public int giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public UserInfo toUser; // 收礼人

    public static RoomCmdSendGiftModel fromJson(String json) {
        return HSJsonUtils.fromJson(json, RoomCmdSendGiftModel.class);
    }

}
