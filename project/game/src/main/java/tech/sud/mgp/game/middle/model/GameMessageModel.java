package tech.sud.mgp.game.middle.model;

/**
 * 游戏消息内容
 */
public class GameMessageModel {

    /**
     * 0 通知
     * 1 提醒
     * 2 结算
     * 3 其他
     */
    public int type;

    /**
     * 消息内容
     */
    public String msg;
}
