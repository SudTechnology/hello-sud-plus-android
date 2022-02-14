package tech.sud.mgp.hello.rtc.protocol;

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
