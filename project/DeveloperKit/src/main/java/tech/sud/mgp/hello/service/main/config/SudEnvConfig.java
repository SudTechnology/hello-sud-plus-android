package tech.sud.mgp.hello.service.main.config;

import java.io.Serializable;
import java.util.Objects;

import tech.sud.mgp.hello.BuildConfig;

/**
 * sud 环境设置
 */
public class SudEnvConfig implements Serializable {

    /** 名称 */
    public String name;

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    public boolean isTestEnv = BuildConfig.gameIsTestEnv;

    /**
     * 1:Pro
     * 2:Sim
     * 3:Fat
     * 4:Dev
     */
    public int env = 1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SudEnvConfig that = (SudEnvConfig) o;
        return isTestEnv == that.isTestEnv && env == that.env && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isTestEnv, env);
    }
}
