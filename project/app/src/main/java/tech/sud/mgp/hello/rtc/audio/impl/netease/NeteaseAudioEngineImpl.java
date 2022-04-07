package tech.sud.mgp.hello.rtc.audio.impl.netease;

import static com.netease.lava.nertc.sdk.NERtcConstants.RTCChannelProfile.COMMUNICATION;

import android.content.Context;
import android.graphics.Rect;

import com.blankj.utilcode.util.ThreadUtils;
import com.netease.lava.nertc.sdk.NERtcCallbackEx;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.audio.NERtcAudioFrame;
import com.netease.lava.nertc.sdk.audio.NERtcAudioFrameObserver;
import com.netease.lava.nertc.sdk.audio.NERtcAudioFrameOpMode;
import com.netease.lava.nertc.sdk.audio.NERtcAudioFrameRequestFormat;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRoom;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

/**
 * 网易云信引擎实现类
 */
public class NeteaseAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private String mRoomID;

    /**
     * 记录房间内用户数量
     */
    private HashSet<String> roomUserList = new HashSet<>();

    private NERtcEx getEngine() {
        return NERtcEx.getInstance();
    }

    @Override
    public void setEventListener(ISudAudioEventListener listener) {
        mISudAudioEventListener = listener;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model) {
        initWithConfig(context, model, null);
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {
        if (model == null)
            return;

        NIMClient.initSDK();

        // 连接 IM 服务
        NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
            @Override
            public void onEvent(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    LoginInfo info = new LoginInfo(model.userID, model.token);
                    RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
                        @Override
                        public void onSuccess(LoginInfo loginInfo) {
                            if (success != null) {
                                success.run();
                            }
                        }

                        @Override
                        public void onFailed(int i) {
                        }

                        @Override
                        public void onException(Throwable throwable) {
                        }
                    };
                    NIMClient.getService(AuthService.class).login(info).setCallback(callback);
                }
            }
        }, true);

        NERtcEx engine = getEngine();
        if (engine != null) {
            try {
                engine.init(context.getApplicationContext(), model.appId, neRtcCallbackEx, null);

                engine.setAudioProfile(NERtcConstants.AudioProfile.HIGH_QUALITY, NERtcConstants.AudioScenario.CHATROOM);
                engine.setChannelProfile(COMMUNICATION);
                engine.setSpeakerphoneOn(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.release();
        }

        NIMClient.getService(AuthService.class).logout();
        mRoomID = null;
        roomUserList.clear();
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        long userID = 0;
        try {
            userID = Long.parseLong(model.userID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NERtcEx engine = getEngine();
        if (engine != null) {
            // 默认关闭麦克风，关掉推流
            stopPublishStream();
            engine.joinChannel(model.token, model.roomID, userID);

            engine.enableAudioVolumeIndication(true, 300);
        }
        mRoomID = model.roomID;

        // IM 进入聊天室
        EnterChatRoomData data = new EnterChatRoomData(model.roomID);
        NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 3).setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, true);
            }

            @Override
            public void onFailed(int i) {
            }

            @Override
            public void onException(Throwable throwable) {
            }
        });
    }

    @Override
    public void leaveRoom() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.leaveChannel();
            engine.enableLocalAudio(false);

            engine.enableAudioVolumeIndication(false, 300);
        }

        // IM 离开聊天室
        NIMClient.getService(ChatRoomService.class).exitChatRoom(mRoomID);
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, false);
        mRoomID = null;
        roomUserList.clear();
    }

    @Override
    public void startPublishStream() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(true); // 开启麦克风采集
            engine.muteLocalAudioStream(false); // 发布本地音频流
        }
    }

    @Override
    public void stopPublishStream() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(false); // 关闭麦克风采集
            engine.muteLocalAudioStream(true); // 取消发布本地音频流
        }
    }

    @Override
    public void startSubscribingStream() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.subscribeAllRemoteAudioStreams(true);
        }
    }

    @Override
    public void stopSubscribingStream() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.subscribeAllRemoteAudioStreams(false);
        }
    }

    @Override
    public void startPCMCapture() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            // 设置音频回调参数
            NERtcAudioFrameRequestFormat formatPCM = new NERtcAudioFrameRequestFormat();
            formatPCM.setChannels(1);
            formatPCM.setSampleRate(16000);
            formatPCM.setOpMode(NERtcAudioFrameOpMode.kNERtcAudioFrameOpModeReadOnly);
            engine.setRecordingAudioFrameParameters(formatPCM);
            engine.setAudioFrameObserver(neRtcAudioFrameObserver);
        }
    }

    @Override
    public void stopPCMCapture() {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.setAudioFrameObserver(null);
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        NERtcEx engine = getEngine();
        if (engine != null) {
            engine.setSpeakerphoneOn(enabled);
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        if (mRoomID != null && command != null && !command.isEmpty()) {
            ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(mRoomID, command);
            NIMClient.getService(ChatRoomService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {

                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        }
    }

    // 更新房间内用户总人数
    private void updateRoomUserCount() {
        RCRTCRoom rcrtcRoom = RCRTCEngine.getInstance().getRoom();
        if (rcrtcRoom == null)
            return;

        ISudAudioEventListener handler = mISudAudioEventListener;
        if (handler != null) {
            handler.onRoomOnlineUserCountUpdate(rcrtcRoom.getRoomId(), roomUserList.size() + 1);
        }
    }

    private final NERtcCallbackEx neRtcCallbackEx = new NERtcCallbackEx() {
        @Override
        public void onJoinChannel(int result, long channelId, long elapsed, long uid) {
            if (result == 0) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISudAudioEventListener handler = mISudAudioEventListener;
                        if (handler != null) {
                            handler.onRoomStateUpdate(mRoomID, AudioRoomState.CONNECTED, 0, null);
                        }
                    }
                });
            }
        }

        @Override
        public void onLeaveChannel(int i) {}

        @Override
        public void onUserJoined(long uid) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roomUserList.add(uid + "");
                    updateRoomUserCount();
                }
            });
        }

        @Override
        public void onUserLeave(long uid, int reason) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roomUserList.remove(uid + "");
                    updateRoomUserCount();
                }
            });
        }

        @Override
        public void onUserAudioStart(long l) { }

        @Override
        public void onUserAudioStop(long l) { }

        @Override
        public void onUserVideoStart(long l, int i) { }

        @Override
        public void onUserVideoStop(long l) { }

        @Override
        public void onDisconnect(int i) { }

        @Override
        public void onClientRoleChange(int i, int i1) {}

        @Override
        public void onUserSubStreamVideoStart(long var1, int var3) {}

        @Override
        public void onUserSubStreamVideoStop(long var1) {}

        @Override
        public void onUserAudioMute(long var1, boolean var3) {}

        @Override
        public void onUserVideoMute(long var1, boolean var3) {}

        @Override
        public void onFirstAudioDataReceived(long var1) {}

        @Override
        public void onFirstVideoDataReceived(long var1) {}

        @Override
        public void onFirstAudioFrameDecoded(long var1) {}

        @Override
        public void onFirstVideoFrameDecoded(long var1, int var3, int var4) {}

        @Override
        public void onUserVideoProfileUpdate(long var1, int var3) {}

        @Override
        public void onAudioDeviceChanged(int var1) {}

        @Override
        public void onAudioDeviceStateChange(int var1, int var2) {}

        @Override
        public void onVideoDeviceStageChange(int var1) {}

        @Override
        public void onConnectionTypeChanged(int var1) {}

        @Override
        public void onReconnectingStart() {}

        @Override
        public void onReJoinChannel(int var1, long var2) {}

        @Override
        public void onAudioMixingStateChanged(int var1) {}

        @Override
        public void onAudioMixingTimestampUpdate(long var1) {}

        @Override
        public void onAudioEffectFinished(int var1) {}

        @Override
        public void onLocalAudioVolumeIndication(int volume) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener handler = mISudAudioEventListener;
                    if (handler != null) {
                        handler.onCapturedSoundLevelUpdate(volume);
                    }
                }
            });
        }

        @Override
        public void onRemoteAudioVolumeIndication(NERtcAudioVolumeInfo[] volumeArray, int totalVolume) {
            if (volumeArray == null)
                return;

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        HashMap<String, Float> userSoundLevels = new HashMap<>();
                        for (NERtcAudioVolumeInfo volumeInfo : volumeArray) {
                            userSoundLevels.put(volumeInfo.uid + "", (float) volumeInfo.volume);
                            listener.onRemoteSoundLevelUpdate(userSoundLevels);
                        }
                    }
                }
            });
        }

        @Override
        public void onLiveStreamState(String var1, String var2, int var3) {}

        @Override
        public void onConnectionStateChanged(int var1, int var2) { }

        @Override
        public void onCameraFocusChanged(Rect var1) {}

        @Override
        public void onCameraExposureChanged(Rect var1) {}

        @Override
        public void onRecvSEIMsg(long var1, String var3) {}

        @Override
        public void onAudioRecording(int var1, String var2) {}

        @Override
        public void onError(int var1) {}

        @Override
        public void onWarning(int var1) {}

        @Override
        public void onMediaRelayStatesChange(int var1, String var2) {}

        @Override
        public void onMediaRelayReceiveEvent(int var1, int var2, String var3) {}

        @Override
        public void onUserSubStreamAudioStart(long var1) {}

        @Override
        public void onUserSubStreamAudioStop(long var1) {}

        @Override
        public void onUserSubStreamAudioMute(long var1, boolean var3) {}
    };

    private final NERtcAudioFrameObserver neRtcAudioFrameObserver = new NERtcAudioFrameObserver() {
        @Override
        public void onRecordFrame(NERtcAudioFrame neRtcAudioFrame) {
            ISudAudioEventListener handler = mISudAudioEventListener;
            if (handler != null) {
                AudioPCMData audioPCMData = new AudioPCMData();
                audioPCMData.data = neRtcAudioFrame.getData();
                audioPCMData.dataLength = neRtcAudioFrame.getFormat().getBytesPerSample() * neRtcAudioFrame.getFormat().getChannels() * neRtcAudioFrame.getFormat().getSamplesPerChannel();
                handler.onCapturedPCMData(audioPCMData);
            }
        }

        @Override
        public void onRecordSubStreamAudioFrame(NERtcAudioFrame var1) {

        }

        @Override
        public void onPlaybackFrame(NERtcAudioFrame neRtcAudioFrame) {

        }

        @Override
        public void onPlaybackAudioFrameBeforeMixingWithUserID(long l, NERtcAudioFrame neRtcAudioFrame) {

        }

        @Override
        public void onPlaybackSubStreamAudioFrameBeforeMixingWithUserID(long var1, NERtcAudioFrame var3) {

        }

        @Override
        public void onMixedAudioFrame(NERtcAudioFrame neRtcAudioFrame) {

        }
    };

    private Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> chatRoomMessages) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        for (ChatRoomMessage message : chatRoomMessages) {
                            if (message != null) {
                                if (message.getMsgType() == MsgTypeEnum.text) {
                                    listener.onRecvCommand(message.getFromAccount(), message.getContent());
                                }
                            }
                        }
                    }
                }
            });

        }
    };
}
