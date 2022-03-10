package tech.sud.mgp.hello.ui.scenes.audio.manager;

public class AudioGameManager extends BaseServiceManager {

    private AudioRoomServiceManager parentManager;

    public AudioGameManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.audioEngineManager.startAudioDataListener();
    }

    /**
     * 设置ASR状态
     */
    public void setASROpen(boolean isOpen) {
        if (parentManager.audioStreamManager.isPublishingStream()) {
            parentManager.audioEngineManager.switchAudioDataListener(isOpen);
        } else {
            parentManager.audioEngineManager.switchAudioDataListener(false);
        }
    }

    /**
     * 设置RTC拉流状态
     */
    public void setRTCPlay(boolean isOn) {
        if (isOn) {
            parentManager.audioEngineManager.startSubscribing();
        } else {
            parentManager.audioEngineManager.stopSubscribing();
        }
    }
}