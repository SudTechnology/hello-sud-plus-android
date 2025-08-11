package tech.sud.mgp.hello.ui.main.home.manager;

import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.CommsEaseConfig;
import tech.sud.mgp.hello.service.main.config.RongCloudConfig;
import tech.sud.mgp.hello.service.main.config.TencentCloudConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;
import tech.sud.mgp.rtc.audio.core.ISudAudioEngine;
import tech.sud.mgp.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.rtc.audio.impl.agora.AgoraAudioEngineImpl;
import tech.sud.mgp.rtc.audio.impl.netease.NeteaseAudioEngineImpl;
import tech.sud.mgp.rtc.audio.impl.rcloud.RCloudAudioEngineImpl;
import tech.sud.mgp.rtc.audio.impl.tx.TXAudioEngineImpl;
import tech.sud.mgp.rtc.audio.impl.zego.ZegoAudioEngineImpl;
import tech.sud.mgp.rtc.audio.model.AudioConfigModel;

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
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = config.appId;
            audioConfigModel.token = rtiToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof AgoraConfig) {
            AudioEngineFactory.create(AgoraAudioEngineImpl.class);
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = config.appId;
            audioConfigModel.token = rtiToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof RongCloudConfig) {
            AudioEngineFactory.create(RCloudAudioEngineImpl.class);
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appKey = config.appKey;
            audioConfigModel.token = rtcToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        } else if (config instanceof CommsEaseConfig) {
            AudioEngineFactory.create(NeteaseAudioEngineImpl.class);
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appKey = config.appKey;
            audioConfigModel.token = rtiToken;
            audioConfigModel.userID = HSUserInfo.userId + "";
            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
        }
//        else if (config instanceof VolcConfig) {
//            AudioEngineFactory.create(VolcAudioEngineImpl.class);
//            AudioConfigModel audioConfigModel = new AudioConfigModel();
//            audioConfigModel.appId = config.appId;
//            audioConfigModel.token = rtiToken;
//            audioConfigModel.userID = HSUserInfo.userId + "";
//            AudioEngineFactory.getEngine().initWithConfig(Utils.getApp(), audioConfigModel, runnable);
//        }
        else if (config instanceof TencentCloudConfig) {
            AudioEngineFactory.create(TXAudioEngineImpl.class);
            AudioConfigModel audioConfigModel = new AudioConfigModel();
            audioConfigModel.appId = config.appId;
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
