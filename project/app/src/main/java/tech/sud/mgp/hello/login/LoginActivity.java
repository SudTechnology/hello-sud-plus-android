package tech.sud.mgp.hello.login;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Random;

import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RetCode;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.agreement.UserAgreementActivity;
import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.login.listener.DialogSecondaryListener;
import tech.sud.mgp.hello.login.listener.DialogSelectListener;
import tech.sud.mgp.hello.login.dialog.UserAgreementDialog;
import tech.sud.mgp.hello.login.dialog.UserSecondaryDialog;
import tech.sud.mgp.hello.login.http.repository.LoginRepository;
import tech.sud.mgp.hello.login.http.resp.LoginResponse;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class LoginActivity extends BaseActivity implements View.OnClickListener, DialogSelectListener, DialogSecondaryListener {

    private TextView nameTv;
    private ConstraintLayout maleBtn;
    private ConstraintLayout femaleBtn;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private TextView goPlayBtn;
    private ImageView randomIv;
    private String[] strings;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        goPlayBtn = findViewById(R.id.go_play_btn);
        nameTv = findViewById(R.id.name_tv);
        maleBtn = findViewById(R.id.male_btn);
        femaleBtn = findViewById(R.id.female_btn);
        maleCheck = findViewById(R.id.male_check);
        femaleCheck = findViewById(R.id.female_check);
        randomIv = findViewById(R.id.random_iv);
        strings = getResources().getStringArray(R.array.names_list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        maleBtn.setOnClickListener(this);
        femaleBtn.setOnClickListener(this);
        goPlayBtn.setOnClickListener(this);
        randomIv.setOnClickListener(this);
        nameTv.setText(randomName());
        //默认性别选中男
        maleBtn.callOnClick();
        //是否同意隐私政策
        boolean isAgree = AppSharedPreferences.getSP().getBoolean(AppSharedPreferences.AGREEMENT_STATE, false);
        if (!isAgree) {
            showAgreementDialog();
        }
    }

    private void showAgreementDialog() {
        UserAgreementDialog agreementDialog = new UserAgreementDialog();
        agreementDialog.setDialogSelectListener(this);
        agreementDialog.show(getSupportFragmentManager(), "argeement");
    }

    private void showSecondaryDialog() {
        UserSecondaryDialog secondaryDialog = new UserSecondaryDialog();
        secondaryDialog.setDialogSecondaryListener(this);
        secondaryDialog.show(getSupportFragmentManager(), "secondary");
    }

    @Override
    public void onSecondaryResult(boolean isAgree) {
        if (!isAgree) {
            finish();
        } else {
            AppSharedPreferences.getSP().put(AppSharedPreferences.AGREEMENT_STATE, true);
        }
    }

    @Override
    public void onAgreementType(int type) {
        Intent intent = new Intent(this, UserAgreementActivity.class);
        switch (type) {
            case 1: {
                intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 0);
                break;
            }
            case 2: {
                intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 1);
                break;
            }
        }
        startActivity(intent);
    }

    @Override
    public void onSelectResult(boolean isAgree) {
        if (!isAgree) {
            showSecondaryDialog();
        } else {
            AppSharedPreferences.getSP().put(AppSharedPreferences.AGREEMENT_STATE, true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == goPlayBtn) {
            LoginRepository.login(null, nameTv.getText().toString(), this, new RxCallback<LoginResponse>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
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
                        loginSuccess();
                    } else {
                        ToastUtils.showShort(t.getRetMsg() + t.getRetCode());
                    }
                }
            });
        } else if (v == maleBtn) {
            maleBtn.setSelected(true);
            maleCheck.setVisibility(View.VISIBLE);
            femaleBtn.setSelected(false);
            femaleCheck.setVisibility(View.GONE);
        } else if (v == femaleBtn) {
            maleBtn.setSelected(false);
            maleCheck.setVisibility(View.GONE);
            femaleBtn.setSelected(true);
            femaleCheck.setVisibility(View.VISIBLE);
        } else if (v == randomIv) {
            nameTv.setText(randomName());
        }
    }

    private String randomName() {
        int index = new Random().nextInt(strings.length);
        return strings[index];
    }

    private void loginSuccess() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}