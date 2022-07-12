package tech.sud.mgp.hello.common.model;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.HelloSudApplication;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.config.SudEnvConfig;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;

/**
 * 全局使用的数据
 */
public class AppData {

    private static final AppData instance = new AppData();

    private BaseRtcConfig selectRtcConfig; // 当前所使用的rtc配置
    private SudConfig sudConfig; // sud配置
    private SudEnvConfig sudEnvConfig; // sud环境

    private AppData() {
    }

    public static AppData getInstance() {
        return instance;
    }

    // 保存数据
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (selectRtcConfig != null) {
            outState.putSerializable("BaseRtcConfig", selectRtcConfig);
        }
        if (sudConfig != null) {
            outState.putSerializable("SudConfig", sudConfig);
        }
        if (sudEnvConfig != null) {
            outState.putSerializable("SudEnvConfig", sudEnvConfig);
        }
    }

    // 恢复数据
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Serializable baseRtcConfig = savedInstanceState.getSerializable("BaseRtcConfig");
        if (baseRtcConfig instanceof BaseRtcConfig) {
            this.selectRtcConfig = (BaseRtcConfig) baseRtcConfig;
        }
        Serializable sudConfigSerializable = savedInstanceState.getSerializable("SudConfig");
        if (sudConfigSerializable instanceof SudConfig) {
            this.sudConfig = (SudConfig) sudConfigSerializable;
        }
        Serializable sudEnvConfigSerializable = savedInstanceState.getSerializable("SudEnvConfig");
        if (sudEnvConfigSerializable instanceof SudEnvConfig) {
            this.sudEnvConfig = (SudEnvConfig) sudEnvConfigSerializable;
        }
    }

    /**
     * 获取当前所使用的rtc配置
     */
    public BaseRtcConfig getSelectRtcConfig() {
        if (selectRtcConfig == null) {
            Object rtcConfigSerializable = GlobalCache.getInstance().getSerializable(GlobalCache.RTC_CONFIG_KEY);
            if (rtcConfigSerializable instanceof BaseRtcConfig) {
                selectRtcConfig = (BaseRtcConfig) rtcConfigSerializable;
            }
        }
        return selectRtcConfig;
    }

    /**
     * 设置当前所使用的rtc配置
     */
    public void setSelectRtcConfig(BaseRtcConfig config) {
        selectRtcConfig = config;
    }

    /**
     * 获取当前所用的rtc类型
     */
    public String getRtcType() {
        if (selectRtcConfig != null) {
            return selectRtcConfig.rtcType;
        }
        return null;
    }

    /**
     * 获取当前所用的rtc名称
     */
    public String getRtcName() {
        BaseRtcConfig config = selectRtcConfig;
        if (config != null) {
            return getRtcNameByRtcType(config.rtcType);
        }
        return null;
    }

    /**
     * 根据rtcType获取名称
     */
    public String getRtcNameByRtcType(String rtcType) {
        BaseConfigResp config = null;
        if (config == null || rtcType == null) {
            return null;
        }
        Context context = HelloSudApplication.instance;
        if (config.zegoCfg != null && config.zegoCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_zego);
        }
        if (config.agoraCfg != null && config.agoraCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_agora);
        }
        if (config.rongCloudCfg != null && config.rongCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_rong_cloud);
        }
        if (config.commsEaseCfg != null && config.commsEaseCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_comms_ease);
        }
        if (config.volcEngineCfg != null && config.volcEngineCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_volc_engine);
        }
        if (config.alibabaCloudCfg != null && config.alibabaCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_alibaba_cloud);
        }
        if (config.tencentCloudCfg != null && config.tencentCloudCfg.rtcType.equals(rtcType)) {
            return context.getString(R.string.rtc_name_tencent_cloud);
        }
        return null;
    }

    public SudConfig getSudConfig() {
        return sudConfig;
    }

    public void setSudConfig(SudConfig sudConfig) {
        this.sudConfig = sudConfig;
    }

    public SudEnvConfig getSudEnvConfig() {
        return sudEnvConfig;
    }

    public void setSudEnvConfig(SudEnvConfig sudEnvConfig) {
        this.sudEnvConfig = sudEnvConfig;
    }
}
