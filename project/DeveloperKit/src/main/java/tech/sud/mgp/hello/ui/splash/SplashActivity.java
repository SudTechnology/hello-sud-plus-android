package tech.sud.mgp.hello.ui.splash;

import android.content.Intent;

import androidx.lifecycle.Observer;

import me.jessyan.autosize.internal.CancelAdapt;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;

/**
 * 闪屏页
 * 实现{@link CancelAdapt}是标识此页面不进行屏幕适配，因为有做闪屏页启动优化的处理
 */
public class SplashActivity extends BaseActivity implements CancelAdapt {

    private SplashViewModel viewModel = new SplashViewModel();

    @Override
    protected boolean beforeSetContentView() {
        if (!this.isTaskRoot()) {
            //此操作是为了防止重复启动，主要是发生在应用安装成功后打开APP，然后回到launcher程序再打开时造成的问题
            if (getIntent() != null) {
                if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(getIntent().getAction())) {
                    return true;
                }
            }
        }
        return super.beforeSetContentView();
    }

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
        viewModel.init();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewModel.initCompletedLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
    }
}