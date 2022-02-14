package tech.sud.mgp.hello.ui.login.http.method;

import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;

public class LoginRequestMethodFactory {

    private static final LoginRequestMethod mAppRequestMethod = RetrofitManager.createMethod(LoginRequestMethod.class);

    public static LoginRequestMethod getMethod() {
        return mAppRequestMethod;
    }

}
