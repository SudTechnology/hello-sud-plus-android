package tech.sud.mgp.hello.login;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Random;

import tech.sud.mgp.common.base.BaseActivity;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RetCode;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.home.HomeActivity;
import tech.sud.mgp.hello.login.callback.DialogSecondaryCallbck;
import tech.sud.mgp.hello.login.callback.DialogSelectCallbck;
import tech.sud.mgp.hello.login.dialog.UserAgreementDialog;
import tech.sud.mgp.hello.login.dialog.UserSecondaryDialog;
import tech.sud.mgp.hello.login.http.repository.LoginRepository;
import tech.sud.mgp.hello.login.http.resp.LoginResponse;
import tech.sud.mgp.hello.utils.AppSharedPreferences;

public class LoginActivity extends BaseActivity implements View.OnClickListener, DialogSelectCallbck, DialogSecondaryCallbck {

    private TextView nameTv;
    private ConstraintLayout maleBut;
    private ConstraintLayout femaleBut;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private TextView goPlayBut;
    private ImageView randomIv;
    private String[] strings;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        goPlayBut = findViewById(R.id.go_play_but);
        nameTv = findViewById(R.id.name_tv);
        maleBut = findViewById(R.id.male_but);
        femaleBut = findViewById(R.id.female_but);
        maleCheck = findViewById(R.id.male_check);
        femaleCheck = findViewById(R.id.female_check);
        randomIv = findViewById(R.id.random_iv);
        strings = getResources().getStringArray(R.array.names_list);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        maleBut.setOnClickListener(this);
        femaleBut.setOnClickListener(this);
        goPlayBut.setOnClickListener(this);
        randomIv.setOnClickListener(this);
        nameTv.setText(randomName());
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
                break;
            }
            case 2: {
                LogUtils.i("agreementType 2");
                break;
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
            LoginRepository.login(null ,nameTv.getText().toString(), this, new RxCallback<LoginResponse>() {
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