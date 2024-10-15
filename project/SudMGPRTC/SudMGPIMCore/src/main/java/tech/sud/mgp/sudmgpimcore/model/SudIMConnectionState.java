package tech.sud.mgp.sudmgpimcore.model;

public enum SudIMConnectionState {

    UNKNOWN(-1),
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2),
    RECONNECTING(3);

    private int value;

    private SudIMConnectionState(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static SudIMConnectionState getIMConnectionState(int value) {
        try {
            if (DISCONNECTED.value == value) {
                return DISCONNECTED;
            }

            if (CONNECTING.value == value) {
                return CONNECTING;
            }

            if (CONNECTED.value == value) {
                return CONNECTED;
            }

            if (RECONNECTING.value == value) {
                return RECONNECTING;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }

        return UNKNOWN;
    }

}


