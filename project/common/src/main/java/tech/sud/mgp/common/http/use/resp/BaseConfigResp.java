package tech.sud.mgp.common.http.use.resp;

import tech.sud.mgp.common.http.use.model.AgoraCfg;
import tech.sud.mgp.common.http.use.model.SudCfg;
import tech.sud.mgp.common.http.use.model.ZegoCfg;

/**
 * 基础配置返回
 */
public class BaseConfigResp {
    public int rtcThirdPlatform;
    public ZegoCfg zegoCfg;
    public SudCfg sudCfg;
    public AgoraCfg agoraCfg;
}
