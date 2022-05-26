package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.CacheDiskUtils;

/**
 * 硬盘缓存，可以存储对象
 */
public class GlobalCache {

    public static CacheDiskUtils getInstance() {
        return CacheDiskUtils.getInstance("global.cache");
    }

}
