package tech.sud.mgp.audio.example.manager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.sud.mgp.audio.example.model.RoomInfoModel;
import tech.sud.mgp.audio.example.service.AudioRoomServiceCallback;
import tech.sud.mgp.audio.example.utils.AudioRoomCommandUtils;
import tech.sud.mgp.audio.middle.MediaAudioEngineManager;
import tech.sud.mgp.audio.middle.MediaAudioEngineNetworkStateType;
import tech.sud.mgp.audio.middle.MediaAudioEnginePlayerStateType;
import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;
import tech.sud.mgp.audio.middle.MediaAudioEnginePublisherSateType;
import tech.sud.mgp.audio.middle.MediaAudioEngineUpdateType;
import tech.sud.mgp.audio.middle.MediaAudioEventHandler;
import tech.sud.mgp.audio.middle.MediaAudioRoomState;
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
    private OnRoomStreamUpdateListener onRoomStreamUpdateListener;
    private final int soundLevelThreshold = 1; // 触发声浪显示的阀值

    @Override
    public void onCreate() {
        super.onCreate();
        commandManager.onCreate();
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.startSoundLevelMonitor();
        }
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

    public void setOnRoomStreamUpdateListener(OnRoomStreamUpdateListener listener) {
        this.onRoomStreamUpdateListener = listener;
    }

    public void removeOnRoomStreamUpdateListener(OnRoomStreamUpdateListener listener) {
        if (onRoomStreamUpdateListener == listener) {
            onRoomStreamUpdateListener = null;
        }
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

    /**
     * 开启推流
     *
     * @param streamId
     */
    public void startPublish(String streamId) {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.startPublish(streamId);
        }
    }

    /**
     * 停止推流
     */
    public void stopPublishStream() {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.stopPublishStream();
        }
    }

    /**
     * 播放流
     *
     * @param streamId 流ID
     */
    void startPlayingStream(String streamId) {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.startPlayingStream(streamId);
        }
    }

    /**
     * 停止播放流
     *
     * @param streamId 流ID
     */
    void stopPlayingStream(String streamId) {
        MediaAudioEngineProtocol engine = getEngine();
        if (engine != null) {
            engine.stopPlayingStream(streamId);
        }
    }

    private MediaAudioEngineProtocol getEngine() {
        return MediaAudioEngineManager.shared().audioEngine;
    }

    private final MediaAudioEventHandler eventHandler = new MediaAudioEventHandler() {
        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            if (parentManager.audioStreamManager.isPublishingStream() && soundLevel > soundLevelThreshold) {
                int selfMicIndex = parentManager.audioMicManager.findSelfMicIndex();
                if (selfMicIndex >= 0) {
                    AudioRoomServiceCallback callback = parentManager.getCallback();
                    if (callback != null) {
                        callback.onSoundLevel(selfMicIndex);
                    }
                }
            }
        }

        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
            if (soundLevels == null || soundLevels.size() == 0) {
                return;
            }
            Set<Map.Entry<String, Float>> entries = soundLevels.entrySet();
            for (Map.Entry<String, Float> entry : entries) {
                String streamId = entry.getKey();
                Float soundLevel = entry.getValue();
                if (soundLevel > soundLevelThreshold) {
                    int micIndex = parentManager.audioMicManager.findMicIndexByStreamId(streamId);
                    if (micIndex >= 0) {
                        AudioRoomServiceCallback callback = parentManager.getCallback();
                        if (callback != null) {
                            callback.onSoundLevel(micIndex);
                        }
                    }
                }
            }
        }

        @Override
        public void onRoomStreamUpdate(String roomId, MediaAudioEngineUpdateType type, List<MediaStream> streamList, JSONObject extendedData) {
            OnRoomStreamUpdateListener listener = onRoomStreamUpdateListener;
            if (listener != null) {
                listener.onRoomStreamUpdate(roomId, type, streamList, extendedData);
            }
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

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            AudioRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onRoomOnlineUserCountUpdate(roomID, count);
            }
        }

        @Override
        public void onRoomStateUpdate(String roomID, MediaAudioRoomState state, int errorCode, JSONObject extendedData) {
            if (state == MediaAudioRoomState.CONNECTED) { // 连接成功之后发送进房信令
                sendCommand(AudioRoomCommandUtils.buildEnterRoomCommand(), null);
            }
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

    public interface OnRoomStreamUpdateListener {
        void onRoomStreamUpdate(String roomId, MediaAudioEngineUpdateType type, List<MediaStream> streamList, JSONObject extendedData);
    }

}
