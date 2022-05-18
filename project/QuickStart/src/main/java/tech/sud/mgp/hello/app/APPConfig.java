/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

import tech.sud.mgp.hello.BuildConfig;

/**
 * 全局配置
 */
public class APPConfig {

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    public static final boolean GAME_IS_TEST_ENV = false;

    /** Bugly的AppId */
    public static final String BUGLY_APP_ID = BuildConfig.buglyAppId;

}
