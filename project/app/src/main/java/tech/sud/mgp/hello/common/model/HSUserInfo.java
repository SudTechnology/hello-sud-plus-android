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

    // 保存静态数据
    public static void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong("userId", HSUserInfo.userId);
        outState.putString("nickName", HSUserInfo.nickName);
        outState.putString("gender", HSUserInfo.gender);
        outState.putString("token", HSUserInfo.token);
        outState.putString("avatar", HSUserInfo.avatar);
    }

    // 恢复静态数据
    public static void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        HSUserInfo.userId = savedInstanceState.getLong("userId");
        HSUserInfo.nickName = savedInstanceState.getString("nickName");
        HSUserInfo.gender = savedInstanceState.getString("gender");
        HSUserInfo.token = savedInstanceState.getString("token");
        HSUserInfo.avatar = savedInstanceState.getString("avatar");
    }

}