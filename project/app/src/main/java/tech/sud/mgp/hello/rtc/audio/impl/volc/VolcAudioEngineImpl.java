package tech.sud.mgp.hello.rtc.audio.impl.volc;

import static com.ss.bytertc.engine.Constants.CONNECTION_STATE_CONNECTED;
import static com.ss.bytertc.engine.Constants.CONNECTION_STATE_CONNECTING;
import static com.ss.bytertc.engine.Constants.CONNECTION_STATE_DISCONNECTED;
import static com.ss.bytertc.engine.RTCEngine.MediaStreamType.RTC_MEDIA_STREAM_TYPE_AUDIO;
import static com.ss.bytertc.engine.RTCEngine.PauseResumeControlMediaType.RTC_PAUSE_RESUME_CONTROL_AUDIO;
import static com.ss.bytertc.engine.data.AudioPlaybackDevice.AUDIO_PLAYBACK_DEVICE_EARPIECE;
import static com.ss.bytertc.engine.data.AudioPlaybackDevice.AUDIO_PLAYBACK_DEVICE_SPEAKERPHONE;

import android.content.Context;

import com.blankj.utilcode.util.ThreadUtils;
import com.ss.bytertc.engine.IAudioProcessor;
import com.ss.bytertc.engine.RTCEngine;
import com.ss.bytertc.engine.RTCRoomConfig;
import com.ss.bytertc.engine.UserInfo;
import com.ss.bytertc.engine.data.AudioChannel;
import com.ss.bytertc.engine.data.AudioFormat;
import com.ss.bytertc.engine.data.AudioPropertiesConfig;
import com.ss.bytertc.engine.data.AudioSampleRate;
import com.ss.bytertc.engine.data.LocalAudioPropertiesInfo;
import com.ss.bytertc.engine.data.RemoteAudioPropertiesInfo;
import com.ss.bytertc.engine.data.StreamIndex;
import com.ss.bytertc.engine.handler.IRTCEngineEventHandler;
import com.ss.bytertc.engine.utils.IAudioFrame;

import java.util.HashMap;

import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

/**
 * 火山引擎实现类
 */
public class VolcAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private RTCEngine mEngine;

    private RTCEngine getEngine() {
        return mEngine;
    }

    @Override
    public void setEventListener(ISudAudioEventListener listener) {
        mISudAudioEventListener = listener;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {
        if (mEngine != null) {
            destroy();
        }

        mEngine = RTCEngine.createEngine(context, model.appId, irtcEngineEventHandler, null, null);

        AudioPropertiesConfig config = new AudioPropertiesConfig(300);
        mEngine.enableAudioPropertiesReport(config);
    }

    @Override
    public void destroy() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            RTCEngine.destroyEngine(engine);
        }
        mEngine = null;
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        RTCEngine engine = getEngine();
        if (engine != null) {
            RTCRoomConfig roomConfig = new RTCRoomConfig(RTCEngine.ChannelProfile.CHANNEL_PROFILE_COMMUNICATION, false, true, false);
            UserInfo userInfo = UserInfo.create(model.userID, "");

            engine.joinRoom(model.token, model.roomID, userInfo, roomConfig);
        }
    }

    @Override
    public void leaveRoom() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.leaveRoom();
        }
    }

    @Override
    public void startPublishStream() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.startAudioCapture();  // 开启麦克风采集
            engine.publishStream(RTC_MEDIA_STREAM_TYPE_AUDIO);  // 发布本地音频流
        }
    }

    @Override
    public void stopPublishStream() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.unpublishStream(RTC_MEDIA_STREAM_TYPE_AUDIO);  // 取消发布本地音频流
            engine.stopAudioCapture();  // 关闭麦克风采集
        }
    }

    @Override
    public void startSubscribingStream() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.resumeAllSubscribedStream(RTC_PAUSE_RESUME_CONTROL_AUDIO);
        }
    }

    @Override
    public void stopSubscribingStream() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.pauseAllSubscribedStream(RTC_PAUSE_RESUME_CONTROL_AUDIO);
        }
    }

    @Override
    public void startPCMCapture() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            AudioFormat audioFormat = new AudioFormat(AudioSampleRate.AUDIO_SAMPLE_RATE_16000, AudioChannel.AUDIO_CHANNEL_MONO);
            engine.registerLocalAudioProcessor(iAudioProcessor, audioFormat);
        }
    }

    @Override
    public void stopPCMCapture() {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.registerLocalAudioProcessor(null, null);
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        RTCEngine engine = getEngine();
        if (engine != null) {
            if (enabled) {
                engine.setAudioPlaybackDevice(AUDIO_PLAYBACK_DEVICE_SPEAKERPHONE);
            } else {
                engine.setAudioPlaybackDevice(AUDIO_PLAYBACK_DEVICE_EARPIECE);
            }
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        RTCEngine engine = getEngine();
        if (engine != null) {
            engine.sendRoomMessage(command);
        }
    }

    @Override
    public void sendRoomMessage(String roomID, String message, SendCommandListener listener) {
    }

    private AudioRoomState convertAudioRoomState(int state) {
        switch (state) {
            case CONNECTION_STATE_DISCONNECTED:
                return AudioRoomState.DISCONNECTED;
            case CONNECTION_STATE_CONNECTING:
                return AudioRoomState.CONNECTING;
            case CONNECTION_STATE_CONNECTED:
                return AudioRoomState.CONNECTED;
        }
        return null;
    }

    private final IRTCEngineEventHandler irtcEngineEventHandler = new IRTCEngineEventHandler() {
        @Override
        public void onWarning(int warn) {
        }

        @Override
        public void onError(int err) {
        }

        @Override
        public void onLocalAudioPropertiesReport(LocalAudioPropertiesInfo[] audioPropertiesInfos) {
            super.onLocalAudioPropertiesReport(audioPropertiesInfos);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener == null || audioPropertiesInfos == null || audioPropertiesInfos.length == 0) {
                        return;
                    }
                    for (LocalAudioPropertiesInfo localAudioPropertiesInfo : audioPropertiesInfos) {
                        if (localAudioPropertiesInfo.streamIndex == StreamIndex.STREAM_INDEX_MAIN) {
                            float volume = localAudioPropertiesInfo.audioPropertiesInfo.linearVolume * 1.0f / 255 * 100;
                            listener.onCapturedSoundLevelUpdate(volume);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public void onRemoteAudioPropertiesReport(RemoteAudioPropertiesInfo[] audioPropertiesInfos, int totalRemoteVolume) {
            super.onRemoteAudioPropertiesReport(audioPropertiesInfos, totalRemoteVolume);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener == null || audioPropertiesInfos == null || audioPropertiesInfos.length == 0) {
                        return;
                    }

                    HashMap<String, Float> soundLevels = new HashMap<>();
                    for (RemoteAudioPropertiesInfo remoteAudioPropertiesInfo : audioPropertiesInfos) {
                        String userID = remoteAudioPropertiesInfo.streamKey.getUserId();
                        float volume = remoteAudioPropertiesInfo.audioPropertiesInfo.linearVolume * 1.0f / 255 * 100;
                        soundLevels.put(userID, volume);
                    }

                    if (soundLevels.size() > 0) {
                        listener.onRemoteSoundLevelUpdate(soundLevels);
                    }
                }
            });
        }

        @Override
        public void onRoomMessageReceived(String uid, String message) {
            super.onRoomMessageReceived(uid, message);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        listener.onRecvCommand(uid, message);
                    }
                }
            });
        }

        @Override
        public void onRoomStats(RTCRoomStats stats) {
            super.onRoomStats(stats);
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                listener.onRoomOnlineUserCountUpdate(stats.users);
            }
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            AudioRoomState audioRoomState = convertAudioRoomState(state);
            if (audioRoomState != null) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISudAudioEventListener handler = mISudAudioEventListener;
                        if (handler != null) {
                            handler.onRoomStateUpdate(audioRoomState, 0, null);
                        }
                    }
                });
            }
        }
    };

    private final IAudioProcessor iAudioProcessor = new IAudioProcessor() {
        @Override
        public int processAudioFrame(IAudioFrame audioFrame) {
            ISudAudioEventListener handler = mISudAudioEventListener;
            if (handler != null) {
                AudioPCMData audioPCMData = new AudioPCMData();
                audioPCMData.data = audioFrame.getDataBuffer();
                audioPCMData.dataLength = audioFrame.data_size();
                handler.onCapturedPCMData(audioPCMData);
            }
            return 0;
        }
    };
}
