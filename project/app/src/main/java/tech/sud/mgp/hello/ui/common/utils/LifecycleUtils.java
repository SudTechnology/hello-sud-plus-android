package tech.sud.mgp.hello.ui.common.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class LifecycleUtils {

    /**
     * 安全的生命周期下回调
     * 比如可以用在DialogFragment的安全地显示上
     */
    public static void safeLifecycle(LifecycleOwner owner, CompletedListener listener) {
        if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            return;
        }
        if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            listener.onCompleted();
            return;
        }
        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            boolean isDestroy = false;

            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (isDestroy) {
                    return;
                }
                if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                    removeObserver();
                    return;
                }
                if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                    removeObserver();
                    listener.onCompleted();
                }
            }

            private void removeObserver() {
                isDestroy = true;
                owner.getLifecycle().removeObserver(this);
            }

        });
    }

    public interface CompletedListener {
        void onCompleted();
    }

}
