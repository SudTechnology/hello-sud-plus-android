package tech.sud.mgp.hello.service.main.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * 游戏sdk配置
 */
public class SudConfig implements Serializable {

    @SerializedName(value = "app_id")
    public String appId;

    @SerializedName(value = "app_key")
    public String appKey;

    @SerializedName(value = "app_name")
    public String area; // 地区

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SudConfig sudConfig = (SudConfig) o;
        return Objects.equals(appId, sudConfig.appId) && Objects.equals(appKey, sudConfig.appKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, appKey);
    }
}
