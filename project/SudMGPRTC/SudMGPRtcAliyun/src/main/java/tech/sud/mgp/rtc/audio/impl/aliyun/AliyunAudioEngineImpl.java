package tech.sud.mgp.rtc.audio.impl.aliyun;

import android.content.Context;
import android.view.View;

import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.rtc.audio.core.MediaViewMode;
import tech.sud.mgp.rtc.audio.core.SudAudioSource;
import tech.sud.mgp.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.rtc.audio.model.AudioJoinRoomModel;

/**
 * 阿里云引擎实现类
 */
public class AliyunAudioEngineImpl implements ISudAudioEngine {
    @Override
    public void setEventListener(ISudAudioEventListener listener) {

    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model, Runnable success) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void joinRoom(AudioJoinRoomModel model) {

    }

    @Override
    public void leaveRoom() {

    }

    @Override
    public void startPublishStream() {

    }

    @Override
    public void stopPublishStream() {

    }

    @Override
    public void startSubscribingStream() {

    }

    @Override
    public void stopSubscribingStream() {

    }

    @Override
    public void sendCommand(String command, SendCommandListener listener) {

    }

    @Override
    public void startPCMCapture() {

    }

    @Override
    public void stopPCMCapture() {

    }

    @Override
    public void setAudioRouteToSpeaker(boolean enabled) {

    }

    @Override
    public void startPlayingStream(String streamID, MediaViewMode mediaViewMode, View view) {

    }

    @Override
    public void stopPlayingStream(String streamID) {

    }

    @Override
    public void playAudio(SudAudioSource audioSource) {

    }
}
