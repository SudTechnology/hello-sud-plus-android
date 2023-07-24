package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMRoomMemberQueriedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomMemberQueryConfig;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMRoomState;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.main.home.manager.RTCManager;
import tech.sud.mgp.hello.ui.scenes.base.activity.RoomConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInfoModel;
import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.hello.ui.scenes.common.cmd.RoomCmdModelUtils;
import tech.sud.mgp.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.rtc.audio.core.AudioStream;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.rtc.audio.core.MediaViewMode;
import tech.sud.mgp.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.rtc.audio.impl.zego.IMRoomManager;
import tech.sud.mgp.rtc.audio.impl.zego.ZIMManager;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;

/**
 * 语音引擎
 */
public class SceneEngineManager extends BaseServiceManager {
    private final SceneRoomServiceManager parentManager;

    public final SceneCommandManager commandManager = new SceneCommandManager();
    private OnRoomStreamUpdateListener onRoomStreamUpdateListener;
    private final int soundLevelThreshold = 1; // 触发声浪显示的阀值
    private boolean isOpenListen = false; // 标识音频监听开关状态
    private boolean enterRoomCompleted = false; // 标识是否进房成功
    private boolean isInitEngine = false; // 标识是否已初始化engine
    public SceneRoomServiceManager.EnterRoomCompletedListener enterRoomCompletedListener;
    private View videoView;
    private Handler handler;
    private ZIMRoomState zimRoomState;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        commandManager.onCreate();
    }

    public SceneEngineManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    public void enterRoom(RoomConfig config, RoomInfoModel model) {
        if (!isInitEngine) {
            initEngine(model, new Runnable() {
                @Override
                public void run() {
                    joinRoom(model);
                }
            });

            initZim(model);
            return;
        }

        joinRoom(model);

        zimJoinRoom(model);
    }

    private void initZim(RoomInfoModel model) {
        LogUtils.file("initZim");
        BaseConfigResp baseConfigResp = (BaseConfigResp) GlobalCache.getInstance().getSerializable(GlobalCache.BASE_CONFIG_KEY);
        if (baseConfigResp != null && baseConfigResp.zegoCfg != null) {
            IMRoomManager.sharedInstance().init(baseConfigResp.zegoCfg.appId, eventHandler, zimListener);
            zimJoinRoom(model);
        }
    }

    private void zimJoinRoom(RoomInfoModel model) {
        LogUtils.file("zimJoinRoom:" + model.roomId + " userId:" + HSUserInfo.userId);
        IMRoomManager.sharedInstance().joinRoom(model.roomId + "", HSUserInfo.userId + "",
                HSUserInfo.nickName, model.imToken, zimListener);
    }

    private void joinRoom(RoomInfoModel model) {
        ISudAudioEngine engine = getEngine();
        if (engine == null) return;

        enterRoomCompleted = false;
        AudioJoinRoomModel audioJoinRoomModel = new AudioJoinRoomModel();
        audioJoinRoomModel.userID = HSUserInfo.userId + "";
        audioJoinRoomModel.userName = HSUserInfo.nickName;
        audioJoinRoomModel.roomID = model.roomId + "";
        audioJoinRoomModel.timestamp = System.currentTimeMillis();
        audioJoinRoomModel.token = model.rtcToken;
        audioJoinRoomModel.appId = AppData.getInstance().getSelectRtcConfig().appId;

        engine.joinRoom(audioJoinRoomModel);
        engine.setEventListener(eventHandler);
        engine.setAudioRouteToSpeaker(true);
    }

    private void initEngine(RoomInfoModel model, Runnable runnable) {
        isInitEngine = true;
        RTCManager.applyRtcEngine(model.rtiToken, model.rtcToken, runnable);
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
     */
    public void sendCommand(String command) {
        sendCommand(command, null);
    }

    /**
     * 发送信令
     *
     * @param command 信令内容
     * @param result  回调
     */
    public void sendCommand(String command, ISudAudioEngine.SendCommandListener result) {
        LogUtils.d("sendCommand:" + command);
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.sendCommand(command, new ISudAudioEngine.SendCommandListener() {
                @Override
                public void onResult(int value) {
                    LogUtils.d("sendCommand onResult:" + value + "---:" + command);
                    if (result != null) {
                        result.onResult(value);
                    }
                }
            });
        }
    }

    /**
     * 发送跨房信令
     *
     * @param roomID  房间ID
     * @param command 信令内容
     */
    public void sendXRoomCommand(String roomID, String command) {
        sendXRoomCommand(roomID, command, null);
    }

    /**
     * 发送跨房信令
     *
     * @param roomID  房间ID
     * @param command 信令内容
     * @param result  回调
     */
    public void sendXRoomCommand(String roomID, String command, ISudAudioEngine.SendCommandListener result) {
        LogUtils.d("sendXRoomCommand:" + command);
        IMRoomManager.sharedInstance().sendXRoomCommand(roomID, command, new ISudAudioEngine.SendCommandListener() {
            @Override
            public void onResult(int value) {
                LogUtils.d("sendXRoomCommand onResult:" + value + "---:" + command);
                if (result != null) {
                    result.onResult(value);
                }
            }
        });
    }

    /**
     * 开启推流
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

    /** 开始拉视频流 */
    public void startVideo(String streamID, MediaViewMode mediaViewMode, View view) {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            videoView = view;
            engine.startPlayingStream(streamID, mediaViewMode, view);
        }
    }

    /** 停止视频流 */
    public void stopVideo(String streamID, View view) {
        ISudAudioEngine engine = getEngine();
        if (engine != null && videoView == view) {
            engine.stopPlayingStream(streamID);
            videoView = null;
        }
    }

    /** 停止视频流 */
    public void stopVideo(String streamID) {
        ISudAudioEngine engine = getEngine();
        if (engine != null) {
            engine.stopPlayingStream(streamID);
            videoView = null;
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
        public void onRecvXRoomCommand(String fromRoomID, String fromUserID, String command) {
            commandManager.onRecvCommand(fromUserID, command);
        }

        @Override
        public void onRoomOnlineUserCountUpdate(int count) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onRoomOnlineUserCountUpdate(count);
            }
        }

        @Override
        public void onRoomStateUpdate(AudioRoomState state, int errorCode, JSONObject extendedData) {
            if (state == AudioRoomState.CONNECTED) { // 连接成功之后发送进房信令
                sendCommand(RoomCmdModelUtils.buildEnterRoomCommand(), null);
                enterRoomCompleted();
            }
        }

        @Override
        public void onCapturedPCMData(AudioPCMData audioPCMData) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onCapturedAudioData(audioPCMData);
            }
        }

        @Override
        public void onPlayingStreamingAdd(String streamID) {

        }

        @Override
        public void onPlayingStreamingDelete(String streamID) {

        }
    };

    private final ZIMManager.ZimListener zimListener = new ZIMManager.ZimListener() {
        @Override
        public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event, JSONObject extendedData) {
            if (state == null) {
                return;
            }
            // zim断线重连
            LogUtils.file("zim:onConnectionStateChanged:" + state);
        }

        @Override
        public void onError(ZIM zim, ZIMError errorInfo) {
            LogUtils.file("zim:onError:" + errorInfo.code);
        }

        @Override
        public void onLoggedIn(ZIMError errorInfo) {
            LogUtils.file("zim:onLoggedIn:" + errorInfo.code);
        }

        @Override
        public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
            LogUtils.file("zim:onRoomEntered:" + errorInfo.code + " message:" + errorInfo.message);
            if (errorInfo.code != ZIMErrorCode.SUCCESS) {
                ZIMRoomMemberQueryConfig config = new ZIMRoomMemberQueryConfig();
                config.count = 10;
                ZIMManager.sharedInstance().queryRoomMemberList(parentManager.getRoomId() + "", config, new ZIMRoomMemberQueriedCallback() {
                    @Override
                    public void onRoomMemberQueried(String roomID, ArrayList<ZIMUserInfo> memberList, String nextFlag, ZIMError errorInfo) {
                        LogUtils.file("zim:onRoomMemberQueried:" + errorInfo.code + " msg:" + errorInfo.message);
                        if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                            boolean existsZim = selfExistsZim(memberList);
                            LogUtils.file("zim:onRoomMemberQueried-existsZim:" + existsZim);
                            if (!existsZim) {
                                // 不存在此房间内，离开房间再次进入该房间
                                zimRejoinRoom();
                            }
                        }
                    }

                    private boolean selfExistsZim(ArrayList<ZIMUserInfo> memberList) {
                        if (memberList != null) {
                            String userId = HSUserInfo.userId + "";
                            for (ZIMUserInfo zimUserInfo : memberList) {
                                if (zimUserInfo != null && userId.equals(zimUserInfo.userID)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        }

        @Override
        public void onRoomStateChanged(ZIMRoomState state, String roomID) {
            if (state == null || roomID == null || !roomID.equals(parentManager.getRoomId() + "")) {
                return;
            }
            LogUtils.file("zim:onRoomStateChanged:" + state + "  roomID:" + roomID);
            zimRoomState = state;
            if (state == ZIMRoomState.DISCONNECTED) {
                delayZimJoinRoom(100);
            }
        }
    };

    private void zimRejoinRoom() {
        removeDelayZimJoinRoom();
        IMRoomManager.sharedInstance().destroy();
        initZim(parentManager.getRoomInfoModel());
    }

    private void delayZimJoinRoom(long delay) {
        if (handler == null) {
            return;
        }
        if (zimRoomState != null && zimRoomState == ZIMRoomState.CONNECTED) {
            return;
        }
        removeDelayZimJoinRoom();
        handler.postDelayed(zimJoinRoomTask, delay);
    }

    private void removeDelayZimJoinRoom() {
        handler.removeCallbacks(zimJoinRoomTask);
    }

    private final Runnable zimJoinRoomTask = new Runnable() {
        @Override
        public void run() {
            RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
            if (roomInfoModel != null) {
                zimJoinRoom(roomInfoModel);
            }
            delayZimJoinRoom(5000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        commandManager.onDestroy();
        AudioEngineFactory.destroy();
        IMRoomManager.sharedInstance().destroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public interface OnRoomStreamUpdateListener {
        void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData);
    }

    public boolean isEnterRoomCompleted() {
        return enterRoomCompleted;
    }

    private void enterRoomCompleted() {
        if (enterRoomCompleted) return;
        enterRoomCompleted = true;
        SceneRoomServiceManager.EnterRoomCompletedListener listener = enterRoomCompletedListener;
        if (listener != null) {
            listener.onEnterRoomCompleted();
        }
    }

}
