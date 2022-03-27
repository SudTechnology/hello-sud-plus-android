package tech.sud.mgp.hello.rtc.audio.impl.rcloud;

import android.content.Context;

import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEventListener;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.AudioJoinRoomModel;

public class RCloudAudioEngineImpl implements ISudAudioEngine {
    @Override
    public void setEventListener(ISudAudioEventListener listener) {

    }

    @Override
    public void initWithConfig(Context context, AudioConfigModel model) {

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
}
