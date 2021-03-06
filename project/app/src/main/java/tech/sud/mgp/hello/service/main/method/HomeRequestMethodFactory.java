package tech.sud.mgp.hello.service.main.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class HomeRequestMethodFactory {

    private static final HomeRequestMethod mAppRequestMethod = RetrofitManager.createMethod(HomeRequestMethod.class);

    public static HomeRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
