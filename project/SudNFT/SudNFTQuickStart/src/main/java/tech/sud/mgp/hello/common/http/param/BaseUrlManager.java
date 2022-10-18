package tech.sud.mgp.hello.common.http.param;

public class BaseUrlManager {

    private static final IBaseUrl iBaseUrl = new UrlImpl();

    /**
     * 获取基础服务
     */
    public static String getBaseUrl() {
        return iBaseUrl.getBaseUrl();
    }

    /**
     * 获取游戏服务
     */
    public static String getGameBaseUrl() {
        return iBaseUrl.getGameBaseUrl();
    }

}
