package tech.sud.mgp.hello.ui.splash;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.util.concurrent.Executor;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.model.UserInfoConverter;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.service.login.resp.RefreshTokenResponse;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.CheckUpgradeResp;
import tech.sud.mgp.hello.ui.common.viewmodel.ConfigViewModel;

/**
 * 闪屏页的业务逻辑
 */
public class SplashViewModel extends BaseViewModel {

    private final ConfigViewModel configViewModel = new ConfigViewModel();
    public final MutableLiveData<Object> startLoginPageLiveData = new MutableLiveData<>(); // 去登录页
    public final MutableLiveData<Object> startMainPageLiveData = new MutableLiveData<>(); // 去主页
    public final MutableLiveData<CheckUpgradeResp> showUpgradeLiveData = new MutableLiveData<>(); // 展示升级信息
    private final Executor executor = ThreadUtils.getIoPool();

    // 初始化
    public void init(RxAppCompatActivity owner) {
        configViewModel.initConfigSuccessLiveData.observeForever(configObserver);
        checkUpgrade(owner);
    }

    // 1.检查应用升级
    private void checkUpgrade(RxAppCompatActivity owner) {
        HomeRepository.checkUpgrade(owner, new RxCallback<CheckUpgradeResp>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                checkLogin(owner);
            }

            @Override
            public void onNext(BaseResponse<CheckUpgradeResp> resp) {
                super.onNext(resp);
                if (resp.getRetCode() == RetCode.SUCCESS) {
                    if (isShowUpgradeDialog(resp.getData())) {
                        showUpgradeLiveData.setValue(resp.getData());
                    } else {
                        checkLogin(owner);
                    }
                } else {
                    checkLogin(owner);
                    ToastUtils.showShort(ResponseUtils.conver(resp));
                }
            }
        });
    }

    // 是否要显示更新弹窗
    private boolean isShowUpgradeDialog(CheckUpgradeResp resp) {
        if (resp != null && resp.upgradeId != null) {
            if (resp.upgradeType == CheckUpgradeResp.FORCE_UPGRADE) { // 强更要显示
                return true;
            } else { // 引导更新，每天只弹一次
                long showTimestamp = GlobalSP.getSP().getLong(GlobalSP.KEY_SHOW_GUIDE_UPGRADE_TIMESTAMP);
                if (TimeUtils.isToday(showTimestamp)) {
                    return false;
                } else {
                    GlobalSP.getSP().put(GlobalSP.KEY_SHOW_GUIDE_UPGRADE_TIMESTAMP, System.currentTimeMillis());
                    return true;
                }
            }
        }
        return false;
    }

    // 更新弹窗交互已经完成
    public void upgradeCompleted(RxAppCompatActivity owner) {
        checkLogin(owner);
    }

    // 2.检查是否已登录并处理
    private void checkLogin(RxAppCompatActivity owner) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LoginRepository.loadUserInfo();
                if (HSUserInfo.userId == -1) {
                    startLoginPageLiveData.postValue(null);
                } else {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String refreshToken = HSUserInfo.refreshToken;
                            if (TextUtils.isEmpty(refreshToken)) { // 没有refreshToken时，直接跳登录页
                                startLoginPageLiveData.postValue(null);
                                return;
                            }
                            LoginRepository.refreshToken(refreshToken, owner, new RxCallback<RefreshTokenResponse>() {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    loginSuccess();
                                }

                                @Override
                                public void onNext(BaseResponse<RefreshTokenResponse> t) {
                                    super.onNext(t);
                                    if (t.getRetCode() == RetCode.SUCCESS) {
                                        UserInfoConverter.conver(t.getData());
                                        ThreadUtils.getIoPool().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                LoginRepository.saveUserInfo();
                                            }
                                        });
                                        configViewModel.getBaseConfig(owner);
                                    } else {
                                        ToastUtils.showShort(ResponseUtils.conver(t));
                                        startLoginPageLiveData.postValue(null);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    // 在登录成功且获取配置成功之后返回
    private final Observer<Object> configObserver = new Observer<Object>() {
        @Override
        public void onChanged(Object o) {
            loginSuccess();
        }
    };

    private void loginSuccess() {
        startMainPageLiveData.setValue(null);
    }

}
