package tech.sud.mgp.sudmgpimcore.model;

public enum SudIMRoomState {

    UNKNOWN(-1),
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2);

    private int value;

    private SudIMRoomState(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static SudIMRoomState getIMRoomState(int value) {
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
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }

        return UNKNOWN;
    }

}
