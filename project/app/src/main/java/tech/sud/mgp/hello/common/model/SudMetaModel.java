package tech.sud.mgp.hello.common.model;

import tech.sud.mgp.hello.app.HelloSudApplication;
import tech.sud.mgp.hello.common.utils.SystemUtils;

/**
 * 客户端请求头当中的信息
 */
public class SudMetaModel {
    public static String locale; // 地区码
    public static String clientChannel; // 客户端渠道号
    public static String clientVersion; // 客户端版本
    public static String deviceId; // 设备ID
    public static String systemType; // 系统类型
    public static String systemVersion; // 系统版本
    public static long clientTimestamp; // 客户端时间戳

    /**
     * 生成发送给后端的字符串数据
     */
    public static String buildString() {
        if (locale == null) {
            locale = SystemUtils.getLanguageCode(HelloSudApplication.INSTANCE);
        }
        if (clientChannel == null) {
            clientChannel = SystemUtils.getChannel();
        }
        if (clientVersion == null) {
            clientVersion = SystemUtils.getAppVersion();
        }
        if (deviceId == null) {
            deviceId = SystemUtils.getDeviceId();
        }
        if (systemType == null) {
            systemType = "Android";
        }
        if (systemVersion == null) {
            systemVersion = SystemUtils.getSystemVersion();
        }
        clientTimestamp = System.currentTimeMillis();
        return locale +
                "," +
                clientChannel +
                "," +
                clientVersion +
                "," +
                deviceId +
                "," +
                systemType +
                "," +
                systemVersion +
                "," +
                clientTimestamp;
    }

}