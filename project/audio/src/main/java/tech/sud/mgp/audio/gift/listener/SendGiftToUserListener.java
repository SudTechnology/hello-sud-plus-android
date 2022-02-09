package tech.sud.mgp.audio.gift.listener;

import java.util.Map;

public interface SendGiftToUserListener {
    void onNotify(Map<Long,Boolean> userState);
}
