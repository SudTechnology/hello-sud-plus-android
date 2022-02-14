package tech.sud.mgp.hello.common.model;

import tech.sud.mgp.hello.common.http.use.model.SudConfig;
import tech.sud.mgp.hello.common.http.use.resp.BaseConfigResp;

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

}
