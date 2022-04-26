package tech.sud.mgp.hello.common.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import kotlin.jvm.Synchronized;

/**
 * 自定义的倒计时器，指定间隔之后总数减一，到零的时候触发结束
 */
public abstract class CustomCountdownTimer {

    private int mTotalCount;
    private long mCountdownInterval;
    private static int MSG = 1;
    private boolean isRunning = false;
    private long mStopTimeInFuture;

    public CustomCountdownTimer(int totalCount) {
        this(totalCount, 1000);
    }

    public CustomCountdownTimer(int totalCount, long countdownInterval) {
        this.mTotalCount = totalCount;
        this.mCountdownInterval = countdownInterval;
    }

    /**
     * Cancel the countdown.
     */
    @Synchronized
    public synchronized void cancel() {
        isRunning = false;
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    public synchronized CustomCountdownTimer start() {
        isRunning = true;
        if (mTotalCount <= 0 || mCountdownInterval <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mTotalCount * mCountdownInterval;
        onTick(mTotalCount);
        mHandler.removeMessages(MSG);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), mCountdownInterval);
        return this;
    }

    /**
     * 剩余数字
     *
     * @param count
     */
    abstract void onTick(int count);

    /**
     * Callback fired when the time is up.
     */
    abstract void onFinish();

    // handles counting down
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            synchronized (CustomCountdownTimer.this) {
                if (!isRunning) {
                    return;
                }
                mTotalCount -= 1;
                if (mTotalCount <= 0) {
                    isRunning = false;
                    onFinish();
                } else {
                    long delay = mCountdownInterval;
                    long lastTickStart = SystemClock.elapsedRealtime();
                    long lastStopTimeInFuture = lastTickStart + mTotalCount * mCountdownInterval; // 按照当前的进度,计算结束的时间
                    if (lastStopTimeInFuture - mStopTimeInFuture > 1000L) { // 推断如果比预计的推迟了一定值之后，重新计算当前值
                        mTotalCount = (int) ((mStopTimeInFuture - lastTickStart) / mCountdownInterval);
                        delay += (mStopTimeInFuture - lastTickStart) % mCountdownInterval;
                    }
                    onTick(mTotalCount);
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

    /** 获取当前的数字 */
    public int getCurrentCount() {
        return mTotalCount;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
