package tech.sud.mgp.hello.service.main.resp;

import java.io.Serializable;

import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;

/**
 * 基础配置返回
 */
public class BaseConfigResp implements Serializable {
    public ZegoConfig zegoCfg;
    public SudConfig sudCfg;
    public AgoraConfig agoraCfg;
}
