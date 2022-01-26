package tech.sud.mgp.audio.example.manager;

/**
 * 房间公屏
 */
public class AudioChatManager extends BaseServiceManager {
    private AudioRoomServiceManager audioRoomServiceManager;

    public AudioChatManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.audioRoomServiceManager = audioRoomServiceManager;
    }

}
