package tech.sud.mgp.hello.ui.room.audio.model.command;

public class CommandCmd {

    /**
     * 公屏消息
     */
    public static final int CMD_PUBLIC_MSG_NTF = 0x01;

    /**
     * 发送礼物
     */
    public static final int CMD_PUBLIC_SEND_GIFT_NTF = 0x02;

    /**
     * 上麦
     */
    public static final int CMD_UP_MIC_NTF = 0x03;

    /**
     * 下麦
     */
    public static final int CMD_DOWN_MIC_NTF = 0x04;

    /**
     * 游戏切换
     */
    public static final int CMD_GAME_CHANGE = 0x05;

    /**
     * 用户进入房间通知
     */
    public static final int CMD_ENTER_ROOM_NTF = 0x06;

}
