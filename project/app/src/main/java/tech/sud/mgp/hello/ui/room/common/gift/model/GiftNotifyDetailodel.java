package tech.sud.mgp.hello.ui.room.common.gift.model;

import tech.sud.mgp.hello.ui.room.audio.model.UserInfo;

public class GiftNotifyDetailodel {
    public int giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public GiftModel gift;
    public UserInfo sendUser;
    public UserInfo toUser; // 收礼人
}