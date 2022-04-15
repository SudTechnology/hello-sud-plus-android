package tech.sud.mgp.hello.ui.main.home;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.hello.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.hello.rtc.audio.impl.agora.AgoraAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.impl.netease.NeteaseAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.impl.rcloud.RCloudAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.impl.zego.ZegoAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.model.AudioConfigModel;
import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.CommsEaseConfig;
import tech.sud.mgp.hello.service.main.config.RongCloudConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;

/**
 * 应用rtc管理
 */
public class RTCManager {

    /**
     * 根据配置，应用rtc引擎
     *
     * @param config   配置
     * @param rtiToken im token
     */
    private static void applyRtcEngine(BaseRtcConfig config, String rtiToken, String rtcToken, Runnable runnable) {
        if (config instanceof ZegoConfig) {
            AudioEngineFactory.create(ZegoAudioEngineImpl.class);
            ZegoConfig zegoConfig = (ZegoConfig) config;
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = zegoConfig.appId;
            audioConfigModel.token = rtcToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof AgoraConfig) {
            AudioEngineFactory.create(AgoraAudioEngineImpl.class);
            AgoraConfig agoraConfig = (AgoraConfig) config;
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = agoraConfig.appId;
            audioConfigModel.token = rtiToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof RongCloudConfig) {
            AudioEngineFactory.create(RCloudAudioEngineImpl.class);
            RongCloudConfig rongCloudConfig = (RongCloudConfig) config;
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appKey = rongCloudConfig.appKey;
            audioConfigModel.token = rtcToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof CommsEaseConfig) {
            AudioEngineFactory.create(NeteaseAudioEngineImpl.class);
            CommsEaseConfig neteaseConfig = (CommsEaseConfig) config;
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appKey = neteaseConfig.appKey;
            audioConfigModel.token = rtiToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        }
    }

    /**
     * 应用当前配置的rtcEngine
     *
     * @param rtiToken im token
     */
    public static void applyRtcEngine(String rtiToken, String rtcToken, Runnable runnable) {
        ISudAudioEngine engine = AudioEngineFactory.getEngine();
        if (engine != null) {
            return;
        }
        applyRtcEngine(AppData.getInstance().getSelectRtcConfig(), rtiToken, rtcToken, runnable);
    }

}
