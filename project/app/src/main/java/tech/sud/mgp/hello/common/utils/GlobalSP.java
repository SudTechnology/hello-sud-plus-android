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

    public final static String KEY_LANG_CELL_TYPE = "key_lang_cell_type"; // 记录当前设置的语言类型
    public final static String KEY_SHOW_GUIDE_UPGRADE_TIMESTAMP = "key_show_guide_upgrade_timestamp"; // 记录上一次显示引导更新的时间戳

    public final static String KEY_USER_HEADER_NFT_URL = "key_user_header_nft_url_v1"; // 登录的头像nft图片
    public final static String USER_HEADER_TYPE_KEY = "user_header_type_key_v1"; // 登录的头像类型
    public static final String KEY_NFT_BIND_WALLET = "key_nft_bind_wallet_v1"; // 已绑定的钱包信息
    public final static String KEY_SHOWN_CHANGE_ADDRESS_GUIDE = "key_shown_change_address_guide_v1"; // 是否已经展示过切换地址的引导
    public final static String KEY_SHOWN_CHANGE_NETWORK_GUIDE = "key_shown_change_network_guide_v1"; // 是否已经展示过切换网络的引导

    public final static String KEY_ROCKET_THUMB_PATH = "key_rocket_thumb_path"; // 火箭缩略图地址

    public final static String KEY_BASE_URL_CONFIG = "key_base_url_config"; // 当前环境标识
    public final static String KEY_LLM_MY_VOICE_PATH_JSON = "key_llm_my_voice_path"; // llmbot我的声音文件路径

    public static SPUtils getSP() {
        return SPUtils.getInstance(SP_NAME);
    }

}
