package tech.sud.mgp.hello.service.login.repository;


import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalSP;

public class LoginRepository {

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
        GlobalSP.getSP().put(GlobalSP.KEY_USER_HEADER_NFT_URL, HSUserInfo.headerNftUrl);
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
        HSUserInfo.headerNftUrl = GlobalSP.getSP().getString(GlobalSP.KEY_USER_HEADER_NFT_URL);
    }

}
