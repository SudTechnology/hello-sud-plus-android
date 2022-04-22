package tech.sud.mgp.hello.ui.main.settings.activity;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.HelloSudApplication;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;

/**
 * 切换rtc服务业务逻辑
 */
public class ChangeRtcViewModel extends BaseViewModel {

    private final Executor executor = Executors.newSingleThreadExecutor();

    // rtc列表数据
    public final MutableLiveData<List<BaseRtcConfig>> rtcDatasLiveData = new MutableLiveData<>();

    // 获取rtc列表
    public void getRtcList() {
        executor.execute(() -> {
            Context context = HelloSudApplication.instance;
            List<BaseRtcConfig> list = new ArrayList<>();
            BaseConfigResp baseConfigResp = (BaseConfigResp) GlobalCache.getInstance().getSerializable(GlobalCache.BASE_CONFIG_KEY);

            // 添加zego
            String rtcNameZego = context.getString(R.string.rtc_name_zego);
            if (baseConfigResp != null && baseConfigResp.zegoCfg != null) {
                baseConfigResp.zegoCfg.desc = rtcNameZego;
                list.add(baseConfigResp.zegoCfg);
            } else {
                list.add(new BaseRtcConfig(null, rtcNameZego));
            }

            // 添加Agora
            String rtcNameAgora = context.getString(R.string.rtc_name_agora);
            if (baseConfigResp != null && baseConfigResp.agoraCfg != null) {
                baseConfigResp.agoraCfg.desc = rtcNameAgora;
                list.add(baseConfigResp.agoraCfg);
            } else {
                list.add(new BaseRtcConfig(null, rtcNameAgora));
            }

//            // 添加融云
//            String rtcNameRong = context.getString(R.string.rtc_name_rong_cloud);
//            if (baseConfigResp != null && baseConfigResp.rongCloudCfg != null) {
//                baseConfigResp.rongCloudCfg.desc = rtcNameRong;
//                list.add(baseConfigResp.rongCloudCfg);
//            } else {
//                list.add(new BaseRtcConfig(null, rtcNameRong));
//            }
//
//            // 添加网易云信
//            String rtcNameComms = context.getString(R.string.rtc_name_comms_ease);
//            if (baseConfigResp != null && baseConfigResp.commsEaseCfg != null) {
//                baseConfigResp.commsEaseCfg.desc = rtcNameComms;
//                list.add(baseConfigResp.commsEaseCfg);
//            } else {
//                list.add(new BaseRtcConfig(null, rtcNameComms));
//            }

            list.add(new BaseRtcConfig(null, context.getString(R.string.rtc_name_rong_cloud)));
            list.add(new BaseRtcConfig(null, context.getString(R.string.rtc_name_comms_ease)));

            // 添加其它暂未支持的rtc
            list.add(new BaseRtcConfig(null, context.getString(R.string.rtc_name_volc_engine)));
            list.add(new BaseRtcConfig(null, context.getString(R.string.rtc_name_alibaba_cloud)));
            list.add(new BaseRtcConfig(null, context.getString(R.string.rtc_name_tencent_cloud)));

            rtcDatasLiveData.postValue(list);
        });
    }

    // 设置所使用的rtc配置
    public void setRtcConfig(BaseRtcConfig config) {
        if (config == null) return;
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

}
