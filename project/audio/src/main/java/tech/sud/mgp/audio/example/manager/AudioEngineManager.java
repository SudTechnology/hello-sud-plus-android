package tech.sud.mgp.audio.example.manager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.middle.MediaAudioEngineManager;
import tech.sud.mgp.audio.middle.MediaAudioEngineNetworkStateType;
import tech.sud.mgp.audio.middle.MediaAudioEnginePlayerStateType;
import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;
import tech.sud.mgp.audio.middle.MediaAudioEnginePublisherSateType;
import tech.sud.mgp.audio.middle.MediaAudioEngineUpdateType;
import tech.sud.mgp.audio.middle.MediaAudioEventHandler;
import tech.sud.mgp.audio.middle.MediaRoomConfig;
import tech.sud.mgp.audio.middle.MediaStream;
import tech.sud.mgp.audio.middle.MediaUser;
import tech.sud.mgp.common.model.HSUserInfo;

/**
 * 语音引擎
 */
public class AudioEngineManager extends BaseServiceManager {
    private final AudioRoomServiceManager parentManager;

    public final AudioCommandManager commandManager = new AudioCommandManager();

    @Override
    public void onCreate() {
        super.onCreate();
        commandManager.onCreate();
    }

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
        engine.setEventHandler(eventHandler);
    }

    public void setCommandListener(AudioCommandManager.ICommandListener listener) {
        commandManager.setCommandListener(listener);
    }

    public void removeCommandListener(AudioCommandManager.ICommandListener listener) {
        commandManager.removeCommandListener(listener);
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

    private final MediaAudioEventHandler eventHandler = new MediaAudioEventHandler() {
        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {

        }

        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {

        }

        @Override
        public void onRoomStreamUpdate(String roomId, MediaAudioEngineUpdateType type, List<MediaStream> streamList, JSONObject extendedData) {

        }

        @Override
        public void onPublisherStateUpdate(String streamID, MediaAudioEnginePublisherSateType state, int errorCode, JSONObject extendedData) {

        }

        @Override
        public void onPlayerStateUpdate(String streamID, MediaAudioEnginePlayerStateType state, int errorCode, JSONObject extendedData) {

        }

        @Override
        public void onNetworkModeChanged(MediaAudioEngineNetworkStateType mode) {

        }

        @Override
        public void onIMRecvCustomCommand(String roomId, MediaUser fromUser, String command) {
            commandManager.onIMRecvCustomCommand(roomId, fromUser, command);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        commandManager.onDestroy();
        MediaAudioEngineProtocol engine = getEngine();
        if (engine == null) return;
        engine.logoutRoom();
        engine.setEventHandler(null);
    }

}
