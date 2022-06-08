package tech.sud.mgp.hello.rtc.audio.core;

public enum SudRTIClientRole {
    // 主播可以发流也可以收留
    SUD_RTI_CLIENT_ROLE_BROADCASTER(1),
    // 观众只能收流不能发流
    SUD_RTI_CLIENT_ROLE_AUDIENCE(2);

    private int value;

    private SudRTIClientRole(int role) {
        this.value = role;
    }

    public int value() {
        return this.value;
    }
}
