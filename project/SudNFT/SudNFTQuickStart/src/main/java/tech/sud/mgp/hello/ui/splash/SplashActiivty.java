package tech.sud.mgp.hello.ui.splash;

import android.content.Intent;

import java.util.Random;

import me.jessyan.autosize.internal.CancelAdapt;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.ui.main.MainActivity;

public class SplashActiivty extends BaseActivity implements CancelAdapt {

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
    protected void initWidget() {
        super.initWidget();
        LoginRepository.loadUserInfo();
        if (!HSUserInfo.isLogin()) {
            HSUserInfo.userId = new Random().nextInt(Integer.MAX_VALUE - 1) + 1;
            HSUserInfo.nickName = "sud";
            LoginRepository.saveUserInfo();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}