package tech.sud.mgp.hello.service.main.req;

/**
 * 确认加入门票场景游戏 请求参数
 */
public class TicketConfirmJoinReq {
    public int sceneId; //  场景id
    public long roomId; // 房间ID
    public long gameId; // 游戏id
    public int gameLevel; // 游戏等级
}
