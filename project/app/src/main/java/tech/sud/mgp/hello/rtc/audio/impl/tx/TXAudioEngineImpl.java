package tech.sud.mgp.hello.rtc.audio.impl.tx;

import tech.sud.mgp.hello.rtc.audio.core.AudioRoomConfig;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEngine;
import tech.sud.mgp.hello.rtc.audio.core.IAudioEventHandler;

/**
 * 腾讯云引擎实现类
 */
public class TXAudioEngineImpl implements IAudioEngine {
    @Override
    public void setEventHandler(IAudioEventHandler handler) {

    }

    @Override
    public void config(String appId, String appKey) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void loginRoom(String roomId, AudioUser user, AudioRoomConfig config) {

    }

    @Override
    public void logoutRoom() {

    }

    @Override
    public void startPublish(String streamId) {

    }

    @Override
    public void stopPublishStream() {

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

    }

    @Override
    public void startAudioDataListener() {

    }

    @Override
    public void stopAudioDataListener() {

    }
}