package tech.sud.mgp.hello.ui.main.settings.activity;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.rtc.audio.factory.AudioEngineFactory;
import tech.sud.mgp.hello.rtc.audio.impl.agora.AgoraAudioEngineImpl;
import tech.sud.mgp.hello.rtc.audio.impl.zego.ZegoAudioEngineImpl;
import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;

/**
 * 切换rtc服务业务逻辑
 */
public class ChangeRtcViewModel extends BaseViewModel {

    private final Executor executor = Executors.newSingleThreadExecutor();

    // rtc列表数据
    public final MutableLiveData<List<BaseRtcConfig>> rtcDatasLiveData = new MutableLiveData<>();

    // 获取rtc列表
    public void getRtcList(String[] rtcServices) {
        executor.execute(() -> {
            List<BaseRtcConfig> list = new ArrayList<>();
            BaseConfigResp baseConfigResp = (BaseConfigResp) GlobalCache.getInstance().getSerializable(GlobalCache.BASE_CONFIG_KEY);
            for (int i = 0; i < rtcServices.length; i++) {
                String name = rtcServices[i];
                switch (i) {
                    case 0: // 即构
                        if (baseConfigResp != null && baseConfigResp.zegoCfg != null) {
                            list.add(baseConfigResp.zegoCfg);
                        } else {
                            list.add(new BaseRtcConfig(null, name));
                        }
                        break;
                    case 1: // 声网
                        if (baseConfigResp != null && baseConfigResp.agoraCfg != null) {
                            list.add(baseConfigResp.agoraCfg);
                        } else {
                            list.add(new BaseRtcConfig(null, name));
                        }
                        break;
                    default:
                        list.add(new BaseRtcConfig(null, name));
                        break;
                }
            }
            rtcDatasLiveData.postValue(list);
        });
    }

    // 设置所使用的rtc配置
    public void setRtcConfig(BaseRtcConfig config) {
        if (config == null) return;
        // 设置引擎
        applyRtcEngine(config);

        // 保存配置
        AppData.getInstance().setSelectRtcConfig(config);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                GlobalCache.getInstance().put(GlobalCache.RTC_CONFIG_KEY, config);
            }
        });
    }

    // 判断当前所使用的rtc是否是该配置
    public boolean isSelectRtcConfig(BaseRtcConfig config) {
        BaseRtcConfig selectRtcConfig = AppData.getInstance().getSelectRtcConfig();
        return selectRtcConfig != null && selectRtcConfig.rtcType != null && selectRtcConfig.rtcType.equals(config.rtcType);
    }

    /**
     * 根据配置，应用rtc引擎
     *
     * @param config
     */
    public static void applyRtcEngine(BaseRtcConfig config) {
        if (config instanceof ZegoConfig) {
            AudioEngineFactory.create(ZegoAudioEngineImpl.class);
            ZegoConfig zegoConfig = (ZegoConfig) config;
            AudioEngineFactory.getEngine().config(zegoConfig.appId, zegoConfig.appKey);
        } else if (config instanceof AgoraConfig) {
            AudioEngineFactory.create(AgoraAudioEngineImpl.class);
            AgoraConfig agoraConfig = (AgoraConfig) config;
            AudioEngineFactory.getEngine().config(agoraConfig.appId, agoraConfig.appKey);
        }
    }

}
