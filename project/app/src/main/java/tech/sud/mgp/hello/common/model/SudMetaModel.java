package tech.sud.mgp.hello.common.model;

import java.util.UUID;

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
    public static int buildNumber; // 构建编号
    public static String rtcType; // rtcType

    /**
     * 生成发送给后端的字符串数据
     */
    public static String buildString() {
        if (locale == null) {
            locale = SystemUtils.getLanguageCode(HelloSudApplication.instance);
        }
        if (clientChannel == null) {
            clientChannel = SystemUtils.getChannel();
        }
        if (clientVersion == null) {
            clientVersion = SystemUtils.getVersionName();
        }
        if (buildNumber == 0) {
            buildNumber = SystemUtils.getVersionCode();
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
        rtcType = AppData.getInstance().getRtcType();
        String uuid = UUID.randomUUID().toString();
        return locale +
                "," +
                clientChannel +
                "," +
                clientVersion +
                "," +
                buildNumber +
                "," +
                deviceId +
                "," +
                systemType +
                "," +
                systemVersion +
                "," +
                clientTimestamp +
                "," +
                rtcType +
                "," +
                uuid;
    }

}
