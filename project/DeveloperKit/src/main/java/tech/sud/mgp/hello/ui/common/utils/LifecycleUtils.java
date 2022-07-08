package tech.sud.mgp.hello.ui.common.utils;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LifecycleUtils {

    /**
     * 安全的生命周期下回调
     * 比如可以用在DialogFragment的安全地显示上
     */
    public static void safeLifecycle(LifecycleOwner owner, CompletedListener listener) {
        safeLifecycle(owner, 0, listener);
    }

    /**
     * 安全的生命周期下回调
     * 比如可以用在DialogFragment的安全地显示上
     *
     * @param waitTime 超时时间 值为0时没有超时，单位为毫秒
     */
    public static Future<Object> safeLifecycle(LifecycleOwner owner, long waitTime, CompletedListener listener) {
        MyFuture myFuture = new MyFuture(owner);
        myFuture.start(waitTime, listener);
        return myFuture;
    }

    public static class MyFuture implements Future<Object> {

        private boolean isCancelled;
        private boolean isDone;
        private LifecycleOwner owner;
        private LifecycleEventObserver observer;

        public MyFuture(LifecycleOwner owner) {
            this.owner = owner;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            isCancelled = true;
            isDone = true;
            if (owner != null && observer != null) {
                owner.getLifecycle().removeObserver(observer);
            }
            owner = null;
            observer = null;
            return true;
        }

        /** 是否已取消 */
        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        /** 是否已完成 */
        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public Object get() throws ExecutionException, InterruptedException {
            return null;
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return null;
        }

        private void start(long waitTime, CompletedListener listener) {
            if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                isDone = true;
                return;
            }
            if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                isDone = true;
                listener.onCompleted();
                return;
            }
            long expiredTime;
            if (waitTime == 0) {
                expiredTime = Long.MAX_VALUE;
            } else {
                expiredTime = SystemClock.elapsedRealtime() + waitTime;
            }
            observer = new LifecycleEventObserver() {

                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (isDone) {
                        return;
                    }
                    if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                        removeObserver();
                        return;
                    }
                    if (owner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        removeObserver();
                        if (SystemClock.elapsedRealtime() <= expiredTime) {
                            listener.onCompleted();
                        }
                    }
                }

                private void removeObserver() {
                    isDone = true;
                    owner.getLifecycle().removeObserver(this);
                }

            };
            owner.getLifecycle().addObserver(observer);
        }
    }

}
