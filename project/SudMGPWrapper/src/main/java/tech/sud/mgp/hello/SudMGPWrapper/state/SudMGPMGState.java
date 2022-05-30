/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.SudMGPWrapper.state;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * MG to APP 的状态定义
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/
 */
public class SudMGPMGState {

    // region MG状态机-通用状态-游戏
    // 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStateGame.html
    /**
     * 1. 公屏消息（已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PUBLIC_MESSAGE = "mg_common_public_message";

    /**
     * 1. 公屏消息（已修改）
     * 向公屏发送消息，字段含义如下
     * type
     * 0 通知
     * 1 提醒
     * 2 结算
     * 3 其他
     * msg
     * <!-- -->内为转义字段：
     * <!--name:用户昵称|uid:用户UID|color:建议颜色-->
     * 其中name/uid/color均为可选字段，字段为空的情况如下：
     * <!--name:|uid:|color:-->
     * SDK仅会缓存最新一条。
     */
    public static class MGCommonPublicMessage {
        // 0 通知
        // 1 提醒
        // 2 结算
        // 3 其他
        public int type;

        // 消息内容
        public List<MGCommonPublicMessageMsg> msg;

        public static class MGCommonPublicMessageMsg {
            // 词组类型 当phrase=1时，会返回text; 当phrase=2时，会返回user
            public int phrase;
            public MGCommonPublicMessageMsgText text;
            public MGCommonPublicMessageMsgUser user;
        }

        public static class MGCommonPublicMessageMsgText {
            @SerializedName(value = "default")
            public String defaultStr; // 默认文本

            @SerializedName(value = "zh-CN")
            public String zh_CN; // 中文(简体)

            @SerializedName(value = "zh-HK")
            public String zh_HK; // 中文(香港)

            @SerializedName(value = "zh-MO")
            public String zh_MO; // 中文(澳门)

            @SerializedName(value = "zh-SG")
            public String zh_SG; // 中文(新加坡)

            @SerializedName(value = "zh-TW")
            public String zh_TW; // 中文(繁体)

            @SerializedName(value = "en-US")
            public String en_US; // 英语(美国)

            @SerializedName(value = "en-GB")
            public String en_GB; // 英语(英国)

            @SerializedName(value = "ms-BN")
            public String ms_BN; // 马来语(文莱达鲁萨兰)

            @SerializedName(value = "ms-MY")
            public String ms_MY; // 马来语(马来西亚)

            @SerializedName(value = "vi-VN")
            public String vi_VN; // 越南语

            @SerializedName(value = "id-ID")
            public String id_ID; // 印度尼西亚语

            @SerializedName(value = "es-ES")
            public String es_ES; // 西班牙语(传统)

            @SerializedName(value = "ja-JP")
            public String ja_JP; // 日语

            @SerializedName(value = "ko-KR")
            public String ko_KR; // 朝鲜语

            @SerializedName(value = "th-TH")
            public String th_TH; // 泰语

            @SerializedName(value = "ar-SA")
            public String ar_SA; // 阿拉伯语(沙特阿拉伯)

            @SerializedName(value = "ur-PK")
            public String ur_PK; // 乌都语

            @SerializedName(value = "tr-TR")
            public String tr_TR; // 土耳其语
        }

        public static class MGCommonPublicMessageMsgUser {
            // 默认内容
            public String defaultStr;
            // 用户名称
            public String name;
            // 用户id
            public String uid;
            // 颜色值
            public String color;
        }
    }

    /**
     * 2. 关键词状态
     */
    public static final String MG_COMMON_KEY_WORD_TO_HIT = "mg_common_key_word_to_hit";

    /**
     * 2. 关键词状态
     */
    public static class MGCommonKeyWordToHit {
        // 必填字段；text:文本包含匹配; number:数字等于匹配(必填字段)；默认:text（你画我猜、你说我猜）；数字炸弹填number；
        public String wordType;

        // 单个关键词，兼容老版本
        public String word;

        // 必填字段；关键词列表，可以传送多个关键词
        public List<String> wordList;

        // 必填字段；关键词语言，默认:zh-CN；
        public String wordLanguage;
    }

    /**
     * 3. 游戏结算状态
     */
    public static final String MG_COMMON_GAME_SETTLE = "mg_common_game_settle";

    /**
     * 3. 游戏结算状态
     */
    public static class MGCommonGameSettle {
        // 游戏模式默认为1
        public int gameMode;

        // 本局游戏的id
        public String gameRoundId;

        // 游戏结果玩家列表
        public List<PlayerResult> results;

        /**
         * 游戏结果玩家定义
         */
        public static class PlayerResult {
            // 用户id
            public String uid;
            // 排名 从 1 开始
            public int rank;
            // 奖励
            public int award;
            // 积分
            public int score;
            // 是否逃跑 1：逃跑 0：非逃跑
            public int isEscaped;
            // 杀自己的玩家的id
            public String killerId;
        }
    }

    /**
     * 4. 加入游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_JOIN_BTN = "mg_common_self_click_join_btn";

    /**
     * 4. 加入游戏按钮点击状态 模型
     * 用户（本人）点击加入按钮，或者点击头像加入
     */
    public static class MGCommonSelfClickJoinBtn {
        // 点击头像加入游戏对应的座位号，int 类型，从0开始， 如果seatIndex=-1，则是随机加入一个空位，如果seatIndex 大于座位数，则加入不成功
        public int seatIndex;
    }

    /**
     * 5. 取消加入(退出)游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN = "mg_common_self_click_cancel_join_btn";

    /**
     * 5. 取消加入(退出)游戏按钮点击状态 模型
     * 用户（本人）点击取消加入按钮
     */
    public static class MGCommonSelfClickCancelJoinBtn {
    }

    /**
     * 6. 准备按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_READY_BTN = "mg_common_self_click_ready_btn";

    /**
     * 6. 准备按钮点击状态 模型
     */
    public static class MGCommonSelfClickReadyBtn {
    }

    /**
     * 7. 取消准备按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_CANCEL_READY_BTN = "mg_common_self_click_cancel_ready_btn";

    /**
     * 7. 取消准备按钮点击状态 模型
     */
    public static class MGCommonSelfClickCancelReadyBtn {
    }

    /**
     * 8. 开始游戏按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_START_BTN = "mg_common_self_click_start_btn";

    /**
     * 8. 开始游戏按钮点击状态 模型
     */
    public static class MGCommonSelfClickStartBtn {
    }

    /**
     * 9. 分享按钮点击状态
     */
    public static final String MG_COMMON_SELF_CLICK_SHARE_BTN = "mg_common_self_click_share_btn";

    /**
     * 9. 分享按钮点击状态 模型
     * 用户（本人）点击分享按钮
     */
    public static class MGCommonSelfClickShareBtn {
    }

    /**
     * 10. 游戏状态
     */
    public static final String MG_COMMON_GAME_STATE = "mg_common_game_state";

    /**
     * 10. 游戏状态 模型
     */
    public static class MGCommonGameState {
        public static final int UNKNOW = -1; // 未知
        public static final int IDLE = 0;
        public static final int LOADING = 1;
        public static final int PLAYING = 2;

        // gameState=0 (idle 状态，游戏未开始，空闲状态）；
        // gameState=1（loading 状态，所有玩家都准备好，队长点击了开始游戏按钮，等待加载游戏场景开始游戏，游戏即将开始提示阶段）；
        // gameState=2（playing状态，游戏进行中状态）
        public int gameState;
    }

    /**
     * 11. 结算界面关闭按钮点击状态（2021-12-27新增）
     */
    public static final String MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN = "mg_common_self_click_game_settle_close_btn";

    /**
     * 11. 结算界面关闭按钮点击状态（2021-12-27新增） 模型
     * 用户（本人）点击结算界面关闭按钮
     */
    public static class MGCommonSelfClickGameSettleCloseBtn {
    }

    /**
     * 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
     */
    public static final String MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN = "mg_common_self_click_game_settle_again_btn";

    /**
     * 12. 结算界面再来一局按钮点击状态（2021-12-27新增）模型
     * 用户（本人）点击结算界面再来一局按钮
     */
    public static class MGCommonSelfClickGameSettleAgainBtn {
    }

    /**
     * 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
     */
    public static final String MG_COMMON_GAME_SOUND_LIST = "mg_common_game_sound_list";

    /**
     * 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强） 模型
     * 游戏上报本游戏中所有的声音资源列表
     */
    public static class MGCommonGameSoundList {
        // 声音资源列表
        public List<MGCommonGameSound> list;

        public static class MGCommonGameSound {
            // 声音资源的名字
            public String name;
            // 声音资源的URL链接
            public String url;
            // 声音资源类型
            public String type;
        }
    }

    /**
     * 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
     */
    public static final String MG_COMMON_GAME_SOUND = "mg_common_game_sound";

    /**
     * 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强） 模型
     * 游戏通知app层播放背景音乐的开关状态
     */
    public static class MGCommonGameSound {
        // 是否播放 isPlay==true(播放)，isPlay==false(停止)
        public boolean isPlay;
        // 要播放的声音文件名，不带后缀
        public String name;
        // 声音资源类型
        public String type;
        // 播放次数；注：times == 0 为循环播放
        public String times;
        // https://www.xxxx.xx/xxx.mp3"  声音资源的url链接
        public String url;
    }

    /**
     * 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
     */
    public static final String MG_COMMON_GAME_BG_MUSIC_STATE = "mg_common_game_bg_music_state";

    /**
     * 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强） 模型
     * 游戏通知app层播放背景音乐的开关状态
     */
    public static class MGCommonGameBgMusicState {
        // 背景音乐的开关状态 true: 开，false: 关
        public boolean state;
    }

    /**
     * 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
     */
    public static final String MG_COMMON_GAME_SOUND_STATE = "mg_common_game_sound_state";

    /**
     * 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强） 模型
     * 游戏通知app层播放音效的状态
     */
    public static class MGCommonGameSoundState {
        // 背景音乐的开关状态 true: 开，false: 关
        public boolean state;
    }

    /**
     * 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
     */
    public static final String MG_COMMON_GAME_ASR = "mg_common_game_asr";

    /**
     * 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增) 模型
     */
    public static class MGCommonGameASR {
        // true:打开语音识别 false:关闭语音识别
        public boolean isOpen;
        // 必填字段；关键词列表，可以传送多个关键词
        public List<String> wordList;
        // 必填字段；关键词语言，默认:zh-CN(老版本游戏可能没有)；透传
        public String wordLanguage;
        // 必填字段；text:文本包含匹配; number:数字等于匹配(必填字段)；默认:text(老版本游戏可能没有)；数字炸弹填number；透传
        public String wordType;
        // 必填字段；false: 命中不停止；true:命中停止(必填字段)；默认:true(老版本游戏可能没有) 你演我猜填false；透传
        public boolean isCloseConnHitted;
        // 必填字段，是否需要匹配关键字， 默认是true,   如果是false, 则只简单的返回语音识别文本；透传
        public boolean enableIsHit;
        // 必填字段，是否需要返回转写文本，默认是true
        public boolean enableIsReturnText;
    }

    /**
     * 18. 麦克风状态（2022-02-08新增）
     */
    public static final String MG_COMMON_SELF_MICROPHONE = "mg_common_self_microphone";

    /**
     * 18. 麦克风状态（2022-02-08新增） 模型
     * 游戏通知app麦克风状态
     */
    public static class MGCommonSelfMicrophone {
        // 麦克风开关状态 true: 开，false: 关
        public boolean isOn;
    }

    /**
     * 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
     */
    public static final String MG_COMMON_SELF_HEADPHONE = "mg_common_self_headphone";

    /**
     * 19. 耳机（听筒，扬声器）状态（2022-02-08新增） 模型
     */
    public static class MGCommonSelfHeadphone {
        // 耳机（听筒，喇叭）开关状态 true: 开，false: 关
        public boolean isOn;
    }

    // endregion 通用状态-游戏


    // region MG状态机-通用状态-玩家
    // 参考：https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStatePlayer.html

    /**
     * 1.加入状态（已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_IN = "mg_common_player_in";

    /**
     * 1.加入状态（已修改） 模型
     * 用户是否加入游戏；
     * 游戏开始后，未加入的用户为OB视角。
     */
    public static class MGCommonPlayerIn {
        // true 已加入，false 未加入
        public boolean isIn;

        // 加入哪支队伍
        public int teamId;

        // 当isIn==false时有效；0 主动退出，1 被踢;（reason默认-1，无意义便于处理）
        public int reason;

        // 当reason==1时有效；kickUID为踢人的用户uid；判断被踢的人是本人条件(onPlayerStateChange(userId==kickedUID == selfUID)；（kickUID默认""，无意义便于处理）
        public String kickUID;
    }

    /**
     * 2.准备状态（已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_READY = "mg_common_player_ready";

    /**
     * 2.准备状态（已修改） 模型
     * 用户是否为队长，队长在游戏中会有开始游戏的权利。
     */
    public static class MGCommonPlayerReady {
        // 当retCode==0时有效；true 已准备，false 未准备
        public boolean isReady;
    }

    /**
     * 3.队长状态（已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_CAPTAIN = "mg_common_player_captain";

    /**
     * 3.队长状态（已修改） 模型
     * 用户是否为队长，队长在游戏中会有开始游戏的权利。
     */
    public static class MGCommonPlayerCaptain {
        // true 是队长，false 不是队长；
        public boolean isCaptain;
    }

    /**
     * 4.游戏状态（已修改）
     * 最低版本: v1.1.30.xx
     */
    public static final String MG_COMMON_PLAYER_PLAYING = "mg_common_player_playing";

    /**
     * 4.游戏状态（已修改）模型
     * 用户游戏状态，如果用户在游戏中，建议：
     * a.空出屏幕中心区：
     * 关闭全屏礼物特效；
     * b.部分强操作类小游戏（spaceMax为true），尽量收缩原生UI，给游戏留出尽量大的操作空间：
     * 收缩公屏；
     * 收缩麦位；
     * 如果不在游戏中，则恢复。
     */
    public static class MGCommonPlayerPlaying {
        // true 游戏中，false 未在游戏中；
        public boolean isPlaying;
        // 本轮游戏id，当isPlaying==true时有效
        public String gameRoundId;
        // 当isPlaying==false时有效；isPlaying=false, 0:正常结束 1:提前结束（自己不玩了）2:无真人可以提前结束（无真人，只有机器人） 3:所有人都提前结束；（reason默认-1，无意义便于处理）
        public int reason;
        // true 建议尽量收缩原生UI，给游戏留出尽量大的操作空间 false 初始状态；
        public Boolean spaceMax;
    }

    /**
     * 5.玩家在线状态
     */
    public static final String MG_COMMON_PLAYER_ONLINE = "mg_common_player_online";

    /**
     * 5.玩家在线状态 模型
     */
    public static class MGCommonPlayerOnline {
        // true：在线，false： 离线
        public boolean isOnline;
    }

    /**
     * 6.玩家换游戏位状态
     */
    public static final String MG_COMMON_PLAYER_CHANGE_SEAT = "mg_common_player_change_seat";

    /**
     * 6.玩家换游戏位状态 模型
     */
    public static class MGCommonPlayerChangeSeat {
        // 换位前的游戏位(座位号)
        public int preSeatIndex;
        // 换位成功后的游戏位(座位号)
        public int currentSeatIndex;
    }

    /**
     * 7. 游戏通知app点击玩家头像（2022-02-09新增，现在只支持飞行棋ludo，仅用于游戏场景中的玩家头像）
     */
    public static final String MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON = "mg_common_self_click_game_player_icon";

    /**
     * 7. 游戏通知app点击玩家头像（2022-02-09新增，现在只支持飞行棋ludo，仅用于游戏场景中的玩家头像）模型
     */
    public static class MGCommonSelfClickGamePlayerIcon {
        // 被点击头像的用户id
        public String uid;
    }

    // endregion 通用状态-玩家


    // region 碰碰我最强
    // endregion 碰碰我最强

    // region 飞刀达人
    // endregion 飞刀达人

    // region 你画我猜
    // 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/DrawGuess.html

    /**
     * 1. 选词中状态（已修改）
     */
    public static final String MG_DG_SELECTING = "mg_dg_selecting";

    /**
     * 1. 选词中状态（已修改） 模型
     * 选词中，头像正下方
     */
    public static class MGDGSelecting {
        // bool 类型 true：正在选词中，false: 不在选词中
        public boolean isSelecting;
    }

    /**
     * 2. 作画中状态（已修改）
     */
    public static final String MG_DG_PAINTING = "mg_dg_painting";

    /**
     * 2. 作画中状态（已修改） 模型
     * 作画中，头像正下方
     */
    public static class MGDGPainting {
        // true: 绘画中，false: 取消绘画
        public boolean isPainting;
    }

    /**
     * 3. 显示错误答案状态（已修改）
     */
    public static final String MG_DG_ERRORANSWER = "mg_dg_erroranswer";

    /**
     * 3. 显示错误答案状态（已修改） 模型
     * 错误的答案，最多6中文，头像正下方
     */
    public static class MGDGErroranswer {
        // 字符串类型，展示错误答案
        public String msg;
    }

    /**
     * 4. 显示总积分状态（已修改）
     */
    public static final String MG_DG_TOTALSCORE = "mg_dg_totalscore";

    /**
     * 4. 显示总积分状态（已修改） 模型
     * 总积分，位于头像右上角
     */
    public static class MGDGTotalscore {
        // 字符串类型 总积分
        public String msg;
    }

    /**
     * 5. 本次获得积分状态（已修改）
     */
    public static final String MG_DG_SCORE = "mg_dg_score";

    /**
     * 5. 本次获得积分状态（已修改） 模型
     * 本次积分，头像正下方
     */
    public static final class MGDGScore {
        // string类型，展示本次获得积分
        public String msg;
    }

    // endregion 你画我猜

}
