package tech.sud.mgp.hello.ui.scenes.base.manager;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONObject;

import java.util.List;

import tech.sud.mgp.hello.ui.scenes.base.service.SceneRoomServiceCallback;
import tech.sud.mgp.rtc.audio.core.AudioEngineUpdateType;
import tech.sud.mgp.rtc.audio.core.AudioStream;

/**
 * 房间音频流
 */
public class SceneStreamManager extends BaseServiceManager {

    private SceneRoomServiceManager parentManager;
    private StreamState state = StreamState.STOP;

    public SceneStreamManager(SceneRoomServiceManager sceneRoomServiceManager) {
        super();
        this.parentManager = sceneRoomServiceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        parentManager.sceneEngineManager.setOnRoomStreamUpdateListener(onRoomStreamUpdateListener);
    }

    public void openMic(String streamId) {
        if (TextUtils.isEmpty(streamId)) {
            ToastUtils.showLong("streamId is empty");
            return;
        } else {
            parentManager.sceneEngineManager.startPublishStream();
        }
        state = StreamState.MIC_STREAM;
        callbackStreamState();
    }

    public void closeMic() {
        state = StreamState.STOP;
        parentManager.sceneEngineManager.stopPublishStream();
        callbackStreamState();
    }

    public void callbackStreamState() {
        SceneRoomServiceCallback callback = parentManager.getCallback();
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

    private SceneEngineManager.OnRoomStreamUpdateListener onRoomStreamUpdateListener = new SceneEngineManager.OnRoomStreamUpdateListener() {
        @Override
        public void onRoomStreamUpdate(String roomId, AudioEngineUpdateType type, List<AudioStream> streamList, JSONObject extendedData) {
            if (streamList == null || streamList.size() == 0) {
                return;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        parentManager.sceneEngineManager.removeOnRoomStreamUpdateListener(onRoomStreamUpdateListener);
    }

    /** 回调页面数据 */
    public void callbackPageData() {
        callbackStreamState();
    }

    public enum StreamState {
        STOP, // 没有推流
        MIC_STREAM // 麦克风推流中
    }

}
