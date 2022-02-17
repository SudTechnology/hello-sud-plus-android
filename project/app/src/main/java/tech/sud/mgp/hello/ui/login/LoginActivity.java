package tech.sud.mgp.hello.ui.login;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Random;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.AppSharedPreferences;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;
import tech.sud.mgp.hello.ui.login.dialog.UserAgreementDialog;
import tech.sud.mgp.hello.ui.login.dialog.UserSecondaryDialog;
import tech.sud.mgp.hello.ui.login.listener.DialogSecondaryListener;
import tech.sud.mgp.hello.ui.login.listener.DialogSelectListener;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.UserAgreementActivity;
import tech.sud.mgp.hello.ui.viewmodel.ConfigViewModel;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, DialogSelectListener, DialogSecondaryListener {

    private TextView nameTv;
    private ConstraintLayout maleBtn;
    private ConstraintLayout femaleBtn;
    private ImageView maleCheck;
    private ImageView femaleCheck;
    private TextView goPlayBtn;
    private ImageView randomIv;
    private String[] names;
    private UserAgreementDialog agreementDialog;
    private final ConfigViewModel configViewModel = new ConfigViewModel();
    private boolean isLogin; // 标识是否正在登录中

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
        names = getResources().getStringArray(R.array.names_list);
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
        configViewModel.initConfigSuccessLiveData.observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                loginSuccess();
            }
        });
    }

    private void showAgreementDialog() {
        agreementDialog = new UserAgreementDialog();
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
        if (agreementDialog != null) {
            agreementDialog.dismiss();
        }
    }

    @Override
    public void onAgreementType(int type) {
        Intent intent = new Intent(this, UserAgreementActivity.class);
        switch (type) {
            case 0: {
                intent.putExtra(UserAgreementActivity.AGREEMENTTYPE, 0);
                break;
            }
            case 1: {
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
            clickLogin();
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

    private void clickLogin() {
        if (isLogin) {
            return;
        }
        isLogin = true;
        LoginRepository.login(null, nameTv.getText().toString(), this, new RxCallback<LoginResponse>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isLogin = false;
            }

            @Override
            public void onNext(BaseResponse<LoginResponse> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            AppSharedPreferences.getSP().put(AppSharedPreferences.USER_ID_KEY, t.getData().userId);
                            AppSharedPreferences.getSP().put(AppSharedPreferences.USER_HEAD_PORTRAIT_KEY, t.getData().avatar);
                            AppSharedPreferences.getSP().put(AppSharedPreferences.USER_NAME_KEY, t.getData().nickname);
                        }
                    }.start();
                    HSUserInfo.userId = t.getData().userId;
                    HSUserInfo.nickName = t.getData().nickname;
                    HSUserInfo.avatar = t.getData().avatar;
                    HSUserInfo.token = t.getData().token;
                    configViewModel.getBaseConfig(LoginActivity.this);
                } else {
                    isLogin = false;
                    ToastUtils.showLong(ResponseUtils.conver(t));
                }
            }
        });
    }

    private String randomName() {
        int index = new Random().nextInt(names.length);
        return names[index];
    }

    private void loginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}