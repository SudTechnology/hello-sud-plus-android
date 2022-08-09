package tech.sud.mgp.hello.service.login.repository;


import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.DeviceUtils;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalSP;
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
     * 将内存中的用户信息写到硬盘缓存当中
     */
    public static void saveUserInfo() {
        GlobalSP.getSP().put(GlobalSP.USER_ID_KEY, HSUserInfo.userId);
        GlobalSP.getSP().put(GlobalSP.USER_HEAD_PORTRAIT_KEY, HSUserInfo.avatar);
        GlobalSP.getSP().put(GlobalSP.USER_NAME_KEY, HSUserInfo.nickName);
        GlobalSP.getSP().put(GlobalSP.USER_TOKEN_KEY, HSUserInfo.token);
        GlobalSP.getSP().put(GlobalSP.USER_REFRESHTOKEN_KEY, HSUserInfo.refreshToken);
        GlobalSP.getSP().put(GlobalSP.USER_HEADER_TYPE_KEY, HSUserInfo.headerType);
        GlobalSP.getSP().put(GlobalSP.USER_HEADER_NFT_TOKEN_KEY, HSUserInfo.headerNftToken);
        GlobalSP.getSP().put(GlobalSP.USER_HEADER_NFT_URL_KEY, HSUserInfo.headerNftUrl);
        GlobalSP.getSP().put(GlobalSP.USER_WALLET_ADDRESS_KEY, HSUserInfo.walletAddress);
    }

    /**
     * 读取本地登陆数据到HSUserInfo
     */
    public static void loadUserInfo() {
        HSUserInfo.userId = GlobalSP.getSP().getLong(GlobalSP.USER_ID_KEY, -1L);
        HSUserInfo.avatar = GlobalSP.getSP().getString(GlobalSP.USER_HEAD_PORTRAIT_KEY);
        HSUserInfo.nickName = GlobalSP.getSP().getString(GlobalSP.USER_NAME_KEY);
        HSUserInfo.token = GlobalSP.getSP().getString(GlobalSP.USER_TOKEN_KEY);
        HSUserInfo.refreshToken = GlobalSP.getSP().getString(GlobalSP.USER_REFRESHTOKEN_KEY);
        HSUserInfo.headerType = GlobalSP.getSP().getInt(GlobalSP.USER_HEADER_TYPE_KEY);
        HSUserInfo.headerNftToken = GlobalSP.getSP().getString(GlobalSP.USER_HEADER_NFT_TOKEN_KEY);
        HSUserInfo.headerNftUrl = GlobalSP.getSP().getString(GlobalSP.USER_HEADER_NFT_URL_KEY);
        HSUserInfo.walletAddress = GlobalSP.getSP().getString(GlobalSP.USER_WALLET_ADDRESS_KEY);
    }

}
