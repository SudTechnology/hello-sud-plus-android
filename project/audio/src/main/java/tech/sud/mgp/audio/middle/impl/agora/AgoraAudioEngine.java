package tech.sud.mgp.audio.middle.impl.agora;

import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;
import tech.sud.mgp.audio.middle.MediaAudioEventHandler;
import tech.sud.mgp.audio.middle.MediaRoomConfig;
import tech.sud.mgp.audio.middle.MediaUser;

// 声网SDK实现
public class AgoraAudioEngine implements MediaAudioEngineProtocol {

    private MediaAudioEventHandler mMediaAudioEventHandler;

    @Override
    public void setEventHandler(MediaAudioEventHandler handler) {
        mMediaAudioEventHandler = handler;
    }

    @Override
    public void config(String appId, String appKey) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void loginRoom(String roomId, MediaUser user, MediaRoomConfig config) {

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

    }

    @Override
    public boolean isMuteAllPlayStreamAudio() {
        return false;
    }

    @Override
    public void setPlayVolume(String streamId, int volume) {

    }

    @Override
    public void setAllPlayStreamVolume(int volume) {

    }

    @Override
    public void muteMicrophone(boolean isMute) {

    }

    @Override
    public void sendCommand(String roomId, String command, SendCommandResult result) {

    }

    @Override
    public void startSoundLevelMonitor() {

    }
}
