package tech.sud.mgp.audio.gift.callback;

import java.util.List;

import tech.sud.mgp.audio.example.model.UserInfo;

public interface GiftSendClickCallback {
    void sendClick(int giftId, int giftCount, List<UserInfo> toUsers);
}
