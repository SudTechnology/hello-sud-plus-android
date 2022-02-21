package tech.sud.mgp.hello.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工厂
 */
public class ThreadPoolFactory {

    /**
     * 获取一个单线程池
     */
    public static ExecutorService getSingleThreadPool() {
        return Executors.newSingleThreadExecutor();
    }

}
