package tech.sud.mgp.hello.common.http.param;

import tech.sud.mgp.hello.BuildConfig;

public class UrlImpl implements IBaseUrl {

    @Override
    public String getBaseUrl() {
        return BuildConfig.baseUrl;
    }

    @Override
    public String getInteractBaseUrl() {
        return BuildConfig.interactBaseUrl;
    }

    @Override
    public String getGameBaseUrl() {
        return BuildConfig.gameBaseUrl;
    }
}
