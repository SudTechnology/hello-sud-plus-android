package tech.sud.mgp.hello.ui.login;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Random;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.model.UserInfoConverter;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;
import tech.sud.mgp.hello.ui.common.viewmodel.ConfigViewModel;
import tech.sud.mgp.hello.ui.login.dialog.UserAgreementDialog;
import tech.sud.mgp.hello.ui.login.dialog.UserSecondaryDialog;
import tech.sud.mgp.hello.ui.login.listener.DialogSecondaryListener;
import tech.sud.mgp.hello.ui.login.listener.DialogSelectListener;
import tech.sud.mgp.hello.ui.main.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.utils.RouterUtils;

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
        if (HSUserInfo.isLogin()) { // 已登录
            nameTv.setText(HSUserInfo.nickName);
        } else {
            nameTv.setText(randomName());
        }
        //默认性别选中男
        maleBtn.callOnClick();
        //是否同意隐私政策
        boolean isAgree = GlobalSP.getSP().getBoolean(GlobalSP.AGREEMENT_STATE, false);
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
            GlobalSP.getSP().put(GlobalSP.AGREEMENT_STATE, true);
        }
        if (agreementDialog != null) {
            agreementDialog.dismiss();
        }
    }

    @Override
    public void onAgreementType(int type) {
        switch (type) {
            case 0: {
                RouterUtils.openUrl(this, getString(R.string.user_agreement_title), APPConfig.USER_PROTOCAL_URL);
                break;
            }
            case 1: {
                RouterUtils.openUrl(this, getString(R.string.user_privacy_title), APPConfig.USER_PRIVACY_URL);
                break;
            }
        }
    }

    @Override
    public void onSelectResult(boolean isAgree) {
        if (!isAgree) {
            showSecondaryDialog();
        } else {
            GlobalSP.getSP().put(GlobalSP.AGREEMENT_STATE, true);
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
        Long userReq = null;
        if (HSUserInfo.isLogin()) {
            userReq = HSUserInfo.userId;
        }
        LoginRepository.login(userReq, nameTv.getText().toString(), this, new RxCallback<LoginResponse>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isLogin = false;
            }

            @Override
            public void onNext(BaseResponse<LoginResponse> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    ThreadUtils.getIoPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            LoginRepository.saveUserInfo();
                        }
                    });
                    UserInfoConverter.conver(t.getData());
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