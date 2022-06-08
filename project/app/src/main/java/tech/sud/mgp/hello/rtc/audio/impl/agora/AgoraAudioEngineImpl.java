package tech.sud.mgp.hello.rtc.audio.impl.agora;

import static io.agora.rtc.Constants.CHANNEL_PROFILE_COMMUNICATION;
import static io.agora.rtc.RtcEngineConfig.AreaCode.AREA_CODE_GLOB;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtc.AudioFrame;
import io.agora.rtc.Constants;
import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcEngineConfig;
import io.agora.rtc.audio.AudioParams;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.core.SudRTIChannelProfile;
import tech.sud.mgp.hello.rtc.audio.core.SudRTIClientRole;
import tech.sud.mgp.hello.rtc.audio.impl.AsyncCallWrapper;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

// 声网SDK实现
public class AgoraAudioEngineImpl implements ISudAudioEngine {

    private static final String kTag = "ISudAudioEngine";

    private ISudAudioEventListener mISudAudioEventListener;
    private RtcEngine mEngine;

    // 信令相关
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;

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
                    config.mAreaCode = AREA_CODE_GLOB;
                    mEngine = RtcEngine.create(config);

                    if (mEngine != null) {
                        mEngine.setChannelProfile(CHANNEL_PROFILE_COMMUNICATION);
                        mEngine.enableAudioVolumeIndication(300, 3, true);

                        // 初始化rtm信令
                        initRtm(context, model, success);
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
                destroyRtm();
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
                    joinRtmRoom(model.roomID);

                    // 默认关闭麦克风，关掉推流
                    engine.enableLocalAudio(false);
                    ChannelMediaOptions channelMediaOptions = new ChannelMediaOptions();
                    channelMediaOptions.autoSubscribeAudio = true;
                    channelMediaOptions.autoSubscribeVideo = false;
                    channelMediaOptions.publishLocalAudio = false;
                    channelMediaOptions.publishLocalVideo = false;
                    // 加入频道
                    engine.joinChannelWithUserAccount(model.token, model.roomID, model.userID, channelMediaOptions);
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
                leaveRtmRoom();
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
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    /* 开启获取PCM数据功能 */
                    engine.registerAudioFrameObserver(iAudioFrameObserver);
                }
            }
        });
    }

    @Override
    public void stopPCMCapture() {
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                RtcEngine engine = getEngine();
                if (engine != null) {
                    /* 关闭获取PCM数据功能 */
                    engine.registerAudioFrameObserver(null);
                }
            }
        });
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
        AsyncCallWrapper.sharedInstance().executeInSerial(new Runnable() {
            @Override
            public void run() {
                if (mRtmChannel != null) {
                    // 创建消息实例
                    RtmMessage message = mRtmClient.createMessage();
                    message.setText(command);

                    // 发送频道消息
                    mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(kTag, "sendMessage onSuccess:");
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {
                            Log.d(kTag, "sendMessage onFailure:" + errorInfo);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setChannelProfile(SudRTIChannelProfile profile) {

    }

    @Override
    public void setClientRole(SudRTIClientRole clientRole) {

    }

    @Override
    public void startLiveStreaming(View view) {

    }

    @Override
    public void stopLiveStreaming() {

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
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
        }
    };

    private final IAudioFrameObserver iAudioFrameObserver = new IAudioFrameObserver() {
        @Override
        public boolean onRecordFrame(AudioFrame audioFrame) {
            //该回调再子线程，切回主线程
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener handler = mISudAudioEventListener;
                    if (handler != null) {
                        AudioPCMData audioPCMData = new AudioPCMData();
                        audioPCMData.data = audioFrame.samples;
                        audioPCMData.dataLength = audioFrame.samples.remaining();
                        handler.onCapturedPCMData(audioPCMData);
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onPlaybackFrame(AudioFrame audioFrame) {
            return false;
        }

        @Override
        public boolean onPlaybackFrameBeforeMixing(AudioFrame audioFrame, int uid) {
            return false;
        }

        @Override
        public boolean onMixedFrame(AudioFrame audioFrame) {
            return false;
        }

        @Override
        public boolean isMultipleChannelFrameWanted() {
            return false;
        }

        @Override
        public boolean onPlaybackFrameBeforeMixingEx(AudioFrame audioFrame, int uid, String channelId) {
            return false;
        }

        @Override
        public int getObservedAudioFramePosition() {
            return IAudioFrameObserver.POSITION_RECORD;
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
    };

    // region RTM 信令操作
    // 初始化rtm信令, 登录 Agora RTM 系统
    private void initRtm(Context context, AudioConfigModel model, Runnable success) {
        try {
            if (mRtmClient == null) {
                mRtmClient = RtmClient.createInstance(context.getApplicationContext(), model.appId, rtmClientListener);
            }

            if (mRtmClient != null) {
                // 登录Agora RTM 系统
                mRtmClient.login(model.token, model.userID, new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (success != null) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    success.run();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 登出 Agora RTM 系统, 释放当前 RtmClient 实例使用的所有资源
    private void destroyRtm() {
        if (mRtmClient != null) {
            mRtmClient.logout(null);
            mRtmClient.release();
            mRtmClient = null;
        }
        mRtmChannel = null;
    }

    // rtm登录房间
    private void joinRtmRoom(String roomId) {
        if (mRtmClient != null) {
            try {
                // 创建频道
                mRtmChannel = mRtmClient.createChannel(roomId, rtmChannelListener);
                if (mRtmChannel != null) {
                    mRtmChannel.join(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // rtm退出房间
    private void leaveRtmRoom() {
        if (mRtmChannel != null) {
            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }

    // 云信令回调
    private final RtmChannelListener rtmChannelListener = new RtmChannelListener() {
        @Override
        public void onMemberCountUpdated(int memberCount) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener handler = mISudAudioEventListener;
                    if (handler != null) {
                        handler.onRoomOnlineUserCountUpdate(memberCount);
                    }
                }
            });
        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
            if (rtmMessage == null || rtmChannelMember == null) {
                return;
            }

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(kTag, "onMessageReceived:" + rtmMessage.getText());
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        try {
                            listener.onRecvCommand(rtmChannelMember.getUserId(), rtmMessage.getText());
                        } catch (Exception e) {
                            Log.e(kTag, "onMessageReceived: " + e.getMessage());
                        }
                    }
                }
            });
        }

        @Override
        public void onImageMessageReceived(RtmImageMessage rtmImageMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberJoined(RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberLeft(RtmChannelMember rtmChannelMember) {

        }
    };

    /**
     * 声网RTM客户端监听
     */
    private final RtmClientListener rtmClientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int i, int i1) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {

        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    };
    // endregion
}
