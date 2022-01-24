package tech.sud.mgp.hello.login.http.method;

import tech.sud.mgp.common.http.retrofit.RetrofitManager;

public class LoginRequestMethodFactory {

    private static final LoginRequestMethod mAppRequestMethod = RetrofitManager.createMethod(LoginRequestMethod.class);

    public static LoginRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
