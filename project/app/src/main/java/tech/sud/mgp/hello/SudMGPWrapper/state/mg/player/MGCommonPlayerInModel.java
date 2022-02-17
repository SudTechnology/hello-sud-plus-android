package tech.sud.mgp.hello.SudMGPWrapper.state.mg.player;

/**
 * 玩家加入状态
 */
public class MGCommonPlayerInModel {

    /**
     * true 已加入，false 未加入
     */
    public boolean isIn;

    /**
     * 加入哪支队伍
     */
    public int teamId;

    /**
     * 当isIn==false时有效；0 主动退出，1 被踢;（reason默认-1，无意义便于处理）
     */
    public int reason;

    /**
     * 当reason==1时有效；kickUID为踢人的用户uid；判断被踢的人是本人条件(onPlayerStateChange(userId==kickedUID == selfUID)；（kickUID默认""，无意义便于处理）
     */
    public String kickUID;

}
