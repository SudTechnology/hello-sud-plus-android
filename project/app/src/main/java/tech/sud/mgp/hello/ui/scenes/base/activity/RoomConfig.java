package tech.sud.mgp.hello.ui.scenes.base.activity;

/**
 * 场景配置
 */
public class RoomConfig {
    public int micCount = 12; // 麦位数量
    public boolean isSudGame = true; // 是否要加载sud游戏
    public boolean isShowGameNumber = true; // 是否展示游戏人数
    public boolean isShowASRTopHint = true; // 右上角是否展示支持ASR的提示
    public boolean isSupportAddRobot = false; // 是否具备添加机器人的能力
    public boolean isShowInteractionGame = true; // 交互游戏房间，是否要展示交互游戏入口
}
