package tech.sud.mgp.hello.ui.room.audio.middle.impl;

import im.zego.zegoexpress.constants.ZegoRoomState;
import tech.sud.mgp.hello.ui.room.audio.middle.MediaAudioRoomState;

public class ZegoRoomStateConverter {

    public static MediaAudioRoomState converAudioRoomState(ZegoRoomState state) {
        switch (state) {
            case DISCONNECTED:
                return MediaAudioRoomState.DISCONNECTED;
            case CONNECTING:
                return MediaAudioRoomState.CONNECTING;
            case CONNECTED:
                return MediaAudioRoomState.CONNECTED;
        }
        return null;
    }

}
