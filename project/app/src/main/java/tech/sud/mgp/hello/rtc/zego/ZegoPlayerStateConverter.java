package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.constants.ZegoPlayerState;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEnginePlayerStateType;

public class ZegoPlayerStateConverter {

    public static MediaAudioEnginePlayerStateType converMediaStateType(ZegoPlayerState state) {
        switch (state) {
            case NO_PLAY:
                return MediaAudioEnginePlayerStateType.NO_PLAY;
            case PLAY_REQUESTING:
                return MediaAudioEnginePlayerStateType.PLAY_REQUESTING;
            case PLAYING:
                return MediaAudioEnginePlayerStateType.PLAYING;
        }
        return null;
    }

}
