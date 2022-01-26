package tech.sud.mgp.audio.example.manager;

/**
 * 房间音频流
 */
public class AudioStreamManager extends BaseServiceManager {
    private AudioRoomServiceManager audioRoomServiceManager;

    public AudioStreamManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.audioRoomServiceManager = audioRoomServiceManager;
    }
}
