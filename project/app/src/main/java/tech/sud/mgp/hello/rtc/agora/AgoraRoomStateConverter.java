package tech.sud.mgp.hello.rtc.agora;

import io.agora.rtc.Constants;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioRoomState;

public class AgoraRoomStateConverter {

    public static MediaAudioRoomState converAudioRoomState(int state) {
        switch (state) {
            case Constants.CONNECTION_STATE_DISCONNECTED:
                return MediaAudioRoomState.DISCONNECTED;
            case Constants.CONNECTION_STATE_CONNECTING:
                return MediaAudioRoomState.CONNECTING;
            case Constants.CONNECTION_STATE_CONNECTED:
                return MediaAudioRoomState.CONNECTED;
        }
        return null;
    }

}
