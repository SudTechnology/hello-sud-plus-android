package tech.sud.mgp.hello.ui.game.middle.state;

/**
 * MG to APP 的状态定义
 */
public class SudMGPMGState {

    // region MG状态机-通用状态-游戏
    // 参考：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/MG%20FSM/%E9%80%9A%E7%94%A8%E7%8A%B6%E6%80%81-%E6%B8%B8%E6%88%8F.md
    /**
     * 公屏消息  （已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PUBLIC_MESSAGE = "mg_common_public_message";

    /**
     * 关键词状态
     */
    public static final String MG_COMMON_KEY_WORD_TO_HIT = "mg_common_key_word_to_hit";

    /**
     * 加入游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_JOIN_BTN = "mg_common_self_click_join_btn";

    /**
     * ASR状态(开启和关闭语音识别状态)
     */
    public static final String MG_COMMON_GAME_ASR = "mg_common_game_asr";

    /**
     * 取消加入游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN = "mg_common_self_click_cancel_join_btn";

    /**
     * 准备按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_READY_BTN = "mg_common_self_click_ready_btn";

    /**
     * 取消准备按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_CANCEL_READY_BTN = "mg_common_self_click_cancel_ready_btn";

    /**
     * 开始游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_START_BTN = "mg_common_self_click_start_btn";

    /**
     * 游戏状态
     */
    public static final String MG_COMMON_GAME_STATE = "mg_common_game_state";

    // endregion 通用状态-游戏


    // region MG状态机-通用状态-玩家
    // 参考：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/MG%20FSM/%E9%80%9A%E7%94%A8%E7%8A%B6%E6%80%81-%E7%8E%A9%E5%AE%B6.md

    /**
     * 加入状态 （已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_IN = "mg_common_player_in";

    /**
     * 准备状态 （已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_READY = "mg_common_player_ready";

    /**
     * 队长状态 （已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_CAPTAIN = "mg_common_player_captain";

    /**
     * 游戏状态 （已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_PLAYING = "mg_common_player_playing";

    // endregion 通用状态-玩家


    // region 碰碰我最强
    // endregion 碰碰我最强

    // region 飞刀达人
    // endregion 飞刀达人

    // region 你画我猜
    // 参考：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/MG%20FSM/%E4%BD%A0%E7%94%BB%E6%88%91%E7%8C%9C.md

    /**
     * 选词中
     */
    public static final String MG_DG_SELECTING = "mg_dg_selecting";

    /**
     * 作画中
     */
    public static final String MG_DG_PAINTING = "mg_dg_painting";

    /**
     * 错误答案
     */
    public static final String MG_DG_ERRORANSWER = "mg_dg_erroranswer";

    /**
     * 总积分
     */
    public static final String MG_DG_TOTALSCORE = "mg_dg_totalscore";

    /**
     * 本次积分
     */
    public static final String MG_DG_SCORE = "mg_dg_score";

    // endregion 你画我猜

}
