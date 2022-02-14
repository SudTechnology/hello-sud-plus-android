package tech.sud.mgp.hello.ui.room.audio.middle;

/**
 * 语聊房房间状态
 */
public enum MediaAudioRoomState {
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2);

    private int value;

    private MediaAudioRoomState(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MediaAudioRoomState getZegoRoomState(int value) {
        try {
            if (DISCONNECTED.value == value) {
                return DISCONNECTED;
            } else if (CONNECTING.value == value) {
                return CONNECTING;
            } else {
                return CONNECTED.value == value ? CONNECTED : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
