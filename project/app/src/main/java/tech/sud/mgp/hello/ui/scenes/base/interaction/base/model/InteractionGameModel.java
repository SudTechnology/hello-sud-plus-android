package tech.sud.mgp.hello.ui.scenes.base.interaction.base.model;

/**
 * 互动游戏 描述
 */
public class InteractionGameModel {
    public long gameId; // 游戏id
    public int iconResId; // 入口图标

    public InteractionGameModel(long gameId, int iconResId) {
        this.gameId = gameId;
        this.iconResId = iconResId;
    }
}
