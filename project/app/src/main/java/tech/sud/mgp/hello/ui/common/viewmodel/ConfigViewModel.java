package tech.sud.mgp.hello.ui.common.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ThreadUtils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.util.concurrent.Executor;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.config.AgoraConfig;
import tech.sud.mgp.hello.service.main.config.AlibabaCloudConfig;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.CommsEaseConfig;
import tech.sud.mgp.hello.service.main.config.RongCloudConfig;
import tech.sud.mgp.hello.service.main.config.TencentCloudConfig;
import tech.sud.mgp.hello.service.main.config.VoicEngineConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;

/**
 * 配置相关的业务逻辑
 */
public class ConfigViewModel extends BaseViewModel {

    // 初始化配置成功的监听
    public final MutableLiveData<Object> initConfigSuccessLiveData = new MutableLiveData<>();
    private final Executor executor = ThreadUtils.getIoPool();

    /**
     * 获取基础配置
     */
    public void getBaseConfig(RxAppCompatActivity activity) {
        HomeRepository.getBaseConfig(activity, new RxCallback<BaseConfigResp>() {
            @Override
            public void onNext(BaseResponse<BaseConfigResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    baseConfigSuccess(t.getData());
                } else {
                    delayGetBaseConfig(activity);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayGetBaseConfig(activity);
            }
        });
    }

    // 获取基础配置成功
    private void baseConfigSuccess(BaseConfigResp baseConfigResp) {
        executor.execute(() -> {
            // 将基础配置保存起来
            AppData.getInstance().setBaseConfigResp(baseConfigResp);
            GlobalCache.getInstance().put(GlobalCache.BASE_CONFIG_KEY, baseConfigResp);

            // 应用rtc配置
            Object rtcConfigSerializable = GlobalCache.getInstance().getSerializable(GlobalCache.RTC_CONFIG_KEY);
            BaseRtcConfig baseRtcConfig = null;
            if (rtcConfigSerializable instanceof BaseRtcConfig) { // 本地已经保存了rtc配置，直接使用
                baseRtcConfig = refreshRtcConfig(rtcConfigSerializable, baseConfigResp);
            } else { // 本地未保存rtc配置，从基础配置当中找一个，设为默认rtc配置
                if (baseConfigResp != null) {
                    if (baseConfigResp.zegoCfg != null) {
                        baseRtcConfig = baseConfigResp.zegoCfg;
                    } else if (baseConfigResp.agoraCfg != null) {
                        baseRtcConfig = baseConfigResp.agoraCfg;
                    }
                    if (baseRtcConfig != null) {
                        GlobalCache.getInstance().put(GlobalCache.RTC_CONFIG_KEY, baseRtcConfig);
                    }
                }
            }
            AppData.getInstance().setSelectRtcConfig(baseRtcConfig);

            // 通知页面
            initConfigSuccessLiveData.postValue(null);
        });
    }

    /** 使用后端返回的最新的RTC配置 */
    private BaseRtcConfig refreshRtcConfig(Object rtcConfigSerializable, BaseConfigResp baseConfigResp) {
        if (baseConfigResp != null) {
            if (rtcConfigSerializable instanceof ZegoConfig) {
                if (baseConfigResp.zegoCfg == null) {
                    return (BaseRtcConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.zegoCfg;
                }
            }
            if (rtcConfigSerializable instanceof AgoraConfig) {
                if (baseConfigResp.agoraCfg == null) {
                    return (AgoraConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.agoraCfg;
                }
            }
            if (rtcConfigSerializable instanceof RongCloudConfig) {
                if (baseConfigResp.rongCloudCfg == null) {
                    return (RongCloudConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.rongCloudCfg;
                }
            }
            if (rtcConfigSerializable instanceof CommsEaseConfig) {
                if (baseConfigResp.commsEaseCfg == null) {
                    return (CommsEaseConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.commsEaseCfg;
                }
            }
            if (rtcConfigSerializable instanceof VoicEngineConfig) {
                if (baseConfigResp.voicEngineCfg == null) {
                    return (VoicEngineConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.voicEngineCfg;
                }
            }
            if (rtcConfigSerializable instanceof AlibabaCloudConfig) {
                if (baseConfigResp.alibabaCloudCfg == null) {
                    return (AlibabaCloudConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.alibabaCloudCfg;
                }
            }
            if (rtcConfigSerializable instanceof TencentCloudConfig) {
                if (baseConfigResp.tencentCloudCfg == null) {
                    return (TencentCloudConfig) rtcConfigSerializable;
                } else {
                    return baseConfigResp.tencentCloudCfg;
                }
            }
        }
        if (rtcConfigSerializable instanceof BaseRtcConfig) {
            return (BaseRtcConfig) rtcConfigSerializable;
        }
        return null;
    }

    // 获取配置失败之后的重试逻辑
    private void delayGetBaseConfig(RxAppCompatActivity activity) {
        executor.execute(() -> {
            Object baseConfigSerializable = GlobalCache.getInstance().getSerializable(GlobalCache.BASE_CONFIG_KEY);
            if (baseConfigSerializable instanceof BaseConfigResp) { // 本地有，直接使用
                ThreadUtils.runOnUiThread(() -> {
                    baseConfigSuccess((BaseConfigResp) baseConfigSerializable);
                });
            } else { // 本地没有，delay再请求网络
                ThreadUtils.runOnUiThreadDelayed(() -> {
                    if (activity.isDestroyed()) {
                        return;
                    }
                    getBaseConfig(activity);
                }, 3000);
            }
        });
    }

}
