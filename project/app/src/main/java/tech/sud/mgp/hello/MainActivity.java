package tech.sud.mgp.hello;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
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
        String userId = AppSharedPreferences.getSP().getString(AppSharedPreferences.USER_ID_KEY,"");
        if (userId.isEmpty()){
            startActivity(new Intent(this, LoginActivity.class));
        }else {
            startActivity(new Intent(this, MainActivity.class));
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