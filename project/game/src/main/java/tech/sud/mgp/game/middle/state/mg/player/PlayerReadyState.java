package tech.sud.mgp.game.middle.state.mg.player;

/**
 * 准备状态
 */
public class PlayerReadyState {

    /**
     * 0 成功，非0不成功（详情见错误码）
     */
    public int retCode;

    /**
     * 当retCode==0时有效；true 已准备，false 未准备
     */
    public boolean isReady;
}
