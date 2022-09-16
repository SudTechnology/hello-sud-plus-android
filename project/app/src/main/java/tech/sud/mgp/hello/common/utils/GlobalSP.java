package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * 全局使用的SharedPreferences
 */
public class GlobalSP {
    private final static String SP_NAME = "global.sp";

    public final static String AGREEMENT_STATE = "agreement_state"; // 是否已经同意了隐私政策
    public final static String USER_ID_KEY = "user_id_key"; // 登录用户id
    public final static String USER_NAME_KEY = "user_name_key"; // 登录用户名称
    public final static String USER_TOKEN_KEY = "user_token_key"; // 登录用户Token
    public final static String USER_REFRESHTOKEN_KEY = "user_refreshtoken_key"; // 登录刷新Token
    public final static String USER_HEAD_PORTRAIT_KEY = "user_head_portrait_key"; // 登录的用户头像
    public final static String USER_HEADER_TYPE_KEY = "user_header_type_key"; // 登录的头像类型

    public final static String KEY_USER_HEADER_NFT_URL = "key_user_header_nft_url"; // 登录的头像nft图片

    public final static String KEY_LANG_CELL_TYPE = "key_lang_cell_type"; // 记录当前设置的语言类型
    public final static String KEY_SHOW_GUIDE_UPGRADE_TIMESTAMP = "key_show_guide_upgrade_timestamp"; // 记录上一次显示引导更新的时间戳

    public static final String KEY_NFT_BIND_WALLET = "key_nft_bind_wallet"; // 已绑定的钱包信息

    public static SPUtils getSP() {
        return SPUtils.getInstance(SP_NAME);
    }
}
