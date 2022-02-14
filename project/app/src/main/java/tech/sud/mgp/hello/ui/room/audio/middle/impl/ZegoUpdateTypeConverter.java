package tech.sud.mgp.hello.ui.room.audio.middle.impl;

import im.zego.zegoexpress.constants.ZegoUpdateType;
import tech.sud.mgp.hello.ui.room.audio.middle.MediaAudioEngineUpdateType;

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
