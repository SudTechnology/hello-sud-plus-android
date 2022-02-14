package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.constants.ZegoUpdateType;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEngineUpdateType;

public class ZegoUpdateTypeConverter {

    public static MediaAudioEngineUpdateType converMediaAudioEnginUpdateType(ZegoUpdateType type) {
        switch (type) {
            case ADD:
                return MediaAudioEngineUpdateType.ADD;
            case DELETE:
                return MediaAudioEngineUpdateType.DELETE;
        }
        return null;
    }

}
