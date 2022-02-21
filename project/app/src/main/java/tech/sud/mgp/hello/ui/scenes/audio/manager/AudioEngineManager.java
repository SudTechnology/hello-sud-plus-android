package tech.sud.mgp.hello.ui.scenes.audio.manager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomConfig;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEventHandler;
import tech.sud.mgp.hello.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.hello.ui.main.home.RTCManager;
import tech.sud.mgp.hello.ui.scenes.audio.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.audio.service.AudioRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;

/**
 * 语音引擎
 */
public class AudioEngineManager extends BaseServiceManager {
    private final AudioRoomServiceManager parentManager;

    public final AudioCommandManager commandManager = new AudioCommandManager();
    private OnRoomStreamUpdateListener onRoomStreamUpdateListener;
    private final int soundLevelThreshold = 1; // 触发声浪显示的阀值
    /**
     * 标识音频监听开关状态
     */
    private boolean isOpenListen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        commandManager.onCreate();
        RTCManager.applyRtcEngine();
    }

    public AudioEngineManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    public void enterRoom(RoomInfoModel model) {
        IAudioEngine engine = getEngine();
        if (engine == null) return;
        AudioRoomConfig config = new AudioRoomConfig();
        config.isUserStatusNotify = true;
        engine.loginRoom(model.roomId + "", new AudioUser(HSUserInfo.userId + "", HSUserInfo.nickName), config);
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
    public void sendCommand(String command, IAudioEngine.SendCommandResult result) {
        IAudioEngine engine = getEngine();
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
        IAudioEngine engine = getEngine();
        if (engine != null) {
            engine.startPublish(streamId);
        }
    }

    /**
     * 停止推流
     */
    public void stopPublishStream() {
        IAudioEngine engine = getEngine();
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
        IAudioEngine engine = getEngine();
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
        IAudioEngine engine = getEngine();
        if (engine != null) {
            engine.stopPlayingStream(streamId);
        }
    }

    /**
     * 开始音频流监听
     */
    void startAudioDataListener() {
        IAudioEngine engine = getEngine();
        if (engine != null) {
            engine.setAudioDataHandler();
        }
    }

    /**
     * 控制是否要开启音频流监听
     */
    void switchAudioDataListener(boolean isOpen) {
        if (isOpenListen == isOpen) {
            return;
        }
        IAudioEngine engine = getEngine();
        if (engine != null) {
            isOpenListen = isOpen;
            if (isOpen) {
                engine.startAudioDataListener();
            } else {
                engine.stopAudioDataListener();
            }
        }

    }

    private IAudioEngine getEngine() {
        return AudioEngineFactory.getEngine();
    }

    private final IAudioEventHandler eventHandler = new IAudioEventHandler() {
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
                String userIdStr = entry.getKey();
                Float soundLevel = entry.getValue();
                if (soundLevel > soundLevelThreshold) {
                    long userIdL;
                    try {
                        userIdL = Long.parseLong(userIdStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    int micIndex = parentManager.audioMicManager.findMicIndex(userIdL);
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
        public void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData) {
            OnRoomStreamUpdateListener listener = onRoomStreamUpdateListener;
            if (listener != null) {
                listener.onRoomStreamUpdate(roomId, type, streamList, extendedData);
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomId, AudioUser fromUser, String command) {
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
        public void onRoomStateUpdate(String roomID, AudioRoomState state, int errorCode, JSONObject extendedData) {
            if (state == AudioRoomState.CONNECTED) { // 连接成功之后发送进房信令
                sendCommand(RoomCmdModelUtils.buildEnterRoomCommand(), null);
            }
        }

        @Override
        public void onCapturedAudioData(AudioPCMData audioPCMData) {
            AudioRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onCapturedAudioData(audioPCMData);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        commandManager.onDestroy();
        IAudioEngine engine = getEngine();
        if (engine == null) return;
        engine.logoutRoom();
        engine.setEventHandler(null);
    }

    public interface OnRoomStreamUpdateListener {
        void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData);
    }

}
