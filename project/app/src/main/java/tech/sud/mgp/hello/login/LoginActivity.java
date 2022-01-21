package tech.sud.mgp.hello.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.login.callback.DialogSecondaryCallbck;
import tech.sud.mgp.hello.login.callback.DialogSelectCallbck;
import tech.sud.mgp.hello.login.dialog.UserAgreementDialog;
import tech.sud.mgp.hello.login.dialog.UserSecondaryDialog;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DialogSelectCallbck, DialogSecondaryCallbck {

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
        //是否同意隐私政策
        boolean isAgree = AppSharedPreferences.getSP().getBoolean(AppSharedPreferences.AGREEMENT_STATE, false);
        if (!isAgree) {
            showAgreementDialog();
        }
    }

    private void showAgreementDialog() {
        UserAgreementDialog agreementDialog = new UserAgreementDialog();
        agreementDialog.setDialogSelectCallbck(this);
        agreementDialog.show(getSupportFragmentManager(), "argeement");
    }

    private void showSecondaryDialog() {
        UserSecondaryDialog secondaryDialog = new UserSecondaryDialog();
        secondaryDialog.setDialogSecondaryCallbck(this);
        secondaryDialog.show(getSupportFragmentManager(), "secondary");
    }

    @Override
    public void secondaryResult(boolean isAgree) {
        if (!isAgree) {
            finish();
        } else {
            AppSharedPreferences.getSP().put(AppSharedPreferences.AGREEMENT_STATE, true);
        }
    }

    @Override
    public void agreementType(int type) {
        switch (type) {
            case 1: {
                LogUtils.i("agreementType 1");
            }
            case 2: {
                LogUtils.i("agreementType 2");
            }
        }
    }

    @Override
    public void selectResult(boolean isAgree) {
        if (!isAgree) {
            showSecondaryDialog();
        } else {
            AppSharedPreferences.getSP().put(AppSharedPreferences.AGREEMENT_STATE, true);
        }
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