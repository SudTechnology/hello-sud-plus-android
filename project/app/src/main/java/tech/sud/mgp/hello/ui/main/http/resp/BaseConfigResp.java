package tech.sud.mgp.hello.ui.main.http.resp;

import java.io.Serializable;

import tech.sud.mgp.hello.ui.main.http.model.AgoraConfig;
import tech.sud.mgp.hello.ui.main.http.model.SudConfig;
import tech.sud.mgp.hello.ui.main.http.model.ZegoConfig;

/**
 * 基础配置返回
 */
public class BaseConfigResp implements Serializable {
    public ZegoConfig zegoCfg;
    public SudConfig sudCfg;
    public AgoraConfig agoraCfg;
}