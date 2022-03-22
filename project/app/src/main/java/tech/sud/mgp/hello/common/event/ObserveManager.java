package tech.sud.mgp.hello.common.event;

import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;

/**
 * 统一管理livebus的ob
 */
public class ObserveManager {

    /**
     * 切换语言后的事件通知
     */
    public static void obChangeLanguage(ObserveListener listener) {
        Observer observer = o -> {
            if (listener != null) {
                listener.onChanged(o);
            }
        };
        LiveEventBus.get(LiveEventBusKey.KEY_CHANGE_LANGUAGE).observeForever(observer);
    }

    public interface ObserveListener {
        void onChanged(Object o);
    }

}
