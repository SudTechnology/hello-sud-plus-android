package tech.sud.mgp.common.http.param;

public class FatUrl implements IBaseUrl {

    @Override
    public String getBaseUrl() {
        return "https://fat-base.sud.tech/";
    }

    @Override
    public String getInteractBaseUrl() {
        return " https://fat-interact.sud.tech/";
    }
}
