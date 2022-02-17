package tech.sud.mgp.hello.rtc.agora;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import io.agora.rtc.AudioFrame;
import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.audio.AudioParams;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMessage;
import tech.sud.mgp.hello.rtc.protocol.AudioData;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEngineProtocol;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEventHandler;
import tech.sud.mgp.hello.rtc.protocol.MediaRoomConfig;
import tech.sud.mgp.hello.rtc.protocol.MediaUser;

// 声网SDK实现
public class AgoraAudioEngine implements MediaAudioEngineProtocol {

    private MediaAudioEventHandler mMediaAudioEventHandler;
    private RtcEngine mEngine;
    private String roomID;

    // 信令相关
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;
    private MyRtmChannelListener rtmChannelListener;

    /**
     * 记录频道内远端用户数量，仅供参考
     */
    private HashSet<Integer> remoteUserList = new HashSet<>();

    private RtcEngine getEngine() {
        return mEngine;
    }

    @Override
    public void setEventHandler(MediaAudioEventHandler handler) {
        mMediaAudioEventHandler = handler;
    }

    @Override
    public void config(String appId, String appKey) {
        Context context = Utils.getApp();
        // 初始化rtm信令
        initRtm(context, appId);

        // 初始化引擎
        try {
            RtcEngine engine = RtcEngine.create(context, appId, mIRtcEngineEventHandler);
            mEngine = engine;
            if (engine != null) {
                engine.enableAudioVolumeIndication(300, 3, true); // 开启音频监听
                engine.setDefaultAudioRoutetoSpeakerphone(true); // 设置默认的音频路由
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化rtm信令
    private void initRtm(Context context, String appId) {
        try {
            if (mRtmClient == null) {
                mRtmClient = RtmClient.createInstance(context, appId, new RtmClientListenerImpl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        RtcEngine.destroy();
        remoteUserList.clear();
        rtmDestroy();
        mRtmClient = null;
    }

    @Override
    public void loginRoom(String roomId, MediaUser user, MediaRoomConfig config) {
        // rtm登录
        rtmLoginRoom(roomId, user.userID);

        RtcEngine engine = getEngine();
        if (engine != null) {
            int userId = 0;
            try {
                userId = Integer.parseInt(user.userID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 默认关闭麦克风，关掉推流
            engine.enableLocalAudio(false);
            ChannelMediaOptions channelMediaOptions = new ChannelMediaOptions();
            channelMediaOptions.publishLocalAudio = false;
            channelMediaOptions.publishLocalVideo = false;

            engine.registerAudioFrameObserver(iAudioFrameObserver);

            // 加入频道
            engine.joinChannel("", roomId, "", userId, channelMediaOptions);
            this.roomID = roomId;
        }
    }

    // rtm登录房间
    private void rtmLoginRoom(String roomId, String userId) {
        RtmClient rtmClient = mRtmClient;
        if (rtmClient == null) return;
        // 登录平台
        rtmClient.login("", userId, null);

        if (rtmChannelListener == null) {
            rtmChannelListener = new MyRtmChannelListener();
        }
        try {
            // 创建频道
            RtmChannel channel = rtmClient.createChannel(roomId, rtmChannelListener);
            channel.join(null);
            mRtmChannel = channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // rtm退出房间
    private void rtmLogoutRoom() {
        RtmChannel rtmChannel = mRtmChannel;
        if (rtmChannel != null) {
            rtmChannel.leave(null);
            mRtmChannel = null;
        }
        rtmChannelListener = null;
    }

    // rtm登出平台
    private void rtmDestroy() {
        RtmClient rtmClient = mRtmClient;
        if (rtmClient != null) {
            rtmClient.logout(null);
            rtmClient.release();
            mRtmClient = null;
        }
        mRtmChannel = null;
        rtmChannelListener = null;
    }

    @Override
    public void logoutRoom() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.registerAudioFrameObserver(null);
            engine.leaveChannel();
        }
        remoteUserList.clear();
        rtmLogoutRoom();
    }

    @Override
    public void startPublish(String streamId) {
        // streamId，声网不需要
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(true); // 开启麦克风采集
            engine.muteLocalAudioStream(false); // 发布本地音频流
        }
    }

    @Override
    public void stopPublishStream() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(false); // 关闭麦克风采集
            engine.muteLocalAudioStream(true); // 取消发布本地音频流
        }
    }

    @Override
    public void startPlayingStream(String streamId) {

    }

    @Override
    public void stopPlayingStream(String streamId) {

    }

    @Override
    public void sendCommand(String roomId, String command, SendCommandResult result) {
        // 创建消息实例
        RtmMessage message = mRtmClient.createMessage();
        message.setText(command);

        // 发送频道消息
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                LogUtils.d("sendMessage onSuccess:");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                LogUtils.d("sendMessage onFailure:" + errorInfo);
            }
        });
    }

    @Override
    public void setAudioDataHandler() {
        //TODO
    }

    @Override
    public void startAudioDataListener() {

    }

    @Override
    public void stopAudioDataListener() {

    }

    private IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {

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
                    MediaAudioEventHandler handler = mMediaAudioEventHandler;
                    if (handler == null || speakers == null || speakers.length == 0) {
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
                        handler.onCapturedSoundLevelUpdate(localSoundLevel);
                    }

                    // 远程用户音量
                    if (soundLevels != null) {
                        handler.onRemoteSoundLevelUpdate(soundLevels);
                    }
                }
            });
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MediaAudioEventHandler handler = mMediaAudioEventHandler;
                    if (handler != null) {
                        handler.onRoomStateUpdate(roomID, AgoraRoomStateConverter.converAudioRoomState(state), 0, null);
                        updateRoomUserCount();
                    }
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    remoteUserList.add(uid);
                    updateRoomUserCount();
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    remoteUserList.remove(uid);
                    updateRoomUserCount();
                }
            });
        }

        // 更新房间内用户总人数
        private void updateRoomUserCount() {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MediaAudioEventHandler handler = mMediaAudioEventHandler;
                    if (handler != null) {
                        handler.onRoomOnlineUserCountUpdate(roomID, remoteUserList.size() + 1);
                    }
                }
            });
        }
    };

    private final IAudioFrameObserver iAudioFrameObserver = new IAudioFrameObserver() {
        @Override
        public boolean onRecordFrame(AudioFrame audioFrame) {
            //该回调再子线程，切回主线程
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MediaAudioEventHandler handler = mMediaAudioEventHandler;
                    if (handler != null) {
                        AudioData audioData = new AudioData();
                        audioData.data = audioFrame.samples;
                        audioData.dataLength = audioFrame.samples.remaining();
                        handler.onCapturedAudioData(audioData);
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
            AudioParams params = new AudioParams(16000, 1, 0, 160);
            return params;
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

    // 云信令回调
    private class MyRtmChannelListener implements RtmChannelListener {

        @Override
        public void onMemberCountUpdated(int i) {

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
            if (rtmMessage == null || rtmChannelMember == null) return;
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("onMessageReceived:" + rtmMessage.getText());
                    MediaAudioEventHandler handler = mMediaAudioEventHandler;
                    if (handler != null) {
                        try {
                            handler.onIMRecvCustomCommand(rtmChannelMember.getChannelId(), new MediaUser(rtmChannelMember.getUserId()), rtmMessage.getText());
                        } catch (Exception e) {
                            LogUtils.e("onMessageReceived", e);
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
    }


}
