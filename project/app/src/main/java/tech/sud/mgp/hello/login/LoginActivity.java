package tech.sud.mgp.hello.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEt;
    private ConstraintLayout maleBut;
    private ConstraintLayout femaleBut;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private TextView goPlayBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        goPlayBut = findViewById(R.id.go_play_but);
        nameEt = findViewById(R.id.name_et);
        maleBut = findViewById(R.id.male_but);
        femaleBut = findViewById(R.id.female_but);
        maleCheck = findViewById(R.id.male_check);
        femaleCheck = findViewById(R.id.female_check);
        maleBut.setOnClickListener(this);
        femaleBut.setOnClickListener(this);
        goPlayBut.setOnClickListener(this);
        //默认性别选中男
        maleBut.callOnClick();
    }

    @Override
    public void onClick(View v) {
        if (v == goPlayBut) {
            String name = nameEt.getText().toString();
            if (!name.isEmpty()) {
                loginSuccess();
            } else {
                ToastUtils.showShort(getString(R.string.login_edit_name));
            }
        } else if (v == maleBut) {
            maleBut.setSelected(true);
            maleCheck.setVisibility(View.VISIBLE);
            femaleBut.setSelected(false);
            femaleCheck.setVisibility(View.GONE);
        } else if (v == femaleBut) {
            maleBut.setSelected(false);
            maleCheck.setVisibility(View.GONE);
            femaleBut.setSelected(true);
            femaleCheck.setVisibility(View.VISIBLE);
        }
    }

    private void loginSuccess() {
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_ID_KEY, 1234L);
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_SEX_KEY, "male");
        AppSharedPreferences.getSP().put(AppSharedPreferences.USER_NAME_KEY, "TestName");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}