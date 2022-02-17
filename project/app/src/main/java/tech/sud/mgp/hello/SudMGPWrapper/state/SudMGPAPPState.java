package tech.sud.mgp.hello.SudMGPWrapper.state;

/**
 * APP to MG 的通用状态定义
 * 参考文档：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/APP%20FST/%E9%80%9A%E7%94%A8%E7%8A%B6%E6%80%81.md
 */
public class SudMGPAPPState {

    /**
     * 加入状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_IN = "app_common_self_in";

    /**
     * 加入状态模型定义
     */
    public class APPCommonSelfInModel {

        /**
         * true 加入游戏，false 退出游戏
         */
        public boolean isIn;

        /**
         * 加入的游戏位(座位号) 默认传seatIndex = -1 随机加入，seatIndex 从0开始，不可大于座位数
         */
        public int seatIndex;

        /**
         * 默认为ture, 带有游戏位(座位号)的时候，如果游戏位(座位号)已经被占用，是否随机分配一个空位坐下 isSeatRandom=true 随机分配空位坐下，isSeatRandom=false 不随机分配
         */
        public boolean isSeatRandom;

        /**
         * 不支持分队的游戏：数值填1；支持分队的游戏：数值填1或2（两支队伍）；
         */
        public int teamId;
    }

    /**
     * 准备状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_READY = "app_common_self_ready";

    /**
     * 游戏状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_PLAYING = "app_common_self_playing";

    /**
     * 队长状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_CAPTAIN = "app_common_self_captain";

    /**
     * 踢人
     * v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_KICK = "app_common_self_kick";

    /**
     * 结束游戏
     * v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_END = "app_common_self_end";

    /**
     * 房间状态, (depreated 已废弃v1.1.30.xx)
     */
    public static final String APP_COMMON_SELF_ROOM = "app_common_self_room";

    /**
     * 麦位状态, (depreated 已废弃v1.1.30.xx)
     */
    public static final String APP_COMMON_SELF_SEAT = "app_common_self_seat";

    /**
     * 麦克风状态
     */
    public static final String APP_COMMON_SELF_MICROPHONE = "app_common_self_microphone";

    /**
     * 文字命中状态
     */
    public static final String APP_COMMON_SELF_TEXT_HIT = "app_common_self_text_hit";

    /**
     * 打开或关闭背景音乐
     */
    public static final String APP_COMMON_OPEN_BG_MUSIC = "app_common_open_bg_music";

    /**
     * 打开或关闭音效
     */
    public static final String APP_COMMON_OPEN_SOUND = "app_common_open_sound";

    /**
     * 打开或关闭游戏中的振动效果
     */
    public static final String APP_COMMON_OPEN_VIBRATE = "app_common_open_vibrate";

    /**
     * 设置游戏的音量大小
     */
    public static final String APP_COMMON_GAME_SOUND_VOLUME = "app_common_game_sound_volume";

}
