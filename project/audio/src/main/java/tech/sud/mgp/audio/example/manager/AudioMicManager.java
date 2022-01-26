package tech.sud.mgp.audio.example.manager;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;

/**
 * 房间麦位
 */
public class AudioMicManager extends BaseServiceManager {
    private AudioRoomServiceManager audioRoomServiceManager;

    private List<AudioRoomMicModel> micList = new ArrayList<>();

    public AudioMicManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.audioRoomServiceManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (int i = 0; i < 9; i++) {
            micList.add(new AudioRoomMicModel());
        }
    }

    public void enterRoom(RoomInfoModel model) {
        AudioRoomServiceCallback callback = audioRoomServiceManager.getCallback();
        if (callback != null) {
            callback.setMicList(micList);
        }
    }

    private void refreshMicList() {
        
    }


}
