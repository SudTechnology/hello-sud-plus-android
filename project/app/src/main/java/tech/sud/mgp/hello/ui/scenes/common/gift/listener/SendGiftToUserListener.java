package tech.sud.mgp.hello.ui.scenes.common.gift.listener;

import java.util.Map;

public interface SendGiftToUserListener {
    void onNotify(Map<Long, Boolean> userState);
}
