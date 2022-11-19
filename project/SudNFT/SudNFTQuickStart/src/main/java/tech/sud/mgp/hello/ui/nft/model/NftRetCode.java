package tech.sud.mgp.hello.ui.nft.model;

public class NftRetCode {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;
    public static final int WALLET_TOKEN_INVALID = 13008;// 钱包令牌无效
    public static final int NFT_DETAILS_TOKEN_EXPIRE = 13019;// NFT详情令牌过期
    public static final int WALLET_TYPE_NOT_SUPPORT = 13020;// 钱包类型不支持
    public static final int SEND_PHONE_CODE_FAILED = 13021;// 发送验证码失败
    public static final int BIND_USER_FAILED = 13022;// 绑定用户失败
    public static final int CARD_LIST_GET_FAILED = 13023;// 藏品列表获取失败
    public static final int CARD_GET_FAILED = 13024;// 藏品信息获取失败
    public static final int CARD_NOT_BELONG_TO_USER = 13025;// 藏品不属于用户
    public static final int SMS_TOO_FREQUENT = 13026;// 短信验证码发送太频繁
    public static final int PHONE_NUMBER_HAS_BEEN_BOUND = 13027;// 手机号已被绑定
    public static final int PHONE_NUMBER_BELONG_TO_OTHERS_USER = 13028;// 该手机号已被其他用户绑定
    public static final int USER_UNBIND_FAILED = 13029;// 用户解绑失败
    public static final int SMS_CODE_INVALID = 13030;// 短信验证码错误
    public static final int USER_HAS_BOUND_MOBILE_PHONE_NUMBER = 13031;// 用户已绑定手机号
    public static final int PHONE_NUMBER_IS_NOT_BOUND_BY_THE_USER = 13032;// 该手机号不是该用户绑定的
    public static final int WALLET_BOUND = 13033;// 钱包已绑定
    public static final int WALLET_ADDRESS_INVALID = 13034;// 钱包地址无效
    public static final int VERIFY_CODE_TIP = 13035;// 您的验证码是1234	验证码提示
}
