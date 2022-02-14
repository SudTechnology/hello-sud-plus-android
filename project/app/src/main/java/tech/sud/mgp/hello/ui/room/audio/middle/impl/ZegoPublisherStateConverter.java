package tech.sud.mgp.hello.ui.room.audio.middle.impl;

import im.zego.zegoexpress.constants.ZegoPublisherState;
import tech.sud.mgp.hello.ui.room.audio.middle.MediaAudioEnginePublisherSateType;

public class ZegoPublisherStateConverter {

    public static MediaAudioEnginePublisherSateType converMediaStateType(ZegoPublisherState state) {
        switch (state) {
            case NO_PUBLISH:
                return MediaAudioEnginePublisherSateType.NO_PUBLISH;
            case PUBLISH_REQUESTING:
                return MediaAudioEnginePublisherSateType.PUBLISH_REQUESTING;
            case PUBLISHING:
                return MediaAudioEnginePublisherSateType.PUBLISHING;
        }
        return null;
    }

}
