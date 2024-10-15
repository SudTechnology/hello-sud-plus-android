package tech.sud.mgp.sudmgpimcore.model;

public enum SudIMConnectionEvent {

    UNKNOWN(-1),
    SUCCESS(0),
    ACTIVE_LOGIN(1),
    LOGIN_TIMEOUT(2),
    LOGIN_INTERRUPTED(3),
    KICKED_OUT(4),
    TOKEN_EXPIRED(5);

    private int value;

    private SudIMConnectionEvent(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static SudIMConnectionEvent getIMConnectionEvent(int value) {
        try {
            if (SUCCESS.value == value) {
                return SUCCESS;
            }

            if (ACTIVE_LOGIN.value == value) {
                return ACTIVE_LOGIN;
            }

            if (LOGIN_TIMEOUT.value == value) {
                return LOGIN_TIMEOUT;
            }

            if (LOGIN_INTERRUPTED.value == value) {
                return LOGIN_INTERRUPTED;
            }

            if (KICKED_OUT.value == value) {
                return KICKED_OUT;
            }

            if (TOKEN_EXPIRED.value == value) {
                return TOKEN_EXPIRED;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }

        return UNKNOWN;
    }

}
