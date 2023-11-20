package tech.sud.mgp.hello.ui.scenes.base.model;

import tech.sud.mgp.hello.service.room.resp.GiftListResp;

public class MonopolyCardGiftModel {
    public boolean isDanmaku; // 是否是用于弹幕展示
    public GiftListResp.BackGiftModel giftModel;

    public int getCardType() {
        if (giftModel != null && giftModel.details != null) {
            return giftModel.details.cardType;
        }
        return -1;
    }

    public String getDescription() {
        if (giftModel != null && giftModel.details != null) {
            return giftModel.details.description;
        }
        return null;
    }

    public String getGiftUrl() {
        if (giftModel != null) {
            return giftModel.giftUrl;
        }
        return null;
    }

    public String getGiftName() {
        if (giftModel != null) {
            return giftModel.name;
        }
        return null;
    }

    public GiftListResp.Details getGiftDetails() {
        if (giftModel != null) {
            return giftModel.details;
        }
        return null;
    }

}
