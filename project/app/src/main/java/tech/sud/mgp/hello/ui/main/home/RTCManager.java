package tech.sud.mgp.hello.ui.main.home;

import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.hello.rtc.audio.impl.agora.AgoraAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.impl.zego.ZegoAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.rtc.audio.model.ZegoAudioConfigModel;
import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;

/**
 * 应用rtc管理
 */
public class RTCManager {

    /**
     * 根据配置，应用rtc引擎
     *
     * @param config
     */
    public static void applyRtcEngine(BaseRtcConfig config) {
        if (config instanceof ZegoConfig) {
            AudioEngineFactory.create(ZegoAudioEngineImpl.class);
            ZegoConfig zegoConfig = (ZegoConfig) config;
            ZegoAudioConfigModel zegoAudioConfigModel = new ZegoAudioConfigModel();
            zegoAudioConfigModel.appId = zegoConfig.appId;
            zegoAudioConfigModel.appSign = zegoConfig.appKey;
            AudioEngineFactory.getEngine().initWithConfig(zegoAudioConfigModel);
        } else if (config instanceof AgoraConfig) {
            AudioEngineFactory.create(AgoraAudioEngineImpl.class);
            AgoraConfig agoraConfig = (AgoraConfig) config;
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = agoraConfig.appId;
            AudioEngineFactory.getEngine().initWithConfig(audioConfigModel);
        }
    }

    /**
     * 应用当前配置的rtcEngine
     */
    public static void applyRtcEngine() {
        ISudAudioEngine engine = AudioEngineFactory.getEngine();
        if (engine != null) {
            return;
        }
        BaseRtcConfig selectRtcConfig = AppData.getInstance().getSelectRtcConfig();
        if (selectRtcConfig == null) {
            Object rtcConfigSerializable = GlobalCache.getInstance().getSerializable(GlobalCache.RTC_CONFIG_KEY);
            if (rtcConfigSerializable instanceof BaseRtcConfig) {
                selectRtcConfig = (BaseRtcConfig) rtcConfigSerializable;
            }
        }
        applyRtcEngine(selectRtcConfig);
    }
}
