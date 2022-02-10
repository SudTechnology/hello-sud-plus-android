package tech.sud.mgp.game.middle.state.mg.common;

/**
 * 通用状态-游戏状态
 */
public class CommonGameState {

    public static final int IDLE = 0;
    public static final int LOADING = 1;
    public static final int PLAYING = 2;

    /**
     * gameState=0 (idle 状态，游戏未开始，空闲状态）；
     * gameState=1（loading 状态，所有玩家都准备好，队长点击了开始游戏按钮，等待加载游戏场景开始游戏，游戏即将开始提示阶段）；
     * gameState=2（playing状态，游戏进行中状态）
     */
    public int gameState;

}
