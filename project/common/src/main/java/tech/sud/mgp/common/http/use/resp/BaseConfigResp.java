package tech.sud.mgp.common.http.use.resp;

import tech.sud.mgp.common.http.use.model.AgoraConfig;
import tech.sud.mgp.common.http.use.model.SudConfig;
import tech.sud.mgp.common.http.use.model.ZegoConfig;

/**
 * 基础配置返回
 */
public class BaseConfigResp {
    public int rtcThirdPlatform;
    public ZegoConfig zegoCfg;
    public SudConfig sudCfg;
    public AgoraConfig agoraCfg;
}
