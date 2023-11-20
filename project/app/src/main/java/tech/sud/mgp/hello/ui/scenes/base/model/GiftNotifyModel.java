package tech.sud.mgp.hello.ui.scenes.base.model;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

public class GiftNotifyModel {
    public long giftID; // 礼物ID
    public int giftCount; // 礼物数量
    public GiftModel gift;
    public UserInfo sendUser;
    public List<UserInfo> toUserList; // 收礼人列表
    public boolean isAllSeat; // 是否是全麦礼物
}
