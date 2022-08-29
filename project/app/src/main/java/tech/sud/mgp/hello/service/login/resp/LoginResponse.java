package tech.sud.mgp.hello.service.login.resp;

public class LoginResponse {
    public String avatar;
    public long userId;
    public String nickname;
    public String token;
    public String refreshToken;
    public int headerType; // 头像类型（0图片，1nft）
    public String headerNftToken; // 头像nft穿戴token
    public String headerNftUrl; // 头像nft图片
}