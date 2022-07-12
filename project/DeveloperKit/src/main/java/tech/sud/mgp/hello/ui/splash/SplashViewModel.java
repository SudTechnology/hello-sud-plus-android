package tech.sud.mgp.hello.ui.splash;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.List;

import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalCache;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.main.config.BaseRtcConfig;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.config.ZegoConfig;
import tech.sud.mgp.hello.ui.login.DeveloperKitUtils;

public class SplashViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> initCompletedLiveData = new MutableLiveData<>();

    public void init() {
        ThreadUtils.getIoPool().execute(new Runnable() {
            @Override
            public void run() {
                // 生成userId
                HSUserInfo.userId = DeveloperKitUtils.genUserID();

                // rtc配置
                BaseRtcConfig selectRtcConfig = AppData.getInstance().getSelectRtcConfig();
                if (selectRtcConfig == null) {
                    ZegoConfig zegoConfig = new ZegoConfig();
                    zegoConfig.appId = APPConfig.ZEGO_APP_ID;
                    GlobalCache.getInstance().put(GlobalCache.RTC_CONFIG_KEY, zegoConfig);
                    AppData.getInstance().setSelectRtcConfig(zegoConfig);
                }

                // sud配置
                SudConfig sudConfig = AppData.getInstance().getSudConfig();
                if (sudConfig == null) {
                    sudConfig = (SudConfig) GlobalCache.getInstance().getSerializable(GlobalCache.SUD_CONFIG);
                    if (sudConfig == null) {
                        GameRepository.sudAppList(null, new RxCallback<List<SudConfig>>() {
                            @Override
                            public void onSuccess(List<SudConfig> sudConfigs) {
                                super.onSuccess(sudConfigs);
                                if (sudConfigs != null && sudConfigs.size() > 0) {
                                    AppData.getInstance().setSudConfig(sudConfigs.get(0));
                                }
                            }
                        });
                    }
                    AppData.getInstance().setSudConfig(sudConfig);
                }
                initCompletedLiveData.postValue(true);
            }
        });
    }

}
