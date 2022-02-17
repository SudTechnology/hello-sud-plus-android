package tech.sud.mgp.hello.rtc.audio.factory;

import tech.sud.mgp.hello.rtc.audio.core.IAudioEngine;

/**
 * 语音引擎管理类
 */
public class AudioEngineFactory {

    private static IAudioEngine audioEngine;

    private AudioEngineFactory() {
    }

    // 获取引擎实例
    public static IAudioEngine getEngine() {
        return audioEngine;
    }

    // 销毁实例，释放资源
    public static void destroy() {
        IAudioEngine engine = audioEngine;
        if (engine != null) {
            engine.destroy();
            audioEngine = null;
        }
    }

    // 创建引擎
    public static void create(Class<? extends IAudioEngine> cls) {
        destroy();
        try {
            audioEngine = cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
