package tech.sud.mgp.audio.middle;

public enum MediaAudioEnginePublisherSateType {
    NO_PUBLISH(0),
    PUBLISH_REQUESTING(1),
    PUBLISHING(2);

    private int value;

    private MediaAudioEnginePublisherSateType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static MediaAudioEnginePublisherSateType getZegoPublisherState(int value) {
        try {
            if (NO_PUBLISH.value == value) {
                return NO_PUBLISH;
            } else if (PUBLISH_REQUESTING.value == value) {
                return PUBLISH_REQUESTING;
            } else {
                return PUBLISHING.value == value ? PUBLISHING : null;
            }
        } catch (Exception var2) {
            throw new RuntimeException("The enumeration cannot be found");
        }
    }
}
