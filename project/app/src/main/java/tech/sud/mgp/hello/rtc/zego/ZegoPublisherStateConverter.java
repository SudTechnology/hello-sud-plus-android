package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.constants.ZegoPublisherState;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEnginePublisherSateType;

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
