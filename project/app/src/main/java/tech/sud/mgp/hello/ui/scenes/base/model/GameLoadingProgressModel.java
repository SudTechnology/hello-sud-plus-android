package tech.sud.mgp.hello.ui.scenes.base.model;

/**
 * 游戏加载进度
 */
public class GameLoadingProgressModel {

    public GameLoadingProgressModel() {
    }

    public GameLoadingProgressModel(int stage, int retCode, int progress) {
        this.stage = stage;
        this.retCode = retCode;
        this.progress = progress;
    }

    public int stage; // 阶段：start=1,loading=2,end=3
    public int retCode; // 错误码：0成功
    public int progress; // 进度：[0, 100]

}
