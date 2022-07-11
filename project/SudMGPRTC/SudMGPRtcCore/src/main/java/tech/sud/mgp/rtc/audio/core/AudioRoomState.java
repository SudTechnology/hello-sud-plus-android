package tech.sud.mgp.rtc.audio.core;

/**
 * 语聊房房间状态
 */
public enum AudioRoomState {
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2);

    private int value;

    private AudioRoomState(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static AudioRoomState getZegoRoomState(int value) {
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
