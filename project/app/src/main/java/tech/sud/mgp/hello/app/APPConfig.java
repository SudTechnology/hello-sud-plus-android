/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.app;

/**
 * 全局配置
 */
public class APPConfig {

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    public static final boolean GAME_IS_TEST_ENV = false;

    /**
     * Bugly的AppId
     */
    public static final String BUGLY_APP_ID = "064b6ebe0f";

    // region 占用大小
    public static final long SudMGPCoreSize = 590848;
    public static final long SudMGPASRSize = 76800;
    public static final long HelloSudSize = 35770369;
    public static final long ZegoRTCSDKSize = 21307064;
    public static final long AgoraRTCSDKSize = 24431820;
    // endregion 占用大小

    // region url地址
    public static final String USER_PRIVACY_URL = "file:///android_asset/user_privacy.html";
    public static final String USER_PROTOCAL_URL = "file:///android_asset/user_protocol.html";
    public static final String APP_LICENSE_URL = "file:///android_asset/license.html";
    public static final String GIT_HUB_URL = "https://github.com/SudTechnology/hello-sud-android";
    // endregion url地址

}
