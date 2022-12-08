package tech.sud.mgp.hello.service.game.req;

/**
 * 带入筹码到游戏 请求参数
 */
public class BringChipReq {
    public long mgId; // 游戏id
    public String roomId; // 房间id
    public String roundId; // 订单关联的游戏局id
    public long lastRoundScore; // 本人当前积分
    public long incrementalScore; // 充值积分
    public long totalScore; // 充值后总积分
}
