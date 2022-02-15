package tech.sud.mgp.hello.rtc.agora;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.util.HashMap;
import java.util.HashSet;

import io.agora.rtc.AudioFrame;
import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.audio.AudioParams;
import io.agora.rtc.models.ChannelMediaOptions;
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
        try {
            RtcEngine engine = RtcEngine.create(Utils.getApp(), appId, mIRtcEngineEventHandler);
            mEngine = engine;
            if (engine != null) {
                engine.enableAudioVolumeIndication(300, 3, true); // 开启音频监听
                engine.setDefaultAudioRoutetoSpeakerphone(true); // 设置默认的音频路由
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        RtcEngine.destroy();
        remoteUserList.clear();
    }

    @Override
    public void loginRoom(String roomId, MediaUser user, MediaRoomConfig config) {
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

            // 加入频道
            engine.joinChannel("", roomId, "", userId, channelMediaOptions);
            this.roomID = roomId;
        }
    }

    @Override
    public void logoutRoom() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.leaveChannel();
        }
        remoteUserList.clear();
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

    }

    @Override
    public void setAudioDataHandler() {
        //TODO
    }

    @Override
    public void startAudioDataListener() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.registerAudioFrameObserver(iAudioFrameObserver);
        }
    }

    @Override
    public void stopAudioDataListener() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.registerAudioFrameObserver(null);
        }
    }

    private IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {

        /**
         * 用户音量提示回调
         * @param speakers 在AudioVolumeInfo中,volume的区间是[0,255]
         */
        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
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

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRoomStateUpdate(roomID, AgoraRoomStateConverter.converAudioRoomState(state), 0, null);
                updateRoomUserCount();
            }
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            LogUtils.d("onUserJoined:" + uid);
            remoteUserList.add(uid);
            updateRoomUserCount();
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            LogUtils.d("onUserOffline:" + uid);
            remoteUserList.remove(uid);
            updateRoomUserCount();
        }

        // 更新房间内用户总人数
        private void updateRoomUserCount() {
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRoomOnlineUserCountUpdate(roomID, remoteUserList.size() + 1);
            }
        }

    };

    private final IAudioFrameObserver iAudioFrameObserver = new IAudioFrameObserver() {
        @Override
        public boolean onRecordFrame(AudioFrame audioFrame) {
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                AudioData audioData = new AudioData();
                audioData.data = audioFrame.samples;
                audioData.dataLength = audioFrame.bytesPerSample * audioFrame.channels * audioFrame.samplesPerSec;
                handler.onCapturedAudioData(audioData);
            }
            return false;
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
            return 0;
        }

        @Override
        public AudioParams getRecordAudioParams() {
            //为保证采集到的音频数据格式符合预期，你可以在调用 registerAudioFrameObserver 方法时注册 getRecordAudioParams 回调，
            // 并在该回调的返回值中设置采集的音频数据格式。
            // SDK 会根据 getRecordAudioParams
            // 回调返回值中设置的 AudioParams 计算采样间隔， 并根据该采样间隔触发 onRecordFrame 回调
            return null;
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

}
