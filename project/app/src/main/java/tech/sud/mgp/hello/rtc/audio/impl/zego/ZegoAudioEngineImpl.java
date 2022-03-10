package tech.sud.mgp.hello.rtc.audio.impl.zego;

import com.blankj.utilcode.util.Utils;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.hello.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomConfig;
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEventHandler;

// 即构SDK实现
public class ZegoAudioEngineImpl implements IAudioEngine {

    private IAudioEventHandler mIAudioEventHandler;

    private ZegoExpressEngine getEngine() {
        return ZegoExpressEngine.getEngine();
    }

    @Override
    public void setEventHandler(IAudioEventHandler handler) {
        mIAudioEventHandler = handler;
    }

    @Override
    public void config(String appId, String appKey) {
        ZegoEngineConfig engineConfig = new ZegoEngineConfig();
        engineConfig.advancedConfig.put("init_domain_name", "ze-config.divtoss.com");
        engineConfig.advancedConfig.put("audio_capture_dummy", "true");
        ZegoExpressEngine.setEngineConfig(engineConfig);
        long appIdL = 0;
        try {
            appIdL = Long.parseLong(appId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = appIdL;
        profile.appSign = appKey;
        profile.scenario = ZegoScenario.GENERAL;
        profile.application = Utils.getApp();
        ZegoExpressEngine engine = ZegoExpressEngine.createEngine(profile, mIZegoEventHandler);
        if (engine != null) {
//            engine.enableAudioCaptureDevice(false); // 推送音乐流时需要开启
            engine.startSoundLevelMonitor();
        }
    }

    @Override
    public void destroy() {
        ZegoExpressEngine.destroyEngine(null);
    }

    @Override
    public void loginRoom(String roomId, AudioUser user, AudioRoomConfig config) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.loginRoom(roomId, ZegoUserConverter.converZegoUser(user), ZegoRoomConfigConverter.converZegoRoomConfig(config));
        }
    }

    @Override
    public void logoutRoom() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.logoutRoom();
        }
    }

    @Override
    public void startPublish(String streamId) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.startPublishingStream(streamId);
            engine.enableAudioCaptureDevice(true);
        }
    }

    @Override
    public void stopPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPublishingStream();
            engine.enableAudioCaptureDevice(false);
        }
    }

    @Override
    public void startPlayingStream(String streamId) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.startPlayingStream(streamId);
        }
    }

    @Override
    public void stopPlayingStream(String streamId) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPlayingStream(streamId);
        }
    }

    @Override
    public void startSubscribing() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(false);
        }
    }

    @Override
    public void stopSubscribing() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(true);
        }
    }

    @Override
    public void sendCommand(String roomId, String command, SendCommandResult result) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.sendCustomCommand(roomId, command, null, new IZegoIMSendCustomCommandCallback() {
                @Override
                public void onIMSendCustomCommandResult(int errorCode) {
                    if (result != null) {
                        result.result(errorCode);
                    }
                }
            });
        }
    }

    @Override
    public void setAudioDataHandler() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.setAudioDataHandler(audioDataHandler);
        }
    }

    @Override
    public void startAudioDataListener() {
        ZegoExpressEngine engine = getEngine();
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
        if (engine != null) {
            engine.startAudioDataObserver(bitmask, param);
        }
    }

    @Override
    public void stopAudioDataListener() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopAudioDataObserver();
        }
    }

    private final IZegoEventHandler mIZegoEventHandler = new IZegoEventHandler() {

        // [streamId：UserId]
        private HashMap<String, String> streamUserMaps = new HashMap<>();

        @Override
        public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
            super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onRoomStateUpdate(roomID, ZegoRoomStateConverter.converAudioRoomState(state), errorCode, extendedData);
            }
        }

        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            super.onCapturedSoundLevelUpdate(soundLevel);
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onCapturedSoundLevelUpdate(soundLevel);
            }
        }

        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
            super.onRemoteSoundLevelUpdate(soundLevels);
            if (soundLevels == null || soundLevels.size() == 0) {
                return;
            }
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                // soundLevels里的key是streamId，将其转换成userId
                HashMap<String, Float> userSoundLevels = new HashMap<>();
                Set<String> keySet = soundLevels.keySet();
                for (String streamId : keySet) {
                    userSoundLevels.put(streamUserMaps.get(streamId), soundLevels.get(streamId));
                }
                handler.onRemoteSoundLevelUpdate(userSoundLevels);
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

            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                AudioEngineUpdateType mediaAudioEngineUpdateType = ZegoUpdateTypeConverter.converMediaAudioEnginUpdateType(updateType);
                List<AudioStream> audioStreamList = ZegoStreamConverter.converMediaStreamList(streamList);
                handler.onRoomStreamUpdate(roomID, mediaAudioEngineUpdateType, audioStreamList, extendedData);
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
            super.onIMRecvCustomCommand(roomID, fromUser, command);
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onIMRecvCustomCommand(roomID, ZegoUserConverter.converMediaUser(fromUser), command);
            }
        }

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            super.onRoomOnlineUserCountUpdate(roomID, count);
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onRoomOnlineUserCountUpdate(roomID, count);
            }
        }
    };

    private final IZegoAudioDataHandler audioDataHandler = new IZegoAudioDataHandler() {
        @Override
        public void onCapturedAudioData(ByteBuffer data, int dataLength, ZegoAudioFrameParam param) {
            super.onCapturedAudioData(data, dataLength, param);
            IAudioEventHandler handler = mIAudioEventHandler;
            if (handler != null) {
                AudioPCMData audioPCMData = new AudioPCMData();
                audioPCMData.data = data;
                audioPCMData.dataLength = dataLength;
                handler.onCapturedAudioData(audioPCMData);
            }
        }
    };

}
