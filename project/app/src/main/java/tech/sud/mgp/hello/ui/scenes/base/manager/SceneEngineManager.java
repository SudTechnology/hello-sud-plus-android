package tech.sud.mgp.hello.ui.scenes.base.manager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;
import tech.sud.mgp.hello.ui.main.home.RTCManager;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;

/**
 * 语音引擎
 */
public class SceneEngineManager extends BaseServiceManager {
    private final SceneRoomServiceManager parentManager;

    public final SceneCommandManager commandManager = new SceneCommandManager();
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

    public SceneEngineManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    public void enterRoom(RoomInfoModel model) {
        ISudAudioEngine engine = getEngine();
        if (engine == null) return;

        String rtcType = AppData.getInstance().getRtcType();
        AudioJoinRoomModel audioJoinRoomModel = null;
        if (rtcType.equals("Zego")) {
            audioJoinRoomModel = new AudioJoinRoomModel();
            audioJoinRoomModel.userID = HSUserInfo.userId + "";
            audioJoinRoomModel.userName = HSUserInfo.nickName;
            audioJoinRoomModel.roomID = model.roomId + "";
        } else if (rtcType.equals("Agora")){
            audioJoinRoomModel = new AudioJoinRoomModel();
            audioJoinRoomModel.userID = HSUserInfo.userId + "";
            audioJoinRoomModel.roomID = model.roomId + "";
        }

        if (audioJoinRoomModel == null)
            return;

        engine.joinRoom(audioJoinRoomModel);
        engine.setEventListener(eventHandler);
        engine.setAudioRouteToSpeaker(true);
    }

    public void setCommandListener(SceneCommandManager.ICommandListener listener) {
        commandManager.setCommandListener(listener);
    }

    public void removeCommandListener(SceneCommandManager.ICommandListener listener) {
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
    public void sendCommand(String command, ISudAudioEngine.SendCommandListener result) {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.sendCommand(command, result);
        }
    }

    /**
     * 开启推流
     *
     */
    public void startPublishStream() {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.startPublishStream();
        }
    }

    /**
     * 停止推流
     */
    public void stopPublishStream() {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.stopPublishStream();
        }
    }

    /**
     * 开启拉流
     */
    public void startSubscribingStream() {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.startSubscribingStream();
        }
    }

    /**
     * 停止拉流
     */
    public void stopSubscribingStream() {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.stopSubscribingStream();
        }
    }

    /**
     * 控制是否要开启音频流监听
     */
    void switchAudioDataListener(boolean isOpen) {
        if (isOpenListen == isOpen) {
            return;
        }
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            isOpenListen = isOpen;
            if (isOpen) {
                engine.startPCMCapture();
            } else {
                engine.stopPCMCapture();
            }
        }

    }

    private ISudAudioEngine getEngine() {
        return AudioEngineFactory.getEngine();
    }

    private final ISudAudioEventListener eventHandler = new ISudAudioEventListener() {
        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            if (parentManager.sceneStreamManager.isPublishingStream() && soundLevel > soundLevelThreshold) {
                int selfMicIndex = parentManager.sceneMicManager.findSelfMicIndex();
                if (selfMicIndex >= 0) {
                    SceneRoomServiceCallback callback = parentManager.getCallback();
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
                    int micIndex = parentManager.sceneMicManager.findMicIndex(userIdL);
                    if (micIndex >= 0) {
                        SceneRoomServiceCallback callback = parentManager.getCallback();
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
        public void onRecvCommand(String fromUserID, String command) {
            commandManager.onRecvCommand(fromUserID, command);
        }

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
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
        public void onCapturedPCMData(AudioPCMData audioPCMData) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onCapturedAudioData(audioPCMData);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        commandManager.onDestroy();
        ISudAudioEngine engine = getEngine();
        if (engine == null) return;
        engine.leaveRoom();
        engine.setEventListener(null);
    }

    public interface OnRoomStreamUpdateListener {
        void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData);
    }

}