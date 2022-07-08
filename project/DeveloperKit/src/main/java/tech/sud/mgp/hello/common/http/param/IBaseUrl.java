package tech.sud.mgp.hello.common.http.param;

public interface IBaseUrl {

    /**
     * 设置这个请求头时，用这个值作为请求地址的完整路径
     */
    String KEY_FULL_URL = "key_full_url";

    /**
     * 重新定义的baseUrl
     */
    String KEY_BASE_URL = "key_base_url";

    /**
     * 基础服务
     */
    String getBaseUrl();

    /**
     * 游戏服务
     */
    String getGameBaseUrl();

}
