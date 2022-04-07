package tech.sud.mgp.hello.service.login.repository;


import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.DeviceUtils;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.service.login.method.LoginRequestMethodFactory;
import tech.sud.mgp.hello.service.login.req.LoginRequestBody;
import tech.sud.mgp.hello.service.login.req.RefreshTokenRequestBody;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;
import tech.sud.mgp.hello.service.login.resp.RefreshTokenResponse;

public class LoginRepository {

    /**
     * 发送登录请求
     */
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

    /**
     * 发送刷新token的请求
     *
     * @param refreshToken 刷新的token
     */
    public static void refreshToken(String refreshToken, LifecycleOwner owner, RxCallback<RefreshTokenResponse> callback) {
        RefreshTokenRequestBody refreshTokenRequestBody = new RefreshTokenRequestBody();
        refreshTokenRequestBody.refreshToken = refreshToken;
        LoginRequestMethodFactory.getMethod()
                .refreshToken(BaseUrlManager.getBaseUrl(), refreshTokenRequestBody)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 用户登陆成功后保存数据
     */
    public static void saveLoginData(LoginResponse response) {
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_ID_KEY, response.userId);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, response.avatar);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_NAME_KEY, response.nickname);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_TOKEN_KEY, response.token);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_REFRESHTOKEN_KEY, response.refreshToken);
    }

    /**
     * 刷新token
     */
    public static void saveRefreshToken(RefreshTokenResponse response) {
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_TOKEN_KEY, response.token);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_REFRESHTOKEN_KEY, response.refreshToken);
        HSUserInfo.token = response.token;
        HSUserInfo.refreshToken = response.refreshToken;
    }

    /**
     * 读取本地登陆数据到HSUserInfo
     */
    public static void createUserInfo() {
        HSUserInfo.userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, -1L);
        HSUserInfo.avatar = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY);
        HSUserInfo.nickName = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY);
        HSUserInfo.token = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_TOKEN_KEY);
        HSUserInfo.refreshToken = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_REFRESHTOKEN_KEY);
    }

}
