package tech.sud.mgp.hello.rtc.protocol;

/**
 * 语音引擎管理类
 */
public class MediaAudioEngineManager {

    private static MediaAudioEngineProtocol audioEngine;

    private MediaAudioEngineManager() {
    }

    // 获取引擎实例
    public static MediaAudioEngineProtocol getEngine() {
        return audioEngine;
    }

    // 销毁实例，释放资源
    public static void destroy() {
        MediaAudioEngineProtocol engine = audioEngine;
        if (engine != null) {
            engine.destroy();
            audioEngine = null;
        }
    }

    // 创建引擎
    public static void makeEngine(Class<? extends MediaAudioEngineProtocol> cls) {
        destroy();
        try {
            audioEngine = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
