package tech.sud.mgp.hello;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.login.LoginActivity;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler.postDelayed(this::jumpPage,2000);
    }

    private void jumpPage(){
        long userId = AppSharedPreferences.getSP().getLong(AppSharedPreferences.USER_ID_KEY,-1L);
        if (userId == -1L){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
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