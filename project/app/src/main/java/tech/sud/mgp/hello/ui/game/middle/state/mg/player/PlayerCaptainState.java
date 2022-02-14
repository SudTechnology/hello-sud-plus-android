package tech.sud.mgp.hello.ui.game.middle.state.mg.player;

/**
 * 队长状态
 */
public class PlayerCaptainState {

    /**
     * 0 成功，非0不成功（详情见错误码）
     */
    public int retCode;

    /**
     * 当retCode==0时有效；true 是队长，false 不是队长；
     */
    public boolean isCaptain;

}
