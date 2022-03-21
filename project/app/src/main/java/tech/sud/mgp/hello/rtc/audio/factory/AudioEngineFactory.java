package tech.sud.mgp.hello.rtc.audio.factory;

import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;

/**
 * 语音引擎管理类
 */
public class AudioEngineFactory {

    private static ISudAudioEngine audioEngine;

    private AudioEngineFactory() {
    }

    // 获取引擎实例
    public static ISudAudioEngine getEngine() {
        return audioEngine;
    }

    // 销毁实例，释放资源
    public static void destroy() {
        ISudAudioEngine engine = audioEngine;
        if (engine != null) {
            engine.destroy();
            audioEngine = null;
        }
    }

    // 创建引擎
    public static void create(Class<? extends ISudAudioEngine> cls) {
        destroy();
        try {
            audioEngine = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
