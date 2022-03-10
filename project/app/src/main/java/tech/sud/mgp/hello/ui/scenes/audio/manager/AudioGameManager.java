package tech.sud.mgp.hello.ui.scenes.audio.manager;

import java.util.UUID;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置关键词(根据业务需求，是否做操作
     */
    public void setkeyword(String keyword) {

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
     * 设置RTC推流状态
     */
    public void setRTCPublish(boolean isOn) {
        if (isOn) {
            if (!parentManager.audioStreamManager.isPublishingStream()) {
                parentManager.audioEngineManager.startPublish(UUID.randomUUID().toString());
            }
        } else {
            parentManager.audioEngineManager.stopPublishStream();
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