package tech.sud.mgp.rtc.audio.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncCallWrapper {

    private static AsyncCallWrapper asyncCallWrapper;

    public static AsyncCallWrapper sharedInstance() {
        if (asyncCallWrapper == null) {
            synchronized (AsyncCallWrapper.class) {
                if (asyncCallWrapper == null) {
                    asyncCallWrapper = new AsyncCallWrapper();
                }
            }
        }

        return asyncCallWrapper;
    }

    /**
     * 线程池执行器
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    /**
     * 串行执行任务
     *
     * @param runnable 要执行的任务
     */
    public void executeInSerial(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }
}
