package tech.sud.mgp.hello.ui.scenes.base.manager;

public class SceneGameManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;

    public SceneGameManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.startAudioDataListener();
    }

    /**
     * 设置ASR状态
     */
    public void setASROpen(boolean isOpen) {
        if (parentManager.sceneStreamManager.isPublishingStream()) {
            parentManager.sceneEngineManager.switchAudioDataListener(isOpen);
        } else {
            parentManager.sceneEngineManager.switchAudioDataListener(false);
        }
    }

    /**
     * 设置RTC拉流状态
     */
    public void setRTCPlay(boolean isOn) {
        if (isOn) {
            parentManager.sceneEngineManager.startSubscribing();
        } else {
            parentManager.sceneEngineManager.stopSubscribing();
        }
    }
}