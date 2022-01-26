package tech.sud.mgp.audio.example.manager;

import tech.sud.mgp.audio.middle.MediaAudioEngineManager;
import tech.sud.mgp.audio.middle.MediaAudioEngineProtocol;

/**
 * 语音引擎
 */
public class AudioEngineManager extends BaseServiceManager {

    private MediaAudioEngineProtocol getEngine() {
        return MediaAudioEngineManager.shared().audioEngine;
    }

}
