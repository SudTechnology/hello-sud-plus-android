package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

public class RoomCmd {

    /**
     * 公屏消息
     */
    public static final int CMD_CHAT_TEXT_NOTIFY = 10000;

    /**
     * 发送礼物
     */
    public static final int CMD_SEND_GIFT_NOTIFY = 10001;

    /**
     * 上麦
     */
    public static final int CMD_UP_MIC_NOTIFY = 10002;

    /**
     * 下麦
     */
    public static final int CMD_DOWN_MIC_NOTIFY = 10003;

    /**
     * 游戏切换
     */
    public static final int CMD_CHANGE_GAME_NOTIFY = 10004;

    /**
     * 用户进入房间通知
     */
    public static final int CMD_ENTER_ROOM_NOTIFY = 10005;

    /**
     * 发送跨房PK邀请
     */
    public static final int CMD_SEND_ROOM_PK_INVITE = 10006;

    /**
     * 跨房PK邀请应答
     */
    public static final int CMD_ROOM_PK_ANSWER = 10007;

    /**
     * 开始跨房PK
     */
    public static final int CMD_START_ROOM_PK = 10008;

    /**
     * 结束跨房PK
     */
    public static final int CMD_FINISH_ROOM_PK = 10009;

    /**
     * 跨房PK设置
     */
    public static final int CMD_ROOM_PK_SETTINGS = 10010;

    /**
     * 开启匹配跨房PK答
     */
    public static final int CMD_OPEN_MATCH_ROOM_PK = 10011;

    /**
     * 用户点单
     */
    public static final int CMD_USER_ORDER_NOTIFY = 100012;

    /**
     * 主播同意或者拒绝用户点单
     */
    public static final int CMD_ORDER_OPERATE_NOTIFY = 100013;

}
