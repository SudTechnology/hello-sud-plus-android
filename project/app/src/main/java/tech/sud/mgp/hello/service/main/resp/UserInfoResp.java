package tech.sud.mgp.hello.service.main.resp;

import com.google.gson.annotations.SerializedName;

public class UserInfoResp {
    public long userId;
    public String avatar; // 头像
    public String nickname; // 昵称
    public String gender; // 性别
    @SerializedName(value = "ai")
    public boolean isAi; // 是否是机器人

    public int headerType; // 头像类型（0图片，1nft）
    public String headerNftToken; // 头像nft穿戴token
    public String headerNftUrl; // 头像nft图片

    public String getUseAvatar() {
        if (headerType == 1) {
            return headerNftUrl;
        } else {
            return avatar;
        }
    }

}
