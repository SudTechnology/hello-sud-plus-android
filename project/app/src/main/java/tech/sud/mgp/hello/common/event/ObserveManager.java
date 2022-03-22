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
    private Observer<ChangeLanguageEvent> mChangeLanguageObserver;

    private static ObserveManager observeManager;

    private ObserveManager() {
    }

    public static ObserveManager getInstance() {
        if (observeManager == null) {
            observeManager = new ObserveManager();
        }
        return observeManager;
    }

    /**
     * 切换语言后的事件通知观察
     */
    public void observeForever(ObserveListener listener) {
        mChangeLanguageObserver = o -> {
            if (listener != null) {
                listener.onChanged(o);
            }
        };
        LiveEventBus.get(LiveEventBusKey.KEY_CHANGE_LANGUAGE,ChangeLanguageEvent.class).observeForever(mChangeLanguageObserver);
    }

    /**
     * 切换语言后的事件通知移除
     */
    public void removeObserver() {
        if (mChangeLanguageObserver != null) {
            LiveEventBus.get(LiveEventBusKey.KEY_CHANGE_LANGUAGE,ChangeLanguageEvent.class).removeObserver(mChangeLanguageObserver);
        }
    }

    public interface ObserveListener {
        void onChanged(Object o);
    }
}
