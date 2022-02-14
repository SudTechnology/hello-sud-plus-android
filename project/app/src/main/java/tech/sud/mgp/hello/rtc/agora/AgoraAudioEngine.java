package tech.sud.mgp.hello.rtc.agora;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEngineProtocol;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEventHandler;
import tech.sud.mgp.hello.rtc.protocol.MediaRoomConfig;
import tech.sud.mgp.hello.rtc.protocol.MediaUser;

// 声网SDK实现
public class AgoraAudioEngine implements MediaAudioEngineProtocol {

    private MediaAudioEventHandler mMediaAudioEventHandler;
    private RtcEngine mEngine;
    private String roomID;

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
            mEngine = RtcEngine.create(Utils.getApp(), appId, mIRtcEngineEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        RtcEngine.destroy();
    }

    @Override
    public void loginRoom(String roomId, MediaUser user, MediaRoomConfig config) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            ChannelMediaOptions channelMediaOptions = new ChannelMediaOptions();
            int userId = 0;
            try {
                userId = Integer.parseInt(user.userID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            engine.enableLocalAudio(false);
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
    }

    @Override
    public void startPublish(String streamId) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(true);
        }
    }

    @Override
    public void stopPublishStream() {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.enableLocalAudio(false);
        }
    }

    @Override
    public boolean isPublishing() {
        return false;
    }

    @Override
    public void startPlayingStream(String streamId) {

    }

    @Override
    public void stopPlayingStream(String streamId) {

    }

    @Override
    public void mutePlayStreamAudio(String streamId, boolean isMute) {

    }

    @Override
    public void muteAllPlayStreamAudio(boolean isMute) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.muteAllRemoteAudioStreams(isMute);
        }
    }

    @Override
    public boolean isMuteAllPlayStreamAudio() {
        return false;
    }

    @Override
    public void setPlayVolume(String streamId, int volume) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.adjustPlaybackSignalVolume(volume);
        }
    }

    @Override
    public void setAllPlayStreamVolume(int volume) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.adjustPlaybackSignalVolume(volume);
        }
    }

    @Override
    public void muteMicrophone(boolean isMute) {
        RtcEngine engine = getEngine();
        if (engine != null) {
            engine.muteLocalAudioStream(isMute);
        }
    }

    @Override
    public void sendCommand(String roomId, String command, SendCommandResult result) {

    }

    @Override
    public void startSoundLevelMonitor() {

    }

    private IRtcEngineEventHandler mIRtcEngineEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            LogUtils.d("onConnectionStateChanged:" + state);
            MediaAudioEventHandler handler = mMediaAudioEventHandler;
            if (handler != null) {
                handler.onRoomStateUpdate(roomID, AgoraRoomStateConverter.converAudioRoomState(state), 0, null);
            }
        }
    };

}
