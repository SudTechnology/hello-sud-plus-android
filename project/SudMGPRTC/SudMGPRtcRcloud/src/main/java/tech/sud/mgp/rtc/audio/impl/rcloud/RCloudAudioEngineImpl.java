package tech.sud.mgp.rtc.audio.impl.rcloud;

import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.ThreadUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.callback.IRCRTCAudioDataListener;
import cn.rongcloud.rtc.api.stream.RCRTCMicOutputStream;
import cn.rongcloud.rtc.base.RCRTCAudioFrame;
import cn.rongcloud.voiceroom.api.IRCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;

public class RCloudAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private String mUserID = ""; // 自己的userId

    /**
     * 记录房间内用户数量
     */
    private HashSet<String> roomUserList = new HashSet<>();

    private IRCVoiceRoomEngine getEngine() {
        return RCVoiceRoomEngine.getInstance();
    }

    @Override
    public void setEventListener(ISudAudioEventListener eventHandler) {
        mISudAudioEventListener = eventHandler;
    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {
        if (model == null)
            return;

        // 设置自己的userId
        mUserID = model.userID;
        // 进行 AppKey 设置
        RongCoreClient.init(context.getApplicationContext(), model.appKey);
        // 连接 IM 服务
        RongCoreClient.connect(model.token, new IRongCoreCallback.ConnectCallback() {
            @Override
            public void onSuccess(String t) {
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
            public void onError(IRongCoreEnum.ConnectionErrorCode errorCode) {
                if (errorCode == IRongCoreEnum.ConnectionErrorCode.RC_CONNECTION_EXIST) {
                    if (success != null) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                success.run();
                            }
                        });
                    }
                }
            }

            @Override
            public void onDatabaseOpened(IRongCoreEnum.DatabaseOpenStatus code) {
            }
        });

        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.addMessageReceiveListener(onReceiveMessageListener);
        }
    }

    @Override
    public void destroy() {
        RongCoreClient.getInstance().disconnect();
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.removeMessageReceiveListener(onReceiveMessageListener);
        }
        roomUserList.clear();
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.setVoiceRoomEventListener(mRCVoiceRoomEventListener);

            RCRTCConfig rcrtcConfig = RCRTCConfig.Builder.create().setAudioSampleRate(16000).enableStereo(false).build();
            engine.joinRoom(rcrtcConfig, model.roomID, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ISudAudioEventListener listener = mISudAudioEventListener;
                            if (listener != null) {
                                listener.onRoomStateUpdate(AudioRoomState.CONNECTED, 0, null);
                                updateRoomUserCount();
                            }

                            engine.enableSpeaker(true);
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    @Override
    public void leaveRoom() {
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.leaveRoom(new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
        roomUserList.clear();
    }

    @Override
    public void startPublishStream() {
        if (RCRTCEngine.getInstance().getRoom() == null)
            return;

        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.getLatestSeatInfo(new RCVoiceRoomResultCallback<List<RCVoiceSeatInfo>>() {
                @Override
                public void onSuccess(List<RCVoiceSeatInfo> rcVoiceSeatInfos) {
                    for (int seatIndex = 0; seatIndex < rcVoiceSeatInfos.size(); seatIndex++) {
                        if (rcVoiceSeatInfos.get(seatIndex).getStatus() == RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty) {
                            engine.enterSeat(seatIndex, null);
                            engine.disableAudioRecording(false);
                            return;
                        }
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    @Override
    public void stopPublishStream() {
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.leaveSeat(new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    engine.disableAudioRecording(true);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    @Override
    public void startSubscribingStream() {
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllRemoteStreams(false);
        }
    }

    @Override
    public void stopSubscribingStream() {
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllRemoteStreams(true);
        }
    }

    @Override
    public void startPCMCapture() {
        RCRTCMicOutputStream deafultAudioStream = RCRTCEngine.getInstance().getDefaultAudioStream();
        if (deafultAudioStream != null) {
            deafultAudioStream.setRecordAudioDataListener(new IRCRTCAudioDataListener() {
                @Override
                public byte[] onAudioFrame(RCRTCAudioFrame rcRTCAudioFrame) {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        AudioPCMData audioPCMData = new AudioPCMData();
                        audioPCMData.data = ByteBuffer.wrap(rcRTCAudioFrame.getBytes());
                        audioPCMData.dataLength = rcRTCAudioFrame.getBytes().length;
                        listener.onCapturedPCMData(audioPCMData);
                    }

                    return rcRTCAudioFrame.getBytes();
                }
            });
        }
    }

    @Override
    public void stopPCMCapture() {
        RCRTCMicOutputStream deafultAudioStream = RCRTCEngine.getInstance().getDefaultAudioStream();
        if (deafultAudioStream != null) {
            deafultAudioStream.setRecordAudioDataListener(null);
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            engine.enableSpeaker(enabled);
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        RCRTCRoom rcrtcRoom = RCRTCEngine.getInstance().getRoom();
        if (rcrtcRoom == null) {
            if (null != listener) {
                listener.onResult(-1);
            }
            return;
        }

        IRCVoiceRoomEngine engine = getEngine();
        if (engine != null) {
            TextMessage messageContent = TextMessage.obtain(command);
            engine.sendRoomMessage(messageContent, new RCVoiceRoomCallback() {
                @Override
                public void onSuccess() {
                    if (null != listener) {
                        listener.onResult(0);
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if (null != listener) {
                        listener.onResult(i);
                    }
                }
            });
        } else {
            if (null != listener) {
                listener.onResult(-1);
            }
        }
    }

    @Override
    public void startPlayingStream(String streamID, View view) {

    }

    @Override
    public void stopPlayingStream(String streamID) {

    }

    // 更新房间内用户总人数
    private void updateRoomUserCount() {
        RCRTCRoom rcrtcRoom = RCRTCEngine.getInstance().getRoom();
        if (rcrtcRoom == null)
            return;

        ISudAudioEventListener listener = mISudAudioEventListener;
        if (listener != null) {
            listener.onRoomOnlineUserCountUpdate(roomUserList.size() + 1);
        }
    }

    private final IRongCoreListener.OnReceiveMessageListener onReceiveMessageListener = new IRongCoreListener.OnReceiveMessageListener() {
        @Override
        public boolean onReceived(Message message, int left) {
            if (message != null) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISudAudioEventListener listener = mISudAudioEventListener;
                        if (listener != null) {
                            MessageContent messageContent = message.getContent();
                            if (messageContent instanceof TextMessage) {
                                TextMessage textMessage = (TextMessage) messageContent;
                                listener.onRecvCommand(message.getSenderUserId(), textMessage.getContent());
                            }
                        }
                    }
                });
            }

            return false;
        }
    };

    private final RCVoiceRoomEventListener mRCVoiceRoomEventListener = new RCVoiceRoomEventListener() {

        // [seatIndex : userId]
        private HashMap<Integer, String> seatUserMap = new HashMap<>();

        /**
         * 房间准备就绪
         */
        @Override
        public void onRoomKVReady() {
        }

        /**
         * 房间销毁回调
         */
        @Override
        public void onRoomDestroy() {

        }

        /**
         * 房间信息变更回调
         *
         * @param roomInfo 房间信息 {@link RCVoiceRoomInfo}
         */
        @Override
        public void onRoomInfoUpdate(RCVoiceRoomInfo roomInfo) {

        }

        /**
         * 房间座位变更回调，包括自身上麦或下麦也会触发此回调
         *
         * @param seatInfoList 座位列表信息 {@link RCVoiceRoomInfo}
         */
        @Override
        public void onSeatInfoUpdate(List<RCVoiceSeatInfo> seatInfoList) {

        }

        /**
         * 某个主播上麦回调，包含自己上麦也会触发此回调
         *
         * @param seatIndex 麦位号
         * @param userId    用户Id
         */
        @Override
        public void onUserEnterSeat(int seatIndex, String userId) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seatUserMap.put(seatIndex, userId);
                }
            });
        }

        /**
         * 某个主播下麦回调，包含自己下麦也会触发此回调
         *
         * @param seatIndex 麦位号
         * @param userId    用户Id
         */
        @Override
        public void onUserLeaveSeat(int seatIndex, String userId) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seatUserMap.remove(seatIndex);
                }
            });
        }

        /**
         * 座位静音状态回调
         *
         * @param index  座位号
         * @param isMute 静音状态
         */
        @Override
        public void onSeatMute(int index, boolean isMute) {

        }

        /**
         * 座位关闭回调
         *
         * @param index  座位号
         * @param isLock 是否关闭
         */
        @Override
        public void onSeatLock(int index, boolean isLock) {

        }

        /**
         * 观众进房回调
         *
         * @param userId 观众 Id
         */
        @Override
        public void onAudienceEnter(String userId) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roomUserList.add(userId);
                    updateRoomUserCount();
                }
            });
        }

        /**
         * 观众退房回调
         *
         * @param userId 观众 Id
         */
        @Override
        public void onAudienceExit(String userId) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roomUserList.remove(userId);
                    updateRoomUserCount();
                }
            });
        }

        /**
         * 用户音量变动回调
         *
         * @param seatIndex  麦位序号
         * @param audioLevel 是否正在说话
         */
        @Override
        public void onSpeakingStateChanged(int seatIndex, int audioLevel) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String userId = seatUserMap.get(seatIndex);
                    float soundLevel = audioLevel * 10.0f;
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (mUserID.equals(userId)) {
                        if (listener != null) {
                            listener.onCapturedSoundLevelUpdate(soundLevel);
                        }
                    } else {
                        if (listener != null) {
                            HashMap<String, Float> userSoundLevels = new HashMap<>();
                            userSoundLevels.put(userId, soundLevel);
                            listener.onRemoteSoundLevelUpdate(userSoundLevels);
                        }
                    }
                }
            });
        }

        /**
         * 收取消息回调
         *
         * @param message 收到的消息
         */
        @Override
        public void onMessageReceived(Message message) {

        }

        /**
         * 房间通知回调
         *
         * @param name    名称
         * @param content 内容
         */
        @Override
        public void onRoomNotificationReceived(String name, String content) {

        }

        /**
         * 自己被抱上麦通知
         */
        @Override
        public void onPickSeatReceivedFrom(String userId) {

        }

        /**
         * 自己被下麦通知
         */
        @Override
        public void onKickSeatReceived(int index) {

        }

        /**
         * 发送的排麦请求得到房主或管理员同意
         */
        @Override
        public void onRequestSeatAccepted() {

        }

        /**
         * 发送的排麦请求被房主或管理员拒绝
         */
        @Override
        public void onRequestSeatRejected() {

        }

        /**
         * 排麦列表发生变化
         */
        @Override
        public void onRequestSeatListChanged() {

        }

        /**
         * 收到邀请回调
         *
         * @param invitationId 邀请的 Id
         * @param userId       发送邀请的用户
         * @param content      邀请内容，用户可以自定义
         */
        @Override
        public void onInvitationReceived(String invitationId, String userId, String content) {

        }

        /**
         * 邀请被接受回调
         *
         * @param invitationId 邀请 Id
         */
        @Override
        public void onInvitationAccepted(String invitationId) {

        }

        /**
         * 邀请被拒绝回调
         *
         * @param invitationId 邀请 Id
         */
        @Override
        public void onInvitationRejected(String invitationId) {

        }

        /**
         * 邀请被取消回调
         *
         * @param invitationId 邀请 Id
         */
        @Override
        public void onInvitationCancelled(String invitationId) {

        }

        /**
         * 被踢出房间回调
         *
         * @param targetId 被踢人 Id
         * @param userId   发起人 Id
         */
        @Override
        public void onUserReceiveKickOutRoom(String targetId, String userId) {

        }

        @Override
        public void onNetworkStatus(int i) {

        }

        @Override
        public void onPKGoing(RCPKInfo rcpkInfo) {

        }

        @Override
        public void onPKFinish() {

        }

        @Override
        public void onReceivePKInvitation(String s, String s1) {

        }

        @Override
        public void onPKInvitationCanceled(String s, String s1) {

        }

        @Override
        public void onPKInvitationRejected(String s, String s1) {

        }

        @Override
        public void onPKInvitationIgnored(String s, String s1) {

        }
    };
}
