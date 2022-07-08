package tech.sud.mgp.hello.ui.scenes.common.gift.listener;

import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;

public interface GiftSendClickListener {
    void onSendClick(GiftModel giftModel, int giftCount, List<UserInfo> toUsers);

    void onNotify(Map<Long, Boolean> userState);
}
