package tech.sud.mgp.hello.SudMGPWrapper.state.mg.player;

/**
 * 游戏状态
 */
public class PlayerPlayingState {

    /**
     * 0 成功，非0不成功（详情见错误码）
     */
    public int retCode;

    /**
     * true 游戏中，false 未在游戏中
     */
    public boolean isPlaying;

    /**
     * 当isPlaying==false时有效；isPlaying=false, 0:正常结束 1:提前结束（自己不玩了）2:无真人可以提前结束（无真人，只有机器人） 3:所有人都提前结束；
     */
    public int reason;

    /**
     * true 建议尽量收缩原生UI，给游戏留出尽量大的操作空间 false 初始状态；
     */
    public boolean spaceMax;

}
