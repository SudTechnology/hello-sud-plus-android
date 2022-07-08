package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;

import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.AlibabaCloudConfig;
import tech.sud.mgp.hello.service.main.config.CommsEaseConfig;
import tech.sud.mgp.hello.service.main.config.RongCloudConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.config.TencentCloudConfig;
import tech.sud.mgp.hello.service.main.config.VolcConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;

/**
 * 基础配置返回
 */
public class BaseConfigResp implements Serializable {
    public SudConfig sudCfg; // sud配置
    public ZegoConfig zegoCfg; // 即构配置
    public AgoraConfig agoraCfg; // 声网配置
    public RongCloudConfig rongCloudCfg; // 融云配置
    public CommsEaseConfig commsEaseCfg; // 网易云信配置
    public VolcConfig volcEngineCfg; // 火山引擎配置
    public AlibabaCloudConfig alibabaCloudCfg; // 阿里云配置
    public TencentCloudConfig tencentCloudCfg; // 腾讯云配置
}
