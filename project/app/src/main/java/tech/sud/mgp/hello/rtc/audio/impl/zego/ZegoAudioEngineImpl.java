package tech.sud.mgp.hello.rtc.audio.impl.zego;

import com.blankj.utilcode.util.Utils;

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
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

// 即构SDK实现
public class ZegoAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mIAudioEventHandler;
    private String mRoomId;

    private ZegoExpressEngine getEngine() {
        return ZegoExpressEngine.getEngine();
    }

    @Override
    public void setEventListener(ISudAudioEventListener handler) {
        mIAudioEventHandler = handler;
    }

    @Override
    public void initWithConfig(AudioConfigModel model) {
        if (model == null)
            return;

        ZegoEngineConfig engineConfig = new ZegoEngineConfig();
        engineConfig.advancedConfig.put("init_domain_name", "ze-config.divtoss.com");
        engineConfig.advancedConfig.put("audio_capture_dummy", "true");
        ZegoExpressEngine.setEngineConfig(engineConfig);
        long appIdL = 0;
        try {
            appIdL = Long.parseLong(model.appId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ZegoEngineProfile profile = new ZegoEngineProfile();
        profile.appID = appIdL;
        profile.appSign = model.appSign;
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
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            ZegoUser zegoUser = new ZegoUser(model.userID, model.userName);
            ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
            zegoRoomConfig.maxMemberCount = model.maxMemberCount;
            zegoRoomConfig.isUserStatusNotify = model.isUserStatusNotify;
            zegoRoomConfig.token = model.token;

            engine.loginRoom(model.roomID, zegoUser, zegoRoomConfig);
        }
    }

    @Override
    public void leaveRoom() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.logoutRoom();
        }
    }

    @Override
    public void startPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            String streamId = UUID.randomUUID().toString() + "-" + String.valueOf(new Date().getTime());
            engine.startPublishingStream(streamId);
//            engine.enableAudioCaptureDevice(true);
        }
    }

    @Override
    public void stopPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPublishingStream();
//            engine.enableAudioCaptureDevice(false);
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
    public void startPCMCapture() {
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
            engine.setAudioDataHandler(audioDataHandler);
        }
    }

    @Override
    public void stopPCMCapture() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopAudioDataObserver();
            engine.setAudioDataHandler(null);
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {

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

            ISudAudioEventListener handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onRoomStateUpdate(roomID, ZegoRoomStateConverter.converAudioRoomState(state), errorCode, extendedData);
            }
        }

        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            super.onCapturedSoundLevelUpdate(soundLevel);
            ISudAudioEventListener handler = mIAudioEventHandler;
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
            ISudAudioEventListener handler = mIAudioEventHandler;
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

            if (streamList != null && streamList.size() > 0) {
                ZegoExpressEngine engine = getEngine();
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

            ISudAudioEventListener handler = mIAudioEventHandler;
            if (handler != null) {
                AudioEngineUpdateType mediaAudioEngineUpdateType = ZegoUpdateTypeConverter.converMediaAudioEnginUpdateType(updateType);
                List<AudioStream> audioStreamList = ZegoStreamConverter.converMediaStreamList(streamList);
                handler.onRoomStreamUpdate(roomID, mediaAudioEngineUpdateType, audioStreamList, extendedData);
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
            super.onIMRecvCustomCommand(roomID, fromUser, command);
            ISudAudioEventListener handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onRecvCommand(ZegoUserConverter.converMediaUser(fromUser), command);
            }
        }

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            super.onRoomOnlineUserCountUpdate(roomID, count);
            ISudAudioEventListener handler = mIAudioEventHandler;
            if (handler != null) {
                handler.onRoomOnlineUserCountUpdate(roomID, count);
            }
        }
    };

    private final IZegoAudioDataHandler audioDataHandler = new IZegoAudioDataHandler() {
        @Override
        public void onCapturedAudioData(ByteBuffer data, int dataLength, ZegoAudioFrameParam param) {
            super.onCapturedAudioData(data, dataLength, param);
            ISudAudioEventListener handler = mIAudioEventHandler;
            if (handler != null) {
                AudioPCMData audioPCMData = new AudioPCMData();
                audioPCMData.data = data;
                audioPCMData.dataLength = dataLength;
                handler.onCapturedPCMData(audioPCMData);
            }
        }
    };

}
