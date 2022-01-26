package tech.sud.mgp.audio.middle;

public class MediaAudioEngineManager {

    private static final MediaAudioEngineManager mInstance = new MediaAudioEngineManager();
    public MediaAudioEngineProtocol audioEngine;

    private MediaAudioEngineManager() {
    }

    public static MediaAudioEngineManager shared() {
        return mInstance;
    }

    public void makeEngine(Class<? extends MediaAudioEngineProtocol> cls) {
        try {
            audioEngine = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
