package tech.sud.mgp.hello.ui.room.audio.example.manager;

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
     * 设置关键词
     * 判断是否要开启或关闭语音监听，来触发游戏的语音命中
     */
    public void setkeyword(String keyword) {
//        if (keyword == null || keyword.isEmpty()) {
//            parentManager.audioEngineManager.switchAudioDataListener(false);
//            return;
//        }
//        if (parentManager.audioStreamManager.isPublishingStream()) {
//            parentManager.audioEngineManager.switchAudioDataListener(true);
//        } else {
//            parentManager.audioEngineManager.switchAudioDataListener(false);
//        }
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
}