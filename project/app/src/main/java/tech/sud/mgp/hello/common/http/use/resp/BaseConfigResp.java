package tech.sud.mgp.hello.common.http.use.resp;

import tech.sud.mgp.hello.common.http.use.model.AgoraConfig;
import tech.sud.mgp.hello.common.http.use.model.SudConfig;
import tech.sud.mgp.hello.common.http.use.model.ZegoConfig;

/**
 * 基础配置返回
 */
public class BaseConfigResp {
    public ZegoConfig zegoCfg;
    public SudConfig sudCfg;
    public AgoraConfig agoraCfg;
}
