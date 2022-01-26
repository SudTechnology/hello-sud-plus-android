package tech.sud.mgp.hello.login.http.req;

public class LoginRequestBody {
    public Long userId;
    public String deviceId;
    public String nickname;
    public String gender; // 性别 (FEMALE: 女性, MALE: 男性)
}
