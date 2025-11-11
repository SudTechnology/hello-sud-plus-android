package tech.sud.mgp.hello.ui.scenes.ad.utils;

import android.os.Handler;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.concurrent.TimeUnit;

/**
 * 秒表的作用，用于计时
 * 会不断累加消耗的时间，除非调用了reset方法
 */
public class SudGameStopwatch {
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private OnTimingListener mOnTimingListener;
    private Handler mHandler = ThreadUtils.getMainHandler();

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        startTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        startNotifyTask();
    }

    /**
     * 注意，这个stop方法，并不会清除已有的计时时间，相当于有一个pause的效果
     * 如果stop之后下次再调start，那么下次的stop会和之前的计时时间累加
     * 如果要清零，则调用reset方法
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        long endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        elapsedTime += endTime - startTime;
        stopNotifyTask();
    }

    private void startNotifyTask() {
        if (!isRunning) {
            return;
        }
        stopNotifyTask();
        mHandler.postDelayed(mNotifyTask, 1000 / 30);  // 一秒30帧频率更新
    }

    private void stopNotifyTask() {
        mHandler.removeCallbacks(mNotifyTask);
    }

    public long getElapsedTimeInMillis() {
        if (!isRunning) { // 如果已经是停了，那直接取值
            return elapsedTime;
        }
        // 如果还在跑，那就动态计算一下
        long endTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        return elapsedTime + endTime - startTime;
    }

    /** 重置状态 */
    public void reset() {
        isRunning = false;
        startTime = 0;
        elapsedTime = 0;
        stopNotifyTask();
    }

    private final Runnable mNotifyTask = new Runnable() {
        @Override
        public void run() {
            if (mOnTimingListener != null) {
                mOnTimingListener.onTiming(getElapsedTimeInMillis());
            }
            startNotifyTask();
        }
    };

    public void setOnTimingListener(OnTimingListener onTimingListener) {
        mOnTimingListener = onTimingListener;
    }

    public interface OnTimingListener {
        void onTiming(long millis);
    }
}
