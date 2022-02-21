package tech.sud.mgp.hello.common.http.param;

public class FatUrl implements IBaseUrl {

    @Override
    public String getBaseUrl() {
        return "https://base-hello-sud.sud.tech/";
    }

    @Override
    public String getInteractBaseUrl() {
        return "https://interact-hello-sud.sud.tech/";
    }
}
