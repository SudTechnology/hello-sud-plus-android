package tech.sud.mgp.hello.login.http.req;

public class LoginRequestBody {
    public long userId;
    public long deviceId;
    public long nickname;
    public long gender; // 性别 (FEMALE: 女性, MALE: 男性)
}
