package tech.sud.mgp.hello.ui.room.common.gift.listener;

import java.util.Map;

public interface SendGiftToUserListener {
    void onNotify(Map<Long, Boolean> userState);
}
