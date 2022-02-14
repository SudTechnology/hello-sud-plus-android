package tech.sud.mgp.hello.rtc.protocol;

public enum MediaAudioEngineNetworkStateType {
    OFFLINE(0),
    UNKNOWN(1),
    ETHERNET(2),
    WIFI(3),
    MODE_2G(4),
    MODE_3G(5),
    MODE_4G(6),
    MODE_5G(7);

    private int value;

    private MediaAudioEngineNetworkStateType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MediaAudioEngineNetworkStateType getZegoNetworkMode(int value) {
        try {
            if (OFFLINE.value == value) {
                return OFFLINE;
            } else if (UNKNOWN.value == value) {
                return UNKNOWN;
            } else if (ETHERNET.value == value) {
                return ETHERNET;
            } else if (WIFI.value == value) {
                return WIFI;
            } else if (MODE_2G.value == value) {
                return MODE_2G;
            } else if (MODE_3G.value == value) {
                return MODE_3G;
            } else if (MODE_4G.value == value) {
                return MODE_4G;
            } else {
                return MODE_5G.value == value ? MODE_5G : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
