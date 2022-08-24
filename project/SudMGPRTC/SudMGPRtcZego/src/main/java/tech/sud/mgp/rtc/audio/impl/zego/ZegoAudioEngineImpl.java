package tech.sud.mgp.rtc.audio.impl.zego;

import android.app.Application;
import android.content.Context;
import android.view.View;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoAudioDataHandler;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.constants.ZegoAudioChannel;
import im.zego.zegoexpress.constants.ZegoAudioDataCallbackBitMask;
import im.zego.zegoexpress.constants.ZegoAudioSampleRate;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoAudioFrameParam;
import im.zego.zegoexpress.entity.ZegoCanvas;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.rtc.audio.core.AudioStream;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;

// 即构SDK实现
public class ZegoAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private String mRoomId;

    private ZegoExpressEngine getEngine() {
        return ZegoExpressEngine.getEngine();
    }

    @Override
    public void setEventListener(ISudAudioEventListener listener) {
        mISudAudioEventListener = listener;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {
        if (model == null)
            return;

        ZegoEngineConfig engineConfig = new ZegoEngineConfig();
        engineConfig.advancedConfig.put("init_domain_name", "ze-config.divtoss.com");
        engineConfig.advancedConfig.put("audio_capture_dummy", "true");
        engineConfig.advancedConfig.put("video_check_poc", "false");
        ZegoExpressEngine.setEngineConfig(engineConfig);
        long appID = 0;
        try {
            appID = Long.parseLong(model.appId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZegoEngineProfile profile = new ZegoEngineProfile();
        /* 请通过官网注册获取，格式为 123456789L */
        profile.appID = appID;
        /* 通用场景接入 */
        profile.scenario = ZegoScenario.COMMUNICATION;
        /* 设置app的application 对象 */
        profile.application = (Application) context.getApplicationContext();
        /* 创建引擎 */
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, mIZegoEventHandler);
        if (engine != null) {
            engine.startSoundLevelMonitor();
            engine.enableAudioCaptureDevice(false);
            engine.enableAEC(true);
            engine.enableHeadphoneAEC(true);
        }

        if (success != null) {
            success.run();
        }
    }

    @Override
    public void destroy() {
        /* 销毁 SDK */
        ZegoExpressEngine.destroyEngine(null);
        mRoomId = null;
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            /* 创建用户 */
            ZegoUser zegoUser = new ZegoUser(model.userID, model.userName);
            ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
            /* Enable notification when user login or logout */
            zegoRoomConfig.isUserStatusNotify = true;
            zegoRoomConfig.token = model.token;
            /* 开始登陆房间 */
            engine.loginRoom(model.roomID, zegoUser, zegoRoomConfig);
        }
    }

    @Override
    public void leaveRoom() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            /* 退出房间 */
            engine.logoutRoom();
        }
        mRoomId = null;
    }

    /* 开始推流 */
    @Override
    public void startPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            String streamId = UUID.randomUUID().toString() + "-" + String.valueOf(new Date().getTime());
            engine.startPublishingStream(streamId);
            engine.enableAudioCaptureDevice(true);
        }
    }

    /* 停止推流 */
    @Override
    public void stopPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPublishingStream();
            engine.enableAudioCaptureDevice(false);
        }
    }

    @Override
    public void startSubscribingStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(false);
        }
    }

    @Override
    public void stopSubscribingStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(true);
        }
    }

    @Override
    public void startPCMCapture() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            /* 开启获取PCM数据功能 */
            ZegoAudioFrameParam param = new ZegoAudioFrameParam();
            int bitmask = 0;
            param.channel = ZegoAudioChannel.MONO;
            param.sampleRate = ZegoAudioSampleRate.ZEGO_AUDIO_SAMPLE_RATE_16K;
            bitmask |= ZegoAudioDataCallbackBitMask.CAPTURED.value();
            engine.startAudioDataObserver(bitmask, param);

            /* 设置原始音频数据回调 */
            engine.setAudioDataHandler(zegoAudioDataHandler);
        }
    }

    @Override
    public void stopPCMCapture() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            /* 置空原始音频数据回调 */
            engine.setAudioDataHandler(null);
            engine.stopAudioDataObserver();
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.setAudioRouteToSpeaker(enabled);
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.sendCustomCommand(mRoomId, command, null, new IZegoIMSendCustomCommandCallback() {
                @Override
                public void onIMSendCustomCommandResult(int errorCode) {
                    if (listener != null) {
                        listener.onResult(errorCode);
                    }
                }
            });
        }
    }

    @Override
    public void startPlayingStream(String streamID, View view) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            ZegoCanvas canvas = new ZegoCanvas(view);
            engine.startPlayingStream(streamID, canvas);
        }
    }

    @Override
    public void stopPlayingStream(String streamID) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPlayingStream(streamID);
        }
    }

    private AudioRoomState convertAudioRoomState(ZegoRoomState state) {
        switch (state) {
            case DISCONNECTED:
                return AudioRoomState.DISCONNECTED;
            case CONNECTING:
                return AudioRoomState.CONNECTING;
            case CONNECTED:
                return AudioRoomState.CONNECTED;
        }
        return null;
    }

    private AudioEngineUpdateType convertMediaAudioEnginUpdateType(ZegoUpdateType type) {
        switch (type) {
            case ADD:
                return AudioEngineUpdateType.ADD;
            case DELETE:
                return AudioEngineUpdateType.DELETE;
        }
        return null;
    }

    private AudioStream convertMediaStream(ZegoStream stream) {
        if (stream == null) return null;
        AudioStream audioStream = new AudioStream();
        audioStream.userID = stream.user.userID;
        audioStream.streamID = stream.streamID;
        audioStream.extraInfo = stream.extraInfo;
        return audioStream;
    }

    private List<AudioStream> convertMediaStreamList(List<ZegoStream> zegoStreamList) {
        if (zegoStreamList == null || zegoStreamList.size() == 0) {
            return null;
        }
        List<AudioStream> list = new ArrayList<>();
        for (int i = 0; i < zegoStreamList.size(); i++) {
            list.add(convertMediaStream(zegoStreamList.get(i)));
        }
        return list;
    }

    private final IZegoEventHandler mIZegoEventHandler = new IZegoEventHandler() {

        // [streamId：UserId]
        private HashMap<String, String> streamUserMaps = new HashMap<>();

        @Override
        public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
            super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
            if (state == ZegoRoomState.CONNECTED) {
                mRoomId = roomID;
            }

            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                listener.onRoomStateUpdate(convertAudioRoomState(state), errorCode, extendedData);
            }
        }

        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            super.onCapturedSoundLevelUpdate(soundLevel);
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                listener.onCapturedSoundLevelUpdate(soundLevel);
            }
        }

        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
            super.onRemoteSoundLevelUpdate(soundLevels);
            if (soundLevels == null || soundLevels.size() == 0) {
                return;
            }
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                // soundLevels里的key是streamId，将其转换成userId
                HashMap<String, Float> userSoundLevels = new HashMap<>();
                Set<String> keySet = soundLevels.keySet();
                for (String streamId : keySet) {
                    userSoundLevels.put(streamUserMaps.get(streamId), soundLevels.get(streamId));
                }
                listener.onRemoteSoundLevelUpdate(userSoundLevels);
            }
        }

        @Override
        public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
            super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
            // 存储userId对应的streamId
            if (streamList != null) {
                for (ZegoStream zegoStream : streamList) {
                    if (updateType == ZegoUpdateType.ADD) {
                        streamUserMaps.put(zegoStream.streamID, zegoStream.user.userID);
                    } else if (updateType == ZegoUpdateType.DELETE) {
                        streamUserMaps.remove(zegoStream.streamID);
                    }
                }
            }

            if (streamList != null && streamList.size() > 0) {
                ZegoExpressEngine engine = getEngine();
                if (engine != null) {
                    switch (updateType) {
                        case ADD:
                            for (ZegoStream zegoStream : streamList) {
                                engine.startPlayingStream(zegoStream.streamID, null);
                            }
                            break;
                        case DELETE:
                            for (ZegoStream zegoStream : streamList) {
                                engine.stopPlayingStream(zegoStream.streamID);
                            }
                            break;
                    }
                }
            }

            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                AudioEngineUpdateType mediaAudioEngineUpdateType = convertMediaAudioEnginUpdateType(updateType);
                List<AudioStream> audioStreamList = convertMediaStreamList(streamList);
                listener.onRoomStreamUpdate(roomID, mediaAudioEngineUpdateType, audioStreamList, extendedData);
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
            super.onIMRecvCustomCommand(roomID, fromUser, command);
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                listener.onRecvCommand(fromUser.userID, command);
            }
        }

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            super.onRoomOnlineUserCountUpdate(roomID, count);
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                listener.onRoomOnlineUserCountUpdate(count);
            }
        }

        @Override
        public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
            if (state == ZegoPlayerState.PLAYING) {
                if (mISudAudioEventListener != null) {
                    mISudAudioEventListener.onPlayingStreamingAdd(streamID);
                }
            } else if (state == ZegoPlayerState.NO_PLAY) {
                if (mISudAudioEventListener != null) {
                    mISudAudioEventListener.onPlayingStreamingDelete(streamID);
                }
            }
        }
    };

    private final IZegoAudioDataHandler zegoAudioDataHandler = new IZegoAudioDataHandler() {
        @Override
        public void onCapturedAudioData(ByteBuffer data, int dataLength, ZegoAudioFrameParam param) {
            super.onCapturedAudioData(data, dataLength, param);
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                AudioPCMData audioPCMData = new AudioPCMData();
                audioPCMData.data = data;
                audioPCMData.dataLength = dataLength;
                listener.onCapturedPCMData(audioPCMData);
            }
        }
    };
}
