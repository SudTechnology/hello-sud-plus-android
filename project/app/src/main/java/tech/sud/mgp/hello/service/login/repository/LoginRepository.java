package tech.sud.mgp.hello.service.login.repository;


import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.DeviceUtils;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.service.login.method.LoginRequestMethodFactory;
import tech.sud.mgp.hello.service.login.req.LoginRequestBody;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;

public class LoginRepository {

    public static void login(Long userId, String name, LifecycleOwner owner, RxCallback<LoginResponse> callback) {
        LoginRequestBody loginRequestBody = new LoginRequestBody();
        loginRequestBody.deviceId = DeviceUtils.getUniqueDeviceId();
        loginRequestBody.nickname = name;
        if (userId != null) {
            loginRequestBody.userId = userId;
        }
        LoginRequestMethodFactory.getMethod()
                .login(BaseUrlManager.getBaseUrl(), loginRequestBody)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
