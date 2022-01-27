package tech.sud.mgp.audio.example.manager;

/**
 * 房间礼物
 */
public class AudioGiftManager extends BaseServiceManager {
    private AudioRoomServiceManager audioRoomServiceManager;

    public AudioGiftManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.audioRoomServiceManager = audioRoomServiceManager;
    }
}
