package tech.sud.mgp.hello.ui.splash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.util.concurrent.Executor;

import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
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
    private final Executor executor = ThreadUtils.getCachedPool();

    public void init(RxAppCompatActivity owner) {
        configViewModel.initConfigSuccessLiveData.observeForever(configObserver);
        checkUpgrade(owner);
    }

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
                    if (resp.getData() != null && resp.getData().upgradeId != null) {
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

    // 更新弹窗交互已经完成
    public void upgradeCompleted(RxAppCompatActivity owner) {
        checkLogin(owner);
    }

    // 检查登录
    private void checkLogin(RxAppCompatActivity owner) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LoginRepository.createUserInfo();
                if (HSUserInfo.userId == -1) {
                    startLoginPageLiveData.postValue(null);
                } else {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoginRepository.refreshToken(HSUserInfo.refreshToken, owner, new RxCallback<RefreshTokenResponse>() {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    loginSuccess();
                                }

                                @Override
                                public void onNext(BaseResponse<RefreshTokenResponse> t) {
                                    super.onNext(t);
                                    if (t.getRetCode() == RetCode.SUCCESS) {
                                        LoginRepository.saveRefreshToken(t.getData());
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
