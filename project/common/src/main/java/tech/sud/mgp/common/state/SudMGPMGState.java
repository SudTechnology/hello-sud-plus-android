package tech.sud.mgp.hello.state;

/**
 * Time:2021/10/19
 * Description: MG to APP 的状态定义
 */
public class SudMGPMGState {

    // region 通用状态-游戏

    /**
     * 公屏消息
     */
    public static final String MG_COMMON_PUBLIC_MESSAGE = "mg_common_public_message";

    /**
     * 关键词状态
     */
    public static final String MG_COMMON_KEY_WORD_TO_HIT = "mg_common_key_word_to_hit";

    // endregion 通用状态-游戏


    // region 通用状态-玩家

    /**
     * 加入状态
     */
    public static final String MG_COMMON_PLAYER_IN = "mg_common_player_in";

    /**
     * 准备状态
     */
    public static final String MG_COMMON_PLAYER_READY = "mg_common_player_ready";

    /**
     * 队长状态
     */
    public static final String MG_COMMON_PLAYER_CAPTAIN = "mg_common_player_captain";

    /**
     * 游戏状态
     */
    public static final String MG_COMMON_PLAYER_PLAYING = "mg_common_player_playing";

    // endregion 通用状态-玩家


    // region 碰碰我最强
    // endregion 碰碰我最强

    // region 飞刀达人
    // endregion 飞刀达人

    // region 你画我猜

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
