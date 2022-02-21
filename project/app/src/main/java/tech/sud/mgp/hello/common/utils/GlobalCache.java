package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.CacheDiskUtils;

/**
 * 硬盘缓存，可以存储对象
 */
public class GlobalCache {

    public static final String BASE_CONFIG_KEY = "base_config_key"; // 基础配置
    public static final String RTC_CONFIG_KEY = "rtc_config_key"; // 当前所用的rtc配置

    public static CacheDiskUtils getInstance() {
        return CacheDiskUtils.getInstance("global.cache");
    }

}
