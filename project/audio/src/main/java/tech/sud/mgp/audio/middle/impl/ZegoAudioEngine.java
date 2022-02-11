package tech.sud.mgp.audio.middle.impl;

import com.blankj.utilcode.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.callback.IZegoEventHandler;
import im.zego.zegoexpress.callback.IZegoIMSendCustomCommandCallback;
import im.zego.zegoexpress.constants.ZegoNetworkMode;
import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import im.zego.zegoexpress.constants.ZegoRoomState;
import im.zego.zegoexpress.constants.ZegoScenario;
import im.zego.zegoexpress.constants.ZegoUpdateType;
import im.zego.zegoexpress.entity.ZegoEngineConfig;
import im.zego.zegoexpress.entity.ZegoEngineProfile;
import im.zego.zegoexpress.entity.ZegoStream;
import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;
import tech.sud.mgp.audio.middle.MediaAudioEngineUpdateType;
import tech.sud.mgp.audio.middle.MediaAudioEventHandler;
import tech.sud.mgp.audio.middle.MediaRoomConfig;
import tech.sud.mgp.audio.middle.MediaStream;
import tech.sud.mgp.audio.middle.MediaUser;

// 即构SDK实现
public class ZegoAudioEngine implements MediaAudioEngineProtocol {

    private MediaAudioEventHandler mMediaAudioEventHandler;

    private ZegoExpressEngine getEngine() {
        return ZegoExpressEngine.getEngine();
    }

    @Override
    public void setEventHandler(MediaAudioEventHandler handler) {
        mMediaAudioEventHandler = handler;
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
        // 推送音乐流时需要开启
//        if (engine != null) {
//            engine.enableAudioCaptureDevice(false);
//        }
    }

    @Override
    public void destroy() {
        ZegoExpressEngine.destroyEngine(null);
    }

    @Override
    public void loginRoom(String roomId, MediaUser user, MediaRoomConfig config) {
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
        }
    }

    @Override
    public void stopPublishStream() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.stopPublishingStream();
        }
    }

    @Override
    public boolean isPublishing() {
        return false;
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
    public void mutePlayStreamAudio(String streamId, boolean isMute) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.mutePlayStreamAudio(streamId, isMute);
        }
    }

    @Override
    public void muteAllPlayStreamAudio(boolean isMute) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllPlayStreamAudio(isMute);
        }
    }

    @Override
    public boolean isMuteAllPlayStreamAudio() {
        return false;
    }

    @Override
    public void setPlayVolume(String streamId, int volume) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.setPlayVolume(streamId, volume);
        }
    }

    @Override
    public void setAllPlayStreamVolume(int volume) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.setAllPlayStreamVolume(volume);
        }
    }

    @Override
    public void muteMicrophone(boolean isMute) {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.muteMicrophone(isMute);
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
    public void startSoundLevelMonitor() {
        ZegoExpressEngine engine = getEngine();
        if (engine != null) {
            engine.startSoundLevelMonitor();
        }
    }

    private final IZegoEventHandler mIZegoEventHandler = new IZegoEventHandler() {

        @Override
        public void onRoomStateUpdate(String roomID, ZegoRoomState state, int errorCode, JSONObject extendedData) {
            super.onRoomStateUpdate(roomID, state, errorCode, extendedData);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRoomStateUpdate(roomID, ZegoRoomStateConverter.converAudioRoomState(state), errorCode, extendedData);
            }
        }

        @Override
        public void onCapturedSoundLevelUpdate(float soundLevel) {
            super.onCapturedSoundLevelUpdate(soundLevel);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onCapturedSoundLevelUpdate(soundLevel);
            }
        }

        @Override
        public void onRemoteSoundLevelUpdate(HashMap<String, Float> soundLevels) {
            super.onRemoteSoundLevelUpdate(soundLevels);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRemoteSoundLevelUpdate(soundLevels);
            }
        }

        @Override
        public void onRoomStreamUpdate(String roomID, ZegoUpdateType updateType, ArrayList<ZegoStream> streamList, JSONObject extendedData) {
            super.onRoomStreamUpdate(roomID, updateType, streamList, extendedData);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                MediaAudioEngineUpdateType mediaAudioEngineUpdateType = ZegoUpdateTypeConverter.converMediaAudioEnginUpdateType(updateType);
                List<MediaStream> mediaStreamList = ZegoStreamConverter.converMediaStreamList(streamList);
                handler.onRoomStreamUpdate(roomID, mediaAudioEngineUpdateType, mediaStreamList, extendedData);
            }
        }

        @Override
        public void onPublisherStateUpdate(String streamID, ZegoPublisherState state, int errorCode, JSONObject extendedData) {
            super.onPublisherStateUpdate(streamID, state, errorCode, extendedData);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onPublisherStateUpdate(streamID, ZegoPublisherStateConverter.converMediaStateType(state), errorCode, extendedData);
            }
        }

        @Override
        public void onPlayerStateUpdate(String streamID, ZegoPlayerState state, int errorCode, JSONObject extendedData) {
            super.onPlayerStateUpdate(streamID, state, errorCode, extendedData);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onPlayerStateUpdate(streamID, ZegoPlayerStateConverter.converMediaStateType(state), errorCode, extendedData);
            }
        }

        @Override
        public void onNetworkModeChanged(ZegoNetworkMode mode) {
            super.onNetworkModeChanged(mode);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onNetworkModeChanged(ZegoNetworkModeConverter.converMediaNetwork(mode));
            }
        }

        @Override
        public void onIMRecvCustomCommand(String roomID, ZegoUser fromUser, String command) {
            super.onIMRecvCustomCommand(roomID, fromUser, command);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onIMRecvCustomCommand(roomID, ZegoUserConverter.converMediaUser(fromUser), command);
            }
        }

        @Override
        public void onRoomOnlineUserCountUpdate(String roomID, int count) {
            super.onRoomOnlineUserCountUpdate(roomID, count);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRoomOnlineUserCountUpdate(roomID, count);
            }
        }

    };

}
