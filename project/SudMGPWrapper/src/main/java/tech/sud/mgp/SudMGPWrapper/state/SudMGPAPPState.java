/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.SudMGPWrapper.state;

import java.io.Serializable;
import java.util.List;

/**
 * APP to MG 的通用状态定义
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/APPFST/
 */
public class SudMGPAPPState implements Serializable {

    // region 通用状态
    /**
     * 1. 加入状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_IN = "app_common_self_in";

    /**
     * 1. 加入状态 模型
     * 用户（本人）加入游戏/退出游戏
     * 正确流程：
     * 1.isIn=true: 加入游戏=>准备游戏=>开始游戏;
     * 2.isIn=false: 结束=>取消准备=>退出游戏;
     */
    public static class APPCommonSelfIn implements Serializable {
        // rue 加入游戏，false 退出游戏
        public boolean isIn;

        // 加入的游戏位(座位号) 默认传seatIndex = -1 随机加入，seatIndex 从0开始，不可大于座位数
        public int seatIndex;

        // 默认为ture, 带有游戏位(座位号)的时候，如果游戏位(座位号)已经被占用，是否随机分配一个空位坐下 isSeatRandom=true 随机分配空位坐下，isSeatRandom=false 不随机分配
        public boolean isSeatRandom;

        // 不支持分队的游戏：数值填1；支持分队的游戏：数值填1或2（两支队伍）；
        public int teamId;
    }

    /**
     * 2. 准备状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_READY = "app_common_self_ready";

    /**
     * 2. 准备状态 模型
     * 用户（本人）准备/取消准备
     */
    public static class APPCommonSelfReady implements Serializable {
        // true 准备，false 取消准备
        public boolean isReady;
    }

    /**
     * 3. 游戏状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_PLAYING = "app_common_self_playing";

    /**
     * 3. 游戏状态 模型
     * 用户游戏状态，如果用户在游戏中，建议：
     * a.空出屏幕中心区：
     * 关闭全屏礼物特效；
     * b.部分强操作类小游戏（spaceMax为true），尽量收缩原生UI，给游戏留出尽量大的操作空间：
     * 收缩公屏；
     * 收缩麦位；
     * 如果不在游戏中，则恢复。
     */
    public static class APPCommonSelfPlaying implements Serializable {
        // true 开始游戏，false 结束游戏
        public boolean isPlaying;

        // string类型，Https服务回调report_game_info参数，最大长度1024字节，超过则截断（2022-01-21）
        public String reportGameInfoExtras;
    }

    /**
     * 4. 队长状态
     * 最低版本: v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_CAPTAIN = "app_common_self_captain";

    /**
     * 4. 队长状态 模型
     * 用户是否为队长，队长在游戏中会有开始游戏的权利。
     */
    public static class APPCommonSelfCaptain implements Serializable {
        // 必填，指定队长uid
        public String curCaptainUID;
    }

    /**
     * 5. 踢人
     * v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_KICK = "app_common_self_kick";

    /**
     * 5. 踢人 模型
     * 用户（本人，队长）踢其他玩家；
     * 队长才能踢人；
     */
    public static class APPCommonSelfKick implements Serializable {
        // 被踢用户uid
        public String kickedUID;
    }

    /**
     * 6. 结束游戏
     * v1.1.30.xx
     */
    public static final String APP_COMMON_SELF_END = "app_common_self_end";

    /**
     * 6. 结束游戏 模型
     * 用户（本人，队长）结束（本局）游戏
     */
    public static class APPCommonSelfEnd implements Serializable {
        // 当前不需要传参
    }

    /**
     * 7. 房间状态（depreated 已废弃v1.1.30.xx）
     */
    public static final String APP_COMMON_SELF_ROOM = "app_common_self_room";

    /**
     * 8. 麦位状态（depreated 已废弃v1.1.30.xx）
     */
    public static final String APP_COMMON_SELF_SEAT = "app_common_self_seat";

    /**
     * 9. 麦克风状态
     */
    public static final String APP_COMMON_SELF_MICROPHONE = "app_common_self_microphone";

    /**
     * 9. 麦克风状态 模型
     * 用户（本人）麦克风状态，建议：
     * 进入房间后初始通知一次；
     * 每次变更（开麦/闭麦/禁麦/解麦）通知一次；
     */
    public static class APPCommonSelfMicrophone implements Serializable {
        // true 开麦，false 闭麦
        public boolean isOn;

        // true 被禁麦，false 未被禁麦
        public boolean isDisabled;
    }

    /**
     * 10. 文字命中状态
     */
    public static final String APP_COMMON_SELF_TEXT_HIT = "app_common_self_text_hit";

    /**
     * 10. 文字命中状态 模型
     * 用户（本人）聊天信息命中关键词状态，建议：
     * 精确匹配；
     * 首次聊天内容命中关键词之后，后续聊天内容不翻转成未命中；
     * 直至小游戏侧关键词更新，再将状态翻转为未命中；
     */
    public static class APPCommonSelfTextHitState implements Serializable {
        // true 命中，false 未命中
        public boolean isHit;

        // 单个关键词， 兼容老版本
        public String keyWord;

        // 返回转写文本
        public String text;

        // text:文本包含匹配; number:数字等于匹配
        public String wordType;

        // 命中关键词，可以包含多个关键词
        public List<String> keyWordList;

        // 在number模式下才有，返回转写的多个数字
        public List<Integer> numberList;
    }

    /**
     * 11. 打开或关闭背景音乐（2021-12-27新增）
     */
    public static final String APP_COMMON_OPEN_BG_MUSIC = "app_common_open_bg_music";

    /**
     * 11. 打开或关闭背景音乐（2021-12-27新增） 模型
     */
    public static class APPCommonOpenBgMusic implements Serializable {
        // true 打开背景音乐，false 关闭背景音乐
        public boolean isOpen;
    }

    /**
     * 12. 打开或关闭音效（2021-12-27新增）
     */
    public static final String APP_COMMON_OPEN_SOUND = "app_common_open_sound";

    /**
     * 12. 打开或关闭音效（2021-12-27新增） 模型
     */
    public static class APPCommonOpenSound implements Serializable {
        // true 打开音效，false 关闭音效
        public boolean isOpen;
    }

    /**
     * 13. 打开或关闭游戏中的振动效果（2021-12-27新增）
     */
    public static final String APP_COMMON_OPEN_VIBRATE = "app_common_open_vibrate";

    /**
     * 13. 打开或关闭游戏中的振动效果（2021-12-27新增）模型
     */
    public static class APPCommonOpenVibrate implements Serializable {
        // true 打开振动效果，false 关闭振动效果
        public boolean isOpen;
    }

    /**
     * 14. 设置游戏的音量大小（2021-12-31新增）
     */
    public static final String APP_COMMON_GAME_SOUND_VOLUME = "app_common_game_sound_volume";

    /**
     * 14. 设置游戏的音量大小（2021-12-31新增）模型
     */
    public static class APPCommonGameSoundVolume implements Serializable {
        // 音量大小 0 到 100
        public int volume;
    }

    /**
     * 15.  设置游戏玩法选项（2022-05-10新增）
     */
    public static final String APP_COMMON_GAME_SETTING_SELECT_INFO = "app_common_game_setting_select_info";

    /**
     * 15.  设置游戏玩法选项（2022-05-10新增） 模型
     */
    public static class APPCommonGameSettingSelectInfo implements Serializable {
        public Ludo ludo; // 游戏名称
    }

    public static class Ludo implements Serializable {
        public int mode; // mode: 默认赛制，0: 快速, 1: 经典;
        public int chessNum; // chessNum: 默认棋子数量, 2: 对应2颗棋子; 4: 对应4颗棋子;
        public int item; // item: 默认道具, 1: 有道具, 0: 没有道具
    }

    /**
     * 16. 设置游戏中的AI玩家（2022-05-11新增）
     */
    public static final String APP_COMMON_GAME_ADD_AI_PLAYERS = "app_common_game_add_ai_players";

    /**
     * 16. 设置游戏中的AI玩家（2022-05-11新增） 模型
     */
    public static class APPCommonGameAddAIPlayers implements Serializable {
        public List<AIPlayers> aiPlayers; // AI玩家
        public int isReady = 1; // 机器人加入后是否自动准备 1：自动准备，0：不自动准备 默认为1
    }

    public static class AIPlayers implements Serializable {
        public String userId; // 玩家id
        public String avatar; // 头像url
        public String name; // 名字
        public String gender; // 性别 male：男，female：女
    }

    /**
     * 17. app在收到游戏断开连接通知后，通知游戏重试连接（2022-06-21新增，暂时支持ludo）
     */
    public static final String APP_COMMON_GAME_RECONNECT = "app_common_game_reconnect";

    /**
     * 17. app在收到游戏断开连接通知后，通知游戏重试连接（2022-06-21新增，暂时支持ludo） 模型
     */
    public static class APPCommonGameReconnect implements Serializable {
    }
    // endregion 通用状态

    // region 元宇宙砂砂舞
    /**
     * 1. 元宇宙砂砂舞相关设置
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/APPFST/CommonStateForDisco.html
     */
    public static final String APP_COMMON_GAME_DISCO_ACTION = "app_common_game_disco_action";

    /**
     * 1. 元宇宙砂砂舞相关设置 模型
     */
    public static class AppCommonGameDiscoAction implements Serializable {
        public int actionId; // 必传的参数，用于指定类型的序号，不同序号用于区分游戏内的不同功能，不传则会判断为无效指令，具体序号代表的功能见下表
        public Integer cooldown; // 持续时间，单位秒，部分功能有持续时间就需要传对应的数值，不传或传错则会按各自功能的默认值处理（见下表）
        public Boolean isTop; // 是否置顶，针对部分功能可排队置顶（false：不置顶；true：置顶；默认为false）
        public String field1; // 额外参数1，针对部分功能有具体的意义
        public String field2; // 额外参数2，针对部分功能有具体的意义
    }
    // endregion 元宇宙砂砂舞

}
