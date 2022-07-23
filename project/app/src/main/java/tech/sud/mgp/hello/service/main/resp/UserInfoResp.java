package tech.sud.mgp.hello.service.main.resp;

import com.google.gson.annotations.SerializedName;

public class UserInfoResp {
    public long userId;
    public String avatar; // 头像
    public String nickname; // 昵称
    public String gender; // 性别
    @SerializedName(value = "ai")
    public boolean isAi; // 是否是机器人
}
