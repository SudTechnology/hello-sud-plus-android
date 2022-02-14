package tech.sud.mgp.hello.rtc.protocol;

public enum MediaAudioEnginePlayerStateType {
    NO_PLAY(0),
    PLAY_REQUESTING(1),
    PLAYING(2);

    private int value;

    private MediaAudioEnginePlayerStateType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MediaAudioEnginePlayerStateType getZegoPlayerState(int value) {
        try {
            if (NO_PLAY.value == value) {
                return NO_PLAY;
            } else if (PLAY_REQUESTING.value == value) {
                return PLAY_REQUESTING;
            } else {
                return PLAYING.value == value ? PLAYING : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
