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
    public static final boolean GAME_IS_TEST_ENV = BuildConfig.gameIsTestEnv;

    /** Bugly的AppId */
    public static final String BUGLY_APP_ID = BuildConfig.buglyAppId;

    /** 分页刷新数据每页大小 */
    public static int GLOBAL_PAGE_SIZE = 30;

    /** 跨房pk默认时长 */
    public static int ROOM_PK_MINUTE = 5;

    // region 占用大小
    public static final long SudMGPCoreSize = 590848;
    public static final long SudMGPASRSize = 76800;
    public static final long HelloSudSize = 46256129;
    public static final long ZegoRTCSDKSize = 21307064;
    public static final long AgoraRTCSDKSize = 24431820;
    public static final long RCloudRTCSDKSize = 12436111;
    public static final long NeteaseRTCSDKSize = 10800332;
    public static final long ZegoZIMSDKSize = 24117248;
    public static final long TxTRTCSDKSize = 8388608;
    public static final long RTCSDKSize = 101481183;
    // endregion 占用大小

    // region url地址
    public static final String USER_PRIVACY_URL = "file:///android_asset/user_privacy.html";
    public static final String USER_PROTOCAL_URL = "file:///android_asset/user_protocol.html";
    public static final String APP_LICENSE_URL = "file:///android_asset/license.html";
    public static final String GIT_HUB_URL = "https://github.com/SudTechnology/hello-sud-android";
    // endregion url地址

    /** 竞猜场景，单人竞猜数量 */
    public static int QUIZ_SINGLE_BET_COUNT = 200;

}
