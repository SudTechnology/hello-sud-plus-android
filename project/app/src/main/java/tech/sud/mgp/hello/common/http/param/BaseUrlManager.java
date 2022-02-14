package tech.sud.mgp.hello.common.http.param;

public class BaseUrlManager {

    private static final IBaseUrl iBaseUrl = new FatUrl();

    /**
     * 获取基础服务
     */
    public static String getBaseUrl() {
        return iBaseUrl.getBaseUrl();
    }

    /**
     * 获取房间服务
     */
    public static String getInteractBaseUrl() {
        return iBaseUrl.getInteractBaseUrl();
    }

}
