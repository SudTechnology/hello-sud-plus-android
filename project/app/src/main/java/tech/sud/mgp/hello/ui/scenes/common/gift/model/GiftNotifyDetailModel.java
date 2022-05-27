package tech.sud.mgp.hello.ui.scenes.common.gift.model;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

public class GiftNotifyDetailModel {
    public int giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public GiftModel gift;
    public UserInfo sendUser;
    public UserInfo toUser; // 收礼人
}
