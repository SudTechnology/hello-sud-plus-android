package tech.sud.mgp.hello.ui.scenes.common.gift.listener;

import java.util.List;
import java.util.Map;

import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

public interface GiftSendClickListener {
    void onSendClick(int giftId, int giftCount, List<UserInfo> toUsers);

    void onNotify(Map<Long, Boolean> userState);
}
