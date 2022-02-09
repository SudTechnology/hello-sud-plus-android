package tech.sud.mgp.audio.gift.listener;

import java.util.List;

import tech.sud.mgp.audio.example.model.UserInfo;

public interface GiftSendClickListener {
    void onSendClick(int giftId, int giftCount, List<UserInfo> toUsers);
}
