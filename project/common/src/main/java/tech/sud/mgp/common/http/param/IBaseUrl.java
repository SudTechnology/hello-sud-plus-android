package tech.sud.mgp.common.http.param;

public interface IBaseUrl {

    /**
     * 设置这个请求头时，用这个值作为请求地址的完整路径
     */
    public static final String KEY_FULL_URL = "key_full_url";

    /**
     * 重新定义的baseUrl
     */
    public static final String KEY_BASE_URL = "key_base_url";

    /**
     * 基础服务
     *
     * @return
     */
    String getBaseUrl();

    /**
     * 通信
     *
     * @return
     */
    String getInteractBaseUrl();

}