package tech.sud.mgp.hello.ui.splash;

import android.content.Intent;

import androidx.lifecycle.Observer;

import me.jessyan.autosize.internal.CancelAdapt;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.service.main.resp.CheckUpgradeResp;
import tech.sud.mgp.hello.ui.login.LoginActivity;
import tech.sud.mgp.hello.ui.login.dialog.VersionUpgradeDialog;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;

/**
 * 闪屏页
 * 实现{@link CancelAdapt}是标识此页面不进行屏幕适配，因为有做闪屏页启动优化的处理
 */
public class SplashActivity extends BaseActivity implements CancelAdapt {

    private final SplashViewModel viewModel = new SplashViewModel();

    @Override
    protected void setStatusBar() {
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.init(this);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewModel.startLoginPageLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                startLoginPage();
            }
        });
        viewModel.startMainPageLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                startMainPage();
            }
        });
        viewModel.showUpgradeLiveData.observe(this, new Observer<CheckUpgradeResp>() {
            @Override
            public void onChanged(CheckUpgradeResp checkUpgradeResp) {
                showUpgrade(checkUpgradeResp);
            }
        });
    }

    // 展示升级信息
    private void showUpgrade(CheckUpgradeResp resp) {
        VersionUpgradeDialog dialog = VersionUpgradeDialog.getInstance(resp);
        dialog.show(getSupportFragmentManager(), null);
        dialog.setOnDestroyListener(new BaseDialogFragment.OnDestroyListener() {
            @Override
            public void onDestroy() {
                viewModel.upgradeCompleted(SplashActivity.this);
            }
        });
    }

    // 去登录页
    private void startLoginPage() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // 去主页
    private void startMainPage() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}