package tech.sud.mgp.hello.SudMGPWrapper.state.app;

/**
 * app发送加入状态
 */
public class CommonSelfInState {

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
