package tech.sud.mgp.hello.common.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * 全局使用的SharedPreferences
 */
public class AppSharedPreferences {
    private final static String SP_NAME = "global.sp";

    public final static String AGREEMENT_STATE = "agreement_state";
    public final static String USER_ID_KEY = "user_id_key";
    public final static String USER_SEX_KEY = "user_sex_key";
    public final static String USER_NAME_KEY = "user_name_key";
    public final static String USER_HEAD_PORTRAIT_KEY = "user_head_portrait_key";
    /** 记录当前设置的语言类型 */
    public final static String KEY_LANG_CELL_TYPE = "key_lang_cell_type";
    public final static String IS_SHOW_MORE_SETTINGS = "is_show_more_settings"; // 是否要显示更多设置

    public static SPUtils getSP() {
        return SPUtils.getInstance(SP_NAME);
    }
}
