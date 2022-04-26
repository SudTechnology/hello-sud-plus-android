package tech.sud.mgp.hello.rtc.audio.impl.tx;

import android.content.Context;

import com.blankj.utilcode.util.ThreadUtils;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

/**
 * 腾讯云引擎实现类
 */
public class TXAudioEngineImpl implements ISudAudioEngine {

    private ISudAudioEventListener mISudAudioEventListener;
    private TRTCCloud mEngine;
    private String mUserID = ""; // 自己的userId

    /**
     * 记录房间内用户数量
     */
    private HashSet<String> roomUserList = new HashSet<>();

    private TRTCCloud getEngine() {
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

        // 设置自己的userId
        mUserID = model.userID;
        mEngine = TRTCCloud.sharedInstance(context.getApplicationContext());
        if (mEngine != null) {
            mEngine.setListener(trtcCloudListener);
            mEngine.enableAudioVolumeEvaluation(300);
        }

        if (success != null) {
            success.run();
        }
    }

    @Override
    public void destroy() {
        TRTCCloud.destroySharedInstance();
        mEngine = null;
        roomUserList.clear();
    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {
        if (model == null)
            return;

        TRTCCloud engine = getEngine();
        if (engine != null) {
            long appID = 0;
            try {
                appID = Long.parseLong(model.appId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams();
            trtcParams.sdkAppId = (int)appID;
            trtcParams.userId = model.userID;
            trtcParams.userSig = model.token;
            trtcParams.strRoomId = model.roomID;
            engine.enterRoom(trtcParams, TRTCCloudDef.TRTC_APP_SCENE_AUDIOCALL);
        }
    }

    @Override
    public void leaveRoom() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.exitRoom();
        }
        roomUserList.clear();
    }

    @Override
    public void startPublishStream() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.startLocalAudio(TRTCCloudDef.TRTC_AUDIO_QUALITY_DEFAULT);
        }
    }

    @Override
    public void stopPublishStream() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.stopLocalAudio();
        }
    }

    @Override
    public void startSubscribingStream() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.muteAllRemoteAudio(false);
        }
    }

    @Override
    public void stopSubscribingStream() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.muteAllRemoteAudio(true);
        }
    }

    @Override
    public void startPCMCapture() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            TRTCCloudDef.TRTCAudioFrameCallbackFormat format = new TRTCCloudDef.TRTCAudioFrameCallbackFormat();
            format.sampleRate = TRTCCloudDef.TRTCAudioSampleRate16000;
            format.channel = 1;
            format.samplesPerCall = 160;
            engine.setCapturedRawAudioFrameCallbackFormat(format);

            /* 设置原始音频数据回调 */
            engine.setAudioFrameListener(trtcAudioFrameListener);
        }
    }

    @Override
    public void stopPCMCapture() {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            /* 置空原始音频数据回调 */
            engine.setAudioFrameListener(null);
        }
    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {
        TRTCCloud engine = getEngine();
        if (engine != null) {
            if (enabled) {
                engine.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
            } else {
                engine.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
            }
        }
    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {
        if (command == null) {
            return;
        }

        TRTCCloud engine = getEngine();
        if (engine != null) {
            engine.sendCustomCmdMsg(0, command.getBytes(), true, true);
        }
    }

    // 更新房间内用户总人数
    private void updateRoomUserCount() {
        ISudAudioEventListener handler = mISudAudioEventListener;
        if (handler != null) {
            handler.onRoomOnlineUserCountUpdate(roomUserList.size() + 1);
        }
    }

    private final TRTCCloudListener trtcCloudListener = new TRTCCloudListener() {
        @Override
        public void onEnterRoom(long result) {
            if (result > 0) {
                ISudAudioEventListener listener = mISudAudioEventListener;
                if (listener != null) {
                    listener.onRoomStateUpdate(AudioRoomState.CONNECTED, 0, null);
                    updateRoomUserCount();
                }
            }
        }

        @Override
        public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] message) {
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener != null) {
                String command = new String(message);
                listener.onRecvCommand(userId, command);
            }
        }

        @Override
        public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {
            ISudAudioEventListener listener = mISudAudioEventListener;
            if (listener == null || userVolumes == null || userVolumes.size() == 0) {
                return;
            }

            HashMap<String, Float> soundLevels = null;
            Float localSoundLevel = null;
            for (TRTCCloudDef.TRTCVolumeInfo volumeInfo : userVolumes) {
                if (mUserID.equals(volumeInfo.userId)) {
                    localSoundLevel = Float.valueOf(volumeInfo.volume);
                } else {
                    if (soundLevels == null) {
                        soundLevels = new HashMap<>();
                    }
                    soundLevels.put(volumeInfo.userId, (float) volumeInfo.volume);
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

        @Override
        public void onRemoteUserEnterRoom(String userId) {
            roomUserList.add(userId);
            updateRoomUserCount();
        }

        @Override
        public void onRemoteUserLeaveRoom(String userId, int reason) {
            roomUserList.remove(userId);
            updateRoomUserCount();
        }
    };

    private final TRTCCloudListener.TRTCAudioFrameListener trtcAudioFrameListener = new TRTCCloudListener.TRTCAudioFrameListener() {
        @Override
        public void onCapturedRawAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ISudAudioEventListener listener = mISudAudioEventListener;
                    if (listener != null) {
                        AudioPCMData audioPCMData = new AudioPCMData();
                        audioPCMData.data = ByteBuffer.wrap(trtcAudioFrame.data);
                        audioPCMData.dataLength = trtcAudioFrame.data.length;
                        listener.onCapturedPCMData(audioPCMData);
                    }
                }
            });
        }

        @Override
        public void onLocalProcessedAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {

        }

        @Override
        public void onRemoteUserAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame, String s) {

        }

        @Override
        public void onMixedPlayAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {

        }

        @Override
        public void onMixedAllAudioFrame(TRTCCloudDef.TRTCAudioFrame trtcAudioFrame) {

        }
    };
}
