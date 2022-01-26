package tech.sud.mgp.audio.middle.impl;

import im.zego.zegoexpress.constants.ZegoPlayerState;
import im.zego.zegoexpress.constants.ZegoPublisherState;
import tech.sud.mgp.audio.middle.MediaAudioEnginePlayerStateType;
import tech.sud.mgp.audio.middle.MediaAudioEnginePublisherSateType;

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
