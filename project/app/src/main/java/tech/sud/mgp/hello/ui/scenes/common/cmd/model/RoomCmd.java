package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

/**
 * 信令值定义
 */
public class RoomCmd {

    // region 基础
    /** 公屏消息 */
    public static final int CMD_CHAT_TEXT_NOTIFY = 10000;

    /** 发送礼物 */
    public static final int CMD_SEND_GIFT_NOTIFY = 10001;

    /** 上麦 */
    public static final int CMD_UP_MIC_NOTIFY = 10002;

    /** 下麦 */
    public static final int CMD_DOWN_MIC_NOTIFY = 10003;

    /** 游戏切换 */
    public static final int CMD_CHANGE_GAME_NOTIFY = 10004;

    /** 用户进入房间通知 */
    public static final int CMD_ENTER_ROOM_NOTIFY = 10005;
    // endregion 基础


    // region 跨房PK
    /** 发送跨房PK邀请 */
    public static final int CMD_ROOM_PK_SEND_INVITE = 10100;

    /** 跨房PK邀请应答 */
    public static final int CMD_ROOM_PK_ANSWER = 10101;

    /** 开始跨房PK */
    public static final int CMD_ROOM_PK_START = 10102;

    /** 结束跨房PK */
    public static final int CMD_ROOM_PK_FINISH = 10103;

    /** 跨房PK设置 */
    public static final int CMD_ROOM_PK_SETTINGS = 10104;

    /** 开启匹配跨房PK */
    public static final int CMD_ROOM_PK_OPEN_MATCH = 10105;

    /** 跨房PK，切换游戏 */
    public static final int CMD_ROOM_PK_CHANGE_GAME = 10106;

    /** 跨房PK，移除对手 */
    public static final int CMD_ROOM_PK_REMOVE_RIVAL = 10107;

    /** 跨房pk游戏结算消息通知 */
    public static final int CMD_ROOM_PK_SETTLE = 20000;

    /** 跨房PK对手房间关闭消息 */
    public static final int CMD_ROOM_PK_RIVAL_EXIT = 20001;
    // endregion 跨房PK

    // region 点单
    /** 用户点单 */
    public static final int CMD_USER_ORDER_NOTIFY = 10200;

    /** 主播同意或者拒绝用户点单 */
    public static final int CMD_ORDER_OPERATE_NOTIFY = 10201;
    // endregion 点单

    // region 竞猜
    /** 竞猜下注通知 */
    public static final int CMD_QUIZ_BET = 10300;
    // endregion 竞猜

    // region 蹦迪
    /** 请求蹦迪信息 */
    public static final int CMD_ROOM_DISCO_INFO_REQ = 10400;
    /** 响应蹦迪信息 */
    public static final int CMD_ROOM_DISCO_INFO_RESP = 10401;
    /** 上DJ台 */
    public static final int CMD_ROOM_DISCO_BECOME_DJ = 10402;
    // endregion 蹦迪

    // region 联赛
    /** 响应联赛信息 */
    public static final int CMD_LEAGUE_INFO_RESP = 10500;
    // endregion 联赛

}
