package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.middle.MediaAudioEngineManager;
import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;
import tech.sud.mgp.audio.middle.MediaRoomConfig;
import tech.sud.mgp.audio.middle.MediaUser;
import tech.sud.mgp.common.model.HSUserInfo;

/**
 * 语音引擎
 */
public class AudioEngineManager extends BaseServiceManager {
    private AudioRoomServiceManager parentManager;

    public AudioEngineManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    public void enterRoom(RoomInfoModel model) {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine == null) return;
        MediaRoomConfig config = new MediaRoomConfig();
        config.isUserStatusNotify = true;
        engine.loginRoom(model.roomId + "", new MediaUser(HSUserInfo.userId + "", HSUserInfo.nickName), config);
    }

    /**
     * 发送信令
     *
     * @param command 信令内容
     * @param result  回调
     */
    public void sendCommand(String command, MediaAudioEngineProtocol.SendCommandResult result) {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.sendCommand(parentManager.getRoomId() + "", command, result);
        }
    }

    private MediaAudioEngineProtocol getEngine() {
        return MediaAudioEngineManager.shared().audioEngine;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaAudioEngineProtocol engine = getEngine();
        if (engine == null) return;
        engine.logoutRoom();
    }
}
