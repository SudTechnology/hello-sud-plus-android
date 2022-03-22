package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONObject;

import java.util.List;

import tech.sud.mgp.hello.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
import tech.sud.mgp.hello.ui.scenes.base.service.AudioRoomServiceCallback;

/**
 * 房间音频流
 */
public class AudioStreamManager extends BaseServiceManager {

    private AudioRoomServiceManager parentManager;
    private StreamState state = StreamState.STOP;

    public AudioStreamManager(AudioRoomServiceManager audioRoomServiceManager) {
        super();
        this.parentManager = audioRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.audioEngineManager.setOnRoomStreamUpdateListener(onRoomStreamUpdateListener);
    }

    public void openMic(String streamId) {
        if (TextUtils.isEmpty(streamId)) {
            ToastUtils.showLong("streamId is empty");
            return;
        } else {
            parentManager.audioEngineManager.startPublish(streamId);
        }
        state = StreamState.MIC_STREAM;
        callbackStreamState();
    }

    public void closeMic() {
        state = StreamState.STOP;
        parentManager.audioEngineManager.stopPublishStream();
        callbackStreamState();
    }

    public void callbackStreamState() {
        AudioRoomServiceCallback callback = parentManager.getCallback();
        if (callback == null) return;
        switch (state) {
            case STOP:
                callback.onMicStateChanged(false);
                break;
            case MIC_STREAM:
                callback.onMicStateChanged(true);
                break;
        }
    }

    /**
     * 是否正在推流当中
     *
     * @return true为推流中
     */
    public boolean isPublishingStream() {
        if (state == StreamState.MIC_STREAM) {
            return true;
        }
        return false;
    }

    private AudioEngineManager.OnRoomStreamUpdateListener onRoomStreamUpdateListener = new AudioEngineManager.OnRoomStreamUpdateListener() {
        @Override
        public void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData) {
            if (streamList == null || streamList.size() == 0) {
                return;
            }
            switch (type) {
                case ADD:
                    for (AudioStream audioStream : streamList) {
                        parentManager.audioEngineManager.startPlayingStream(audioStream.streamID);
                    }
                    break;
                case DELETE:
                    for (AudioStream audioStream : streamList) {
                        parentManager.audioEngineManager.stopPlayingStream(audioStream.streamID);
                    }
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.audioEngineManager.removeOnRoomStreamUpdateListener(onRoomStreamUpdateListener);
    }

    public enum StreamState {
        STOP, // 没有推流
        MIC_STREAM // 麦克风推流中
    }

}
