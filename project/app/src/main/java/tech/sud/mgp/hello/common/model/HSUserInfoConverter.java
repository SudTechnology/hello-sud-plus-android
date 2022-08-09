package tech.sud.mgp.hello.common.model;

import tech.sud.mgp.hello.service.login.resp.LoginResponse;
import tech.sud.mgp.hello.service.login.resp.RefreshTokenResponse;

public class HSUserInfoConverter {

    // 将登录返回的数据，写到内存当中
    public static void conver(LoginResponse resp) {
        if (resp == null) return;
        HSUserInfo.userId = resp.userId;
        HSUserInfo.nickName = resp.nickname;
        HSUserInfo.avatar = resp.avatar;
        HSUserInfo.token = resp.token;
        HSUserInfo.refreshToken = resp.refreshToken;
        HSUserInfo.headerType = resp.headerType;
        HSUserInfo.headerNftToken = resp.headerNftToken;
        HSUserInfo.headerNftUrl = resp.headerNftUrl;
    }

    // 将刷新token返回的数据，写到内存当中
    public static void conver(RefreshTokenResponse resp) {
        if (resp == null) return;
        HSUserInfo.token = resp.token;
        HSUserInfo.refreshToken = resp.refreshToken;
    }

}
