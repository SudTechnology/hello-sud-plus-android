package tech.sud.mgp.hello;

import android.content.Intent;
import android.os.Handler;

import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.login.LoginActivity;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class MainActivity extends BaseActivity {

    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        handler.postDelayed(this::jumpPage, 2000);
    }

    private void jumpPage() {
        long userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY, -1L);
        if (userId == -1L) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}