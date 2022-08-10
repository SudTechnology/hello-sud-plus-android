package tech.sud.mgp.hello.service.base;


/**
 * 接口地址
 */
public interface RequestUrl {

    /** 登录 */
    String LOGIN = "login/v1";

    /** 刷新token */
    String REFRESHTOKEN = "refresh-token/v1";

    /** 主页接口 */
    String GAME_LIST = "game/list/v1";

    /** 房间列表 */
    String ROOM_LIST = "room/list/v1";

    /** 进入房间 */
    String ENTER_ROOM = "room/enter-room/v1";

    /** 查询麦位列表接口 */
    String ROOM_MIC_LIST = "room/mic/list/v1";

    /** 批量查询用户信息 */
    String USER_INFO_LIST = "batch/user-info/v1";

    /** 房间上下麦接口 */
    String ROOM_MIC_SWITCH = "room/switch-mic/v1";

    /** 房间列表 */
    String ROOM_MATCH = "room/match-room/v1";

    /** 查询基础配置 */
    String GET_BASE_CONFIG = "base/config/v1";

    /** 游戏登录 */
    String GAME_LOGIN = "base/login/v1";

    /** 游戏切换 */
    String SWITCH_GAME = "room/switch-game/v1";

    /** 退出房间 */
    String EXIT_ROOM = "room/exit-room/v1";

    /** 创建房间 */
    String CREAT_ROOM = "room/create-room/v1";

    /** 查询用户账户 */
    String GET_ACCOUNT = "get-account/v1";

    /** 确认加入门票游戏 */
    String TICKET_CONFIRM_JOIN = "game/ticket/confirm-join/v1";

    /** 检查更新 */
    String CHECK_UPGRADE = "check-upgrade/v1";

    /** 点单场景：用户下单 */
    String ROOM_ORDER_CREATE = "room/order/create/v1";

    /** 点单场景：主播接单 */
    String ROOM_ORDER_RECEIVE = "room/order/receive/v1";

    // region 房间pk
    /** 设置PK时长 */
    String ROOM_PK_DURATION = "room/pk/duration/v1";

    /** 设置PK开关 */
    String ROOM_PK_SWITCH = "room/pk/switch/v1";

    /** 开始PK */
    String ROOM_PK_START = "room/pk/start/v1";

    /** 同意PK */
    String ROOM_PK_AGREE = "room/pk/agree/v1";

    /** 移除pk对手 */
    String ROOM_PK_REMOVE_RIVAL = "room/pk/release/v1";

    /** pk再来一局 */
    String ROOM_PK_AGAIN = "room/pk/again/v1";
    // endregion 房间pk

    // region 竞猜场景
    /** 下注 */
    String QUIZ_BET = "quiz/bet/v1";

    /** 查询竞猜游戏列表 */
    String QUIZ_GAME_LIST = "quiz/list/v1";

    /** 查询竞猜场景游戏玩家列表（房间内） */
    String QUIZ_GAME_PLAYER = "quiz/game-player/v1";
    // endregion 竞猜场景

    // region 弹幕游戏
    /** 弹幕列表 */
    String DANMAKU_LIST = "bullet-chat-game/shortcut-window/v1";

    /** 发送弹幕 */
    String SEND_DANMAKU = "bullet-chat-game/send-barrage/v1";
    // endregion 弹幕游戏

    // region 礼物
    /** 送礼 */
    String SEND_GIFT = "gift/send/v1";

    /** 礼物列表 */
    String GIFT_LIST = "gift/list/v1";

    /** 机器人列表 */
    String ROBOT_LIST = "robot/list/v1";
    // endregion 礼物

    // region 跨域
    /** 授权房间列表 */
    String AUTH_ROOM_LIST = "extra/get-auth-room-list";
    /** 跨域匹配房间 */
    String AUTH_MATCH_ROOM = "extra/match-room/v1";
    // endregion 跨域

    // region 蹦迪
    /** 扣费 */
    String DEDUCTION_COIN = "disco/operation/v1";

    /** 蹦迪主播列表 */
    String DISCO_ANCHOR_LIST = "disco/anchor-list/v1";

    /** 上/下主播位 */
    String DISCO_SWITCH_ANCHOR = "disco/switch-anchors/v1";
    // endregion 蹦迪

    // region 联赛
    /** 查询进入前三的房间 */
    String LEAGUE_PLAYING = "league/playing/v1";
    // endregion 联赛

}
