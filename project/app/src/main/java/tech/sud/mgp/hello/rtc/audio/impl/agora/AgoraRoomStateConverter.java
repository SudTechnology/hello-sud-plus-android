package tech.sud.mgp.hello.rtc.audio.impl.agora;

import io.agora.rtc.Constants;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomState;

public class AgoraRoomStateConverter {

    public static AudioRoomState converAudioRoomState(int state) {
        switch (state) {
            case Constants.CONNECTION_STATE_DISCONNECTED:
                return AudioRoomState.DISCONNECTED;
            case Constants.CONNECTION_STATE_CONNECTING:
                return AudioRoomState.CONNECTING;
            case Constants.CONNECTION_STATE_CONNECTED:
                return AudioRoomState.CONNECTED;
        }
        return null;
    }

}
