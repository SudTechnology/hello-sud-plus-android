package tech.sud.mgp.rtc.audio.impl.agora;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IAudioFrameObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.audio.AudioParams;
import io.agora.rtc2.video.VideoCanvas;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.rtc.audio.core.MediaViewMode;
import tech.sud.mgp.rtc.audio.core.SudAudioSource;
import tech.sud.mgp.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;

// 声网SDK实现
public class AgoraAudioEngineImpl implements ISudAudioEngine {

    private static final String kTag = "ISudAudioEngine";

    private ISudAudioEventListener mISudAudioEventListener;
    private RtcEngine mEngine;
    private AgoraAudioMsgPlayer mAgoraAudioMsgPlayer;

    // 信令相关
    private boolean isStartedPCMData;

    private RtcEngine getEngine() {
        return mEngine;
    }

    @Override
    public void setEventListener(ISudAudioEventListener listener) {
        mISudAudioEventListener = listener;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                // 初始化引擎
                try {
                    RtcEngineConfig config = new RtcEngineConfig();
                    config.mAppId = model.appId;
                    config.mEventHandler = mIRtcEngineEventHandler;
                    config.mContext = context.getApplicationContext();
                    config.mAreaCode = RtcEngineConfig.AreaCode.AREA_CODE_GLOB;
                    mEngine = RtcEngine.create(config);

                    if (mEngine != null) {
                        // 监听原始音频数据，声网RTC得在加入频道前进行注册和设置
                        mEngine.registerAudioFrameObserver(iAudioFrameObserver);
                        mEngine.setRecordingAudioFrameParameters(16000, 1, Constants.RAW_AUDIO_FRAME_OP_MODE_READ_ONLY, 160);

                        mEngine.setDefaultAudioRoutetoSpeakerphone(true);
                        mEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
                        mEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
                        mEngine.enableAudioVolumeIndication(300, 3, true);

                        if (success != null) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    success.run();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void destroy() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine.destroy();
                mEngine = null;
            }
        });
    }

    /**
     * @param model model.token, 这里的token是RTC的token，用于语音通话
     */
    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {

                    // 默认关闭麦克风，关掉推流
                    engine.enableLocalAudio(false);
                    ChannelMediaOptions channelMediaOptions = new ChannelMediaOptions();
                    channelMediaOptions.autoSubscribeAudio = true;
                    channelMediaOptions.autoSubscribeVideo = true;
//                    channelMediaOptions.publishLocalAudio = false;
//                    channelMediaOptions.publishLocalVideo = false;
                    int uid;
                    try {
                        uid = Integer.parseInt(model.userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.showLong("uid转int失败:" + model.userID);
                        return;
                    }

                    // 加入频道
                    engine.joinChannel(model.token, model.roomID, uid, channelMediaOptions);
//                    engine.joinChannelWithUserAccount(model.token, model.roomID, model.userID,channelMediaOptions);
                    engine.setEnableSpeakerphone(true); // 开启。音频路由为扬声器。
                }
            }
        });
    }

    @Override
    public void leaveRoom() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.leaveChannel();
                }
            }
        });
    }

    @Override
    public void startPublishStream() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.enableLocalAudio(true); // 开启麦克风采集
                    engine.muteLocalAudioStream(false); // 发布本地音频流
                }
            }
        });
    }

    @Override
    public void stopPublishStream() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.enableLocalAudio(false); // 关闭麦克风采集
                    engine.muteLocalAudioStream(true); // 取消发布本地音频流
                }
            }
        });
    }

    @Override
    public void startSubscribingStream() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.muteAllRemoteAudioStreams(false);
                }
            }
        });
    }

    @Override
    public void stopSubscribingStream() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.muteAllRemoteAudioStreams(true);
                }
            }
        });
    }

    @Override
    public void startPCMCapture() {
        isStartedPCMData = true;
    }

    @Override
    public void stopPCMCapture() {
        isStartedPCMData = false;
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    engine.setEnableSpeakerphone(enabled);
                }
            }
        });
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {

    }

    @Override
    public void startPlayingStream(String streamID, MediaViewMode mediaViewMode, View view) {
        int uid;
        try {
            uid = Integer.parseInt(streamID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // 将 SurfaceView 对象传入声网实时互动 SDK，设置远端视图
        mEngine.setupRemoteVideo(new VideoCanvas(view, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    @Override
    public void stopPlayingStream(String streamID) {
    }

    @Override
    public void playAudio(SudAudioSource audioSource) {
        if (audioSource == null) {
            return;
        }
        if (mAgoraAudioMsgPlayer == null) {
            mAgoraAudioMsgPlayer = new AgoraAudioMsgPlayer();
        }
        mAgoraAudioMsgPlayer.play(mEngine, audioSource.audioDatas, audioSource.sudAudioPlayListener);
    }

    private AudioRoomState convertAudioRoomState(int state) {
        switch (state) {
            case Constants.CONNECTION_STATE_DISCONNECTED:
                return AudioRoomState.DISCONNECTED;
            case Constants.CONNECTION_STATE_CONNECTING:
                return AudioRoomState.CONNECTING;
            case Constants.CONNECTION_STATE_CONNECTED:
                return AudioRoomState.CONNECTED;
        }
        return null;
    }

    private IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            LogUtils.d("onJoinChannelSuccess channel:" + channel + " uid:" + uid);
        }

        @Override
        public void onAudioRouteChanged(int routing) {
            super.onAudioRouteChanged(routing);
            LogUtils.d("onAudioRouteChanged:" + routing);
        }

        @Override
        public void onError(int err) {
            Log.d(kTag, "error code:" + err);
        }

        /**
         * 用户音量提示回调
         * @param speakers 在AudioVolumeInfo中,volume的区间是[0,255]
         */
        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener == null || speakers == null || speakers.length == 0) {
                        return;
                    }
                    HashMap<String, Float> soundLevels = null;
                    Float localSoundLevel = null;
                    for (AudioVolumeInfo speaker : speakers) {
                        float volume = speaker.volume * 1.0f / 255 * 100;
                        if (speaker.uid > 0) { // 远端用户
                            if (soundLevels == null) {
                                soundLevels = new HashMap<>();
                            }
                            soundLevels.put(speaker.uid + "", volume);
                        } else { // 本地用户
                            localSoundLevel = volume;
                        }
                    }

                    // 本地采集音量
                    if (localSoundLevel != null) {
                        listener.onCapturedSoundLevelUpdate(localSoundLevel);
                    }

                    // 远程用户音量
                    if (soundLevels != null) {
                        listener.onRemoteSoundLevelUpdate(soundLevels);
                    }
                }
            });
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

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            LogUtils.d("onUserJoined uid:" + uid + " elapsed:" + elapsed);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            LogUtils.d("onUserOffline uid:" + uid + " reason:" + reason);
        }

        @Override
        public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {
            super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
            ThreadUtils.runOnUiThread(() -> {
                if (mISudAudioEventListener != null) {
                    mISudAudioEventListener.onPlayerVideoSizeChanged(uid + "", width, height);
                }
            });
        }

    };

    private final IAudioFrameObserver iAudioFrameObserver = new IAudioFrameObserver() {

        @Override
        public boolean onRecordAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
            // 这里做个优化处理，如果外部没有意图开启PCM数据回调，直接阻断不回调
            if (!isStartedPCMData) {
                return false;
            }

            // 该回调再子线程，切回主线程
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        AudioPCMData audioPCMData = new AudioPCMData();
                        audioPCMData.data = buffer;
                        audioPCMData.dataLength = buffer.remaining();
                        listener.onCapturedPCMData(audioPCMData);
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onPlaybackAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
            return false;
        }

        @Override
        public boolean onMixedAudioFrame(String channelId, int type, int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
            return false;
        }

        @Override
        public boolean onEarMonitoringAudioFrame(int type, int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
            return false;
        }

        @Override
        public boolean onPlaybackAudioFrameBeforeMixing(String channelId, int userId, int type, int samplesPerChannel, int bytesPerSample, int channels, int samplesPerSec, ByteBuffer buffer, long renderTimeMs, int avsync_type) {
            return false;
        }

        @Override
        public int getObservedAudioFramePosition() {
            return Constants.POSITION_MIXED;
        }

        @Override
        public AudioParams getRecordAudioParams() {
            //为保证采集到的音频数据格式符合预期，你可以在调用 registerAudioFrameObserver 方法时注册 getRecordAudioParams 回调，
            // 并在该回调的返回值中设置采集的音频数据格式。
            // SDK 会根据 getRecordAudioParams
            // 回调返回值中设置的 AudioParams 计算采样间隔， 并根据该采样间隔触发 onRecordFrame 回调
            return new AudioParams(16000, 1, Constants.RAW_AUDIO_FRAME_OP_MODE_READ_ONLY, 160);
        }

        @Override
        public AudioParams getPlaybackAudioParams() {
            return null;
        }

        @Override
        public AudioParams getMixedAudioParams() {
            return null;
        }

        @Override
        public AudioParams getEarMonitoringAudioParams() {
            return null;
        }
    };

    // endregion
}
