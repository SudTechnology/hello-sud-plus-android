package tech.sud.mgp.hello.ui.splash;

import android.content.Intent;
import android.os.Handler;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;
import tech.sud.mgp.hello.ui.login.LoginActivity;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;
import tech.sud.mgp.hello.ui.viewmodel.ConfigViewModel;

/**
 * 闪屏页
 */
public class SplashActivity extends BaseActivity {

    private Handler handler = new Handler();
    private final ConfigViewModel configViewModel = new ConfigViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        super.initData();
        handler.postDelayed(this::jumpPage, 2000);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        configViewModel.initConfigSuccessLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                loginSuccess();
            }
        });
    }

    private void jumpPage() {
        long userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, -1L);
        if (userId == -1L) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String name = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_NAME_KEY);
            LoginRepository.login(userId, name, this, new RxCallback<LoginResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    loginSuccess();
                }

                @Override
                public void onNext(BaseResponse<LoginResponse> t) {
                    super.onNext(t);
                    if (t.getRetCode() == RetCode.SUCCESS) {
                        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_ID_KEY, t.getData().userId);
                        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, t.getData().avatar);
                        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_NAME_KEY, t.getData().nickname);
                        HSUserInfo.userId = t.getData().userId;
                        HSUserInfo.nickName = t.getData().nickname;
                        HSUserInfo.avatar = t.getData().avatar;
                        HSUserInfo.token = t.getData().token;
                    } else {
                        ToastUtils.showShort(ResponseUtils.conver(t));
                    }
                    configViewModel.getBaseConfig(SplashActivity.this);
                }
            });
        }
    }

    private void loginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}