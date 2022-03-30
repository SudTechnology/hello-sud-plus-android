package tech.sud.mgp.hello.common.model;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;

import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;

/**
 * 全局使用的数据
 */
public class AppData {

    private static final AppData instance = new AppData();

    private BaseConfigResp baseConfigResp; // 基础配置
    private BaseRtcConfig selectRtcConfig; // 当前所使用的rtc配置

    private AppData() {
    }

    public static AppData getInstance() {
        return instance;
    }

    public void setBaseConfigResp(BaseConfigResp resp) {
        baseConfigResp = resp;
    }

    public SudConfig getSudConfig() {
        if (baseConfigResp != null) {
            return baseConfigResp.sudCfg;
        }
        return null;
    }

    // 保存数据
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (baseConfigResp != null) {
            outState.putSerializable("BaseConfigResp", baseConfigResp);
        }
        if (selectRtcConfig != null) {
            outState.putSerializable("BaseRtcConfig", selectRtcConfig);
        }
    }

    // 恢复数据
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Serializable baseConfigResp = savedInstanceState.getSerializable("BaseConfigResp");
        if (baseConfigResp instanceof BaseConfigResp) {
            this.baseConfigResp = (BaseConfigResp) baseConfigResp;
        }
        Serializable baseRtcConfig = savedInstanceState.getSerializable("BaseRtcConfig");
        if (baseRtcConfig instanceof BaseRtcConfig) {
            this.selectRtcConfig = (BaseRtcConfig) baseRtcConfig;
        }
    }

    /**
     * 获取当前所使用的rtc配置
     */
    public BaseRtcConfig getSelectRtcConfig() {
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
            return config.getRtcName();
        }
        return null;
    }

    /**
     * 根据rtcType获取名称
     */
    public String getRtcNameByRtcType(String rtcType) {
        BaseConfigResp config = baseConfigResp;
        if (config == null || rtcType == null) {
            return null;
        }
        if (config.zegoCfg != null && config.zegoCfg.rtcType.equals(rtcType)) {
            return config.zegoCfg.desc;
        }
        if (config.agoraCfg != null && config.agoraCfg.rtcType.equals(rtcType)) {
            return config.agoraCfg.desc;
        }
        return null;
    }

}
