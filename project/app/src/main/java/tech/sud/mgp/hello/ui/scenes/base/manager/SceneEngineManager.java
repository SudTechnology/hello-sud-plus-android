package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.common.http.param.RetCode;
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
import tech.sud.mgp.rtc.audio.core.SudAudioPlayListener;
import tech.sud.mgp.rtc.audio.core.SudAudioSource;
import tech.sud.mgp.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.rtc.audio.impl.agora.AgoraAudioEngineImpl;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;
import tech.sud.mgp.sudmgpim.SudIMRoomManager;
import tech.sud.mgp.sudmgpimcore.listener.SendXCommandListener;
import tech.sud.mgp.sudmgpimcore.listener.SudIMListener;
import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionEvent;
import tech.sud.mgp.sudmgpimcore.model.SudIMConnectionState;
import tech.sud.mgp.sudmgpimcore.model.SudIMError;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomFullInfo;
import tech.sud.mgp.sudmgpimcore.model.SudIMRoomState;

/**
 * 语音引擎
 */
public class SceneEngineManager extends BaseServiceManager {
    private final SceneRoomServiceManager parentManager;

    public final SceneCommandManager commandManager = new SceneCommandManager();
    private OnRoomStreamUpdateListener onRoomStreamUpdateListener;
    private boolean isOpenListen = false; // 标识音频监听开关状态
    private boolean enterRoomCompleted = false; // 标识是否进房成功
    private boolean isInitEngine = false; // 标识是否已初始化engine
    public SceneRoomServiceManager.EnterRoomCompletedListener enterRoomCompletedListener;
    private View videoView;
    private Handler handler;
    private SudIMRoomState sudImRoomState;

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
            LogUtils.file("enterRoom:" + SudJsonUtils.toJson(model));
            initIm(model);
            return;
        }

        joinRoom(model);

        imJoinRoom(model);
    }

    private void initIm(RoomInfoModel model) {
        LogUtils.file("initIm");
        BaseConfigResp baseConfigResp = (BaseConfigResp) GlobalCache.getInstance().getSerializable(GlobalCache.BASE_CONFIG_KEY);
        if (baseConfigResp != null && baseConfigResp.agoraCfg != null) {
            SudIMRoomManager.sharedInstance().init(Utils.getApp(), baseConfigResp.agoraCfg.appId, HSUserInfo.userId + "", imListener);
            imJoinRoom(model);
        }
    }

    private void imJoinRoom(RoomInfoModel model) {
        LogUtils.file("imJoinRoom " + " userId:" + HSUserInfo.userId + "  roomInfoModel:" + SudJsonUtils.toJson(model));
        SudIMRoomManager.sharedInstance().joinRoom(model.roomId + "", HSUserInfo.userId + "",
                HSUserInfo.nickName, model.imToken);
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
            if (engine instanceof AgoraAudioEngineImpl) {
                SudIMRoomManager.sharedInstance().sendXRoomCommand(parentManager.getRoomId() + "", command, new SendXCommandListener() {
                    @Override
                    public void onResult(int value) {
                        if (result != null) {
                            result.onResult(value);
                        }
                    }
                });
            } else {
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
        SudIMRoomManager.sharedInstance().sendXRoomCommand(roomID, command, new SendXCommandListener() {
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
        private String mSelfUserId = HSUserInfo.userId + "";

        /**
         * 捕获本地音量变化, 可用于展示自己说话音浪大小
         *
         * @param soundLevel 本地音量级别，取值范围[0, 100]
         */
        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            if (parentManager.sceneStreamManager.isPublishingStream()) {
                int selfMicIndex = parentManager.sceneMicManager.findSelfMicIndex();
                if (selfMicIndex >= 0) {
                    SceneRoomServiceCallback callback = parentManager.getCallback();
                    if (callback != null) {
                        callback.onSoundLevel(mSelfUserId, selfMicIndex, soundLevel);
                    }
                }
            }
        }

        /**
         * 捕获远程音流音量变化, 可用于展示远端说话音浪大小
         *
         * @param soundLevels [userId: 音量]，音量取值范围[0, 100]
         */
        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
            if (soundLevels == null || soundLevels.size() == 0) {
                return;
            }
            Set<Map.Entry<String, Float>> entries = soundLevels.entrySet();
            for (Map.Entry<String, Float> entry : entries) {
                String userIdStr = entry.getKey();
                Float soundLevel = entry.getValue();
                if (soundLevel != null) {
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
                            callback.onSoundLevel(userIdStr, micIndex, soundLevel);
                        }
                    }
                }
            }
        }

        /**
         * 房间流更新 增、减。可用于知道当前推流人数
         *
         * @param roomId       房间id
         * @param type         流更新类型 增，减
         * @param streamList   变动流列表
         * @param extendedData 扩展信息
         */
        @Override
        public void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData) {
            OnRoomStreamUpdateListener listener = onRoomStreamUpdateListener;
            if (listener != null) {
                listener.onRoomStreamUpdate(roomId, type, streamList, extendedData);
            }
        }

        /**
         * 接收自定义指令信息回调
         *
         * @param fromUserID 用户
         * @param command    指令内容
         */
        @Override
        public void onRecvCommand(String fromUserID, String command) {
            commandManager.onRecvCommand(fromUserID, command);
        }

        /**
         * 房间内当前在线用户数量回调
         *
         * @param count 人数
         */
        @Override
        public void onRoomOnlineUserCountUpdate(int count) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onRoomOnlineUserCountUpdate(count);
            }
        }

        /**
         * 房间状态变化
         *
         * @param state        状态
         * @param errorCode    错误码
         * @param extendedData 扩展信息
         */
        @Override
        public void onRoomStateUpdate(AudioRoomState state, int errorCode, JSONObject extendedData) {
            if (state == AudioRoomState.CONNECTED) { // 连接成功之后发送进房信令
                sendCommand(RoomCmdModelUtils.buildEnterRoomCommand(), null);
                enterRoomCompleted();
            }
        }

        /**
         * 监听音频PCM流回调
         *
         * @param audioPCMData 音频流数据
         */
        @Override
        public void onCapturedPCMData(AudioPCMData audioPCMData) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onCapturedAudioData(audioPCMData);
            }
        }

        /**
         * 观众拉流成功通知
         *
         * @param streamID
         */
        @Override
        public void onPlayingStreamingAdd(String streamID) {
        }

        /**
         * 观众拉流结束通知
         *
         * @param streamID
         */
        @Override
        public void onPlayingStreamingDelete(String streamID) {
        }

        /**
         * 拉流分辨率变更通知。
         *
         * @param streamID 流id
         * @param width    宽
         * @param height   高
         */
        @Override
        public void onPlayerVideoSizeChanged(String streamID, int width, int height) {
            SceneRoomServiceCallback callback = parentManager.getCallback();
            if (callback != null) {
                callback.onPlayerVideoSizeChanged(streamID, width, height);
            }
        }
    };

    private final SudIMListener imListener = new SudIMListener() {
        @Override
        public void onConnectionStateChanged(SudIMConnectionState state, SudIMConnectionEvent event, JSONObject extendedData) {
            if (state == null) {
                return;
            }
            // im断线重连
            LogUtils.file("im:onConnectionStateChanged:" + state);
        }

        @Override
        public void onError(SudIMError errorInfo) {
            LogUtils.file("im:onError:" + errorInfo.code);
        }

        @Override
        public void onLoggedIn(SudIMError errorInfo) {
            LogUtils.file("im:onLoggedIn:" + errorInfo.code);
        }

        @Override
        public void onRoomEntered(SudIMRoomFullInfo roomInfo, SudIMError errorInfo) {
            LogUtils.file("im:onRoomEntered:" + errorInfo.code + " message:" + errorInfo.message);
            if (errorInfo.code != RetCode.SUCCESS) {
                SudIMRoomManager.sharedInstance().checkConnected(parentManager.getRoomId() + "", HSUserInfo.userId + "");
            }
        }

        @Override
        public void onRoomStateChanged(SudIMRoomState state, String roomID) {
            if (state == null || roomID == null || !roomID.equals(parentManager.getRoomId() + "")) {
                return;
            }
            LogUtils.file("im:onRoomStateChanged:" + state + "  roomID:" + roomID);
            sudImRoomState = state;
            if (state == SudIMRoomState.DISCONNECTED) {
                delayImJoinRoom(100);
            }
        }

        @Override
        public void onRejoinRoom(String roomId, String userId) {
            imRejoinRoom();
        }

        /**
         * 接收跨房指令信息回调
         *
         * @param fromRoomID 消息的房间 ID
         * @param fromUserID 消息的用户 ID
         * @param command    指令内容
         */
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

    };

    private void imRejoinRoom() {
        removeDelayImJoinRoom();
        SudIMRoomManager.sharedInstance().destroy();
        initIm(parentManager.getRoomInfoModel());
    }

    private void delayImJoinRoom(long delay) {
        if (handler == null) {
            return;
        }
        if (sudImRoomState != null && sudImRoomState == SudIMRoomState.CONNECTED) {
            return;
        }
        removeDelayImJoinRoom();
        handler.postDelayed(imJoinRoomTask, delay);
    }

    private void removeDelayImJoinRoom() {
        handler.removeCallbacks(imJoinRoomTask);
    }

    private final Runnable imJoinRoomTask = new Runnable() {
        @Override
        public void run() {
            RoomInfoModel roomInfoModel = parentManager.getRoomInfoModel();
            if (roomInfoModel != null) {
                imJoinRoom(roomInfoModel);
            }
            delayImJoinRoom(5000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        commandManager.onDestroy();
        AudioEngineFactory.destroy();
        SudIMRoomManager.sharedInstance().destroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public void playAudio(byte[] audioDatas, SudAudioPlayListener sudAudioPlayListener) {
        SudAudioSource sudAudioSource = new SudAudioSource();
        sudAudioSource.audioDatas = audioDatas;
        sudAudioSource.sudAudioPlayListener = sudAudioPlayListener;
        getEngine().playAudio(sudAudioSource);
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
