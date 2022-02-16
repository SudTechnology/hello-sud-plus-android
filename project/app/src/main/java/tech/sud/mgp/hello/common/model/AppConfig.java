package tech.sud.mgp.hello.common.model;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;

import tech.sud.mgp.hello.ui.main.http.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.main.model.config.SudConfig;

/**
 * 全局配置
 */
public class AppConfig {

    private static final AppConfig instance = new AppConfig();

    private BaseConfigResp baseConfigResp;

    private AppConfig() {
    }

    public static AppConfig getInstance() {
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
    }

    // 恢复数据
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Serializable baseConfigResp = savedInstanceState.getSerializable("BaseConfigResp");
        if (baseConfigResp instanceof BaseConfigResp) {
            this.baseConfigResp = (BaseConfigResp) baseConfigResp;
        }
    }

    /**
     * 获取当前所用的rtc类型
     */
    public String getRtcType() {
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
