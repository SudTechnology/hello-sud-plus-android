package tech.sud.mgp.hello.service.main.config;

import android.content.Context;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.HelloSudApplication;

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

    // 获取经过转换的rtc名称，用于显示在Ui上面
    public String getRtcName() {
        Context context = HelloSudApplication.instance;
        if (this instanceof ZegoConfig) {
            return context.getString(R.string.rtc_name_zego);
        }
        if (this instanceof AgoraConfig) {
            return context.getString(R.string.rtc_name_agora);
        }
        return desc;
    }

}
