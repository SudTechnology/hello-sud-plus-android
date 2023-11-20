package tech.sud.mgp.hello.ui.scenes.common.gift.utils;

import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

public class GiftModelConverter {

    public static GiftModel conver(GiftListResp.BackGiftModel src) {
        if (src == null) {
            return null;
        }
        GiftModel dest = new GiftModel();
        dest.type = 1;
        dest.giftId = src.giftId;
        dest.giftName = src.name;
        dest.giftPrice = src.giftPrice;
        dest.giftUrl = src.giftUrl;
        dest.animationUrl = src.animationUrl;
        dest.details = src.details;
        return dest;
    }

}
