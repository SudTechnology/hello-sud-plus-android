package tech.sud.mgp.hello.common.model;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * 存储全局使用的登录用户信息
 */
public class HSUserInfo {
    public static long userId;
    public static String nickName;
    public static String gender; //male为男性，female为女性
    public static String token;
    public static String avatar;
    public static String refreshToken;

    public static int headerType; // 头像类型（0图片，1nft）
    public static String headerNftToken; // 头像nft穿戴token
    public static String headerNftUrl; // 头像nft图片
    public static String walletAddress; // 钱包地址
    public static int zoneType; // 绑定的钱包区域 0国外 1国内

    public static String getUseAvatar() {
        if (HSUserInfo.headerType == 1) {
            return headerNftUrl;
        } else {
            return avatar;
        }
    }

    // 保存静态数据
    public static void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("userId", HSUserInfo.userId);
        outState.putString("nickName", HSUserInfo.nickName);
        outState.putString("gender", HSUserInfo.gender);
        outState.putString("token", HSUserInfo.token);
        outState.putString("avatar", HSUserInfo.avatar);
        outState.putString("refreshToken", HSUserInfo.refreshToken);
        outState.putInt("headerType", HSUserInfo.headerType);
        outState.putString("headerNftToken", HSUserInfo.headerNftToken);
        outState.putString("headerNftUrl", HSUserInfo.headerNftUrl);
        outState.putString("walletAddress", HSUserInfo.walletAddress);
        outState.putInt("zoneType", HSUserInfo.zoneType);
    }

    // 恢复静态数据
    public static void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        HSUserInfo.userId = savedInstanceState.getLong("userId");
        HSUserInfo.nickName = savedInstanceState.getString("nickName");
        HSUserInfo.gender = savedInstanceState.getString("gender");
        HSUserInfo.token = savedInstanceState.getString("token");
        HSUserInfo.avatar = savedInstanceState.getString("avatar");
        HSUserInfo.refreshToken = savedInstanceState.getString("refreshToken");
        HSUserInfo.headerType = savedInstanceState.getInt("headerType");
        HSUserInfo.headerNftToken = savedInstanceState.getString("headerNftToken");
        HSUserInfo.headerNftUrl = savedInstanceState.getString("headerNftUrl");
        HSUserInfo.walletAddress = savedInstanceState.getString("walletAddress");
        HSUserInfo.zoneType = savedInstanceState.getInt("zoneType");
    }

    // 返回是否已登录
    public static boolean isLogin() {
        return userId != -1;
    }

}