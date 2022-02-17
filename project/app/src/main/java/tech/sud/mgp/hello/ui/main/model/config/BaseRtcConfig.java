package tech.sud.mgp.hello.ui.main.model.config;

import java.io.Serializable;

/**
 * rtc配置的基类
 */
public class BaseRtcConfig implements Serializable {

    public BaseRtcConfig() {
    }

    public BaseRtcConfig(String rtcType, String desc) {
        this.rtcType = rtcType;
        this.desc = desc;
    }

    public String rtcType;
    public String desc;
}
