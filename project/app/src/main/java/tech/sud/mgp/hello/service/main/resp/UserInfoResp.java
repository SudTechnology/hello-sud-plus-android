package tech.sud.mgp.hello.service.main.resp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoResp implements Serializable {
    public long userId; // 用户id
    public String avatar; // 头像
    public String nickname; // 昵称
    public String gender; // 性别
    @SerializedName(value = "ai")
    public boolean isAi; // 是否是机器人

    public int headerType; // 头像类型（0图片，1nft）
    public String headerNftToken; // 头像nft穿戴token
    public String headerNftUrl; // 头像nft图片

    public int index; // 位置
    public boolean isCaptain; // 是否是队长

    public String getUseAvatar() {
        if (headerType == 1) {
            return headerNftUrl;
        } else {
            return avatar;
        }
    }

    public void clearUser() {
        userId = -1;
        avatar = null;
        nickname = null;
        gender = null;
        isAi = false;
        headerType = 0;
        headerNftToken = null;
        headerNftUrl = null;
        isCaptain = false;
    }

    public void setUserInfo(UserInfoResp userInfoResp) {
        userId = userInfoResp.userId;
        avatar = userInfoResp.avatar;
        nickname = userInfoResp.nickname;
        gender = userInfoResp.gender;
        isAi = userInfoResp.isAi;
        headerType = userInfoResp.headerType;
        headerNftToken = userInfoResp.headerNftToken;
        headerNftUrl = userInfoResp.headerNftUrl;
    }

}
