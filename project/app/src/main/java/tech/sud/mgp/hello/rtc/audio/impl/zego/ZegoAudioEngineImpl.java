package tech.sud.mgp.hello.rtc.audio.impl.zego;

import android.app.Application;
import android.content.Context;

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
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoAudioFrameParam;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoRoomConfig;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.hello.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

// 即构SDK实现
public class ZegoAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private String mRoomId;

    @Override
    public void setEventListener(ISudAudioEventListener listener) {
        mISudAudioEventListener = listener;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model) {
        if (model == null)
            return;

        ZegoEngineConfig engineConfig = new ZegoEngineConfig();
        engineConfig.advancedConfig.put("init_domain_name", "ze-config.divtoss.com");
        engineConfig.advancedConfig.put("audio_capture_dummy", "true");
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
        /* 64个字符，请通过官网注册获取 */
        profile.appSign = model.appKey;
        /* 通用场景接入 */
        profile.scenario = ZegoScenario.COMMUNICATION;
        /* 设置app的application 对象 */
        profile.application = (Application) context.getApplicationContext();
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, mIZegoEventHandler);
        if (engine != null) {
            engine.startSoundLevelMonitor();
        }
    }

    @Override
    public void destroy() {
        ZegoExpressEngine.destroyEngine(null);
        mRoomId = null;
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            ZegoUser zegoUser = new ZegoUser(model.userID, model.userName);
            ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
            zegoRoomConfig.isUserStatusNotify = true;
            zegoRoomConfig.token = model.token;

            engine.loginRoom(model.roomID, zegoUser, zegoRoomConfig);
        }
    }

    @Override
    public void leaveRoom() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.logoutRoom();
            mRoomId = null;
        }
    }

    @Override
    public void startPublishStream() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            String streamId = UUID.randomUUID().toString() + "-" + String.valueOf(new Date().getTime());
            engine.startPublishingStream(streamId);
            engine.enableAudioCaptureDevice(true);
        }
    }

    @Override
    public void stopPublishStream() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.stopPublishingStream();
            engine.enableAudioCaptureDevice(false);
        }
    }

    @Override
    public void startSubscribingStream() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(false);
        }
    }

    @Override
    public void stopSubscribingStream() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(true);
        }
    }

    @Override
    public void startPCMCapture() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            /* 开启获取PCM数据功能 */
            ZegoAudioFrameParam param = new ZegoAudioFrameParam();
            int bitmask = 0;
            // Enable obtaining raw audio data
            param.channel = ZegoAudioChannel.MONO;
            param.sampleRate = ZegoAudioSampleRate.ZEGO_AUDIO_SAMPLE_RATE_16K;
            // Add bitmask and turn on the switch of collecting audio data
            // The bitmask values corresponding to capture, playback and mixing are: CAPTURED=1, PLAYBACK=2, MIXED=4.
            // The final value of bitmask is 7, which means that capture callback,
            // playback callback, and mixing callback will be triggered at the same time.
            // 采集，拉流，混合对应的位掩码值分别是：CAPTURED=1，PLAYBACK=2, MIXED=4，bitmask最终得到的值为7，表示会同时触发采集、拉流、混合的原始数据回调。
            bitmask = bitmask | ZegoAudioDataCallbackBitMask.CAPTURED.value();
            engine.startAudioDataObserver(bitmask, param);

            /* 设置原始音频数据回调 */
            engine.setAudioDataHandler(audioDataHandler);
        }
    }

    @Override
    public void stopPCMCapture() {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.setAudioDataHandler(null);
            engine.stopAudioDataObserver();
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
        if (engine != null) {
            engine.setAudioRouteToSpeaker(enabled);
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
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
                listener.onRoomStateUpdate(roomID, convertAudioRoomState(state), errorCode, extendedData);
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
                ZegoExpressEngine engine = ZegoExpressEngine.getEngine();
                if (engine != null) {
                    switch (updateType) {
                        case ADD:
                            for (ZegoStream zegoStream :streamList) {
                                engine.startPlayingStream(zegoStream.streamID);
                            }
                            break;
                        case DELETE:
                            for (ZegoStream zegoStream :streamList) {
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
                listener.onRoomOnlineUserCountUpdate(roomID, count);
            }
        }
    };

    private final IZegoAudioDataHandler audioDataHandler = new IZegoAudioDataHandler() {
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
