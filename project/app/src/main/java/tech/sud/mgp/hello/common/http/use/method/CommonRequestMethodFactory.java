package tech.sud.mgp.hello.common.http.use.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class CommonRequestMethodFactory {

    private static final CommonRequestMethod method = RetrofitManager.createMethod(CommonRequestMethod.class);

    public static CommonRequestMethod getMethod() {
        return method;
    }

}
