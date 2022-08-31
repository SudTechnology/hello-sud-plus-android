package tech.sud.mgp.hello.ui.main.nft.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.Serializable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.CustomCountdownTimer;
import tech.sud.mgp.hello.common.utils.HSTextUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.ui.common.listener.SimpleTextWatcher;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;
import tech.sud.mgp.hello.ui.main.base.utils.RouterUtils;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.listener.ISudNFTListenerSendSmsCode;
import tech.sud.nft.core.listener.ISudNFTListenerSmsCodeBindWallet;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;
import tech.sud.nft.core.model.resp.SudNFTSmsCodeBindWalletModel;

/**
 * 国内钱包绑定页面
 */
public class InternalWalletBindActivity extends BaseActivity {

    private HSTopBar topBar;
    private ImageView ivWalletIcon;
    private TextView tvTips1;
    private EditText etPhone;
    private View viewClearPhone;
    private TextView tvSmsCode;
    private EditText etCode;
    private TextView tvTips2;
    private TextView tvConfirm;
    private TextView tvAgreement;
    private View viewContainer;

    private SudNFTGetWalletListModel.WalletInfo walletInfo;
    private static long sendSmsCodeTimestamp;
    private int countdownTotalCount = 60;
    private CustomCountdownTimer sendSmsCodeCountdownTimer;
    private NFTViewModel viewModel = new NFTViewModel();

    public static void start(Context context, SudNFTGetWalletListModel.WalletInfo walletInfo) {
        Intent intent = new Intent(context, InternalWalletBindActivity.class);
        intent.putExtra("WalletInfo", walletInfo);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        Serializable walletInfoSeri = getIntent().getSerializableExtra("WalletInfo");
        if (walletInfoSeri instanceof SudNFTGetWalletListModel.WalletInfo) {
            walletInfo = (SudNFTGetWalletListModel.WalletInfo) walletInfoSeri;
        } else {
            return true;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_internal_wallet;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topBar = findViewById(R.id.top_bar);
        ivWalletIcon = findViewById(R.id.iv_wallet_icon);
        tvTips1 = findViewById(R.id.tv_tips_1);
        etPhone = findViewById(R.id.et_phone);
        viewClearPhone = findViewById(R.id.view_clear_phone);
        tvSmsCode = findViewById(R.id.tv_sms_code);
        etCode = findViewById(R.id.et_code);
        tvTips2 = findViewById(R.id.tv_tips_2);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvAgreement = findViewById(R.id.tv_agreement);
        viewContainer = findViewById(R.id.view_container);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.initData(this);

        topBar.setTitle(getString(R.string.bind_phone_title, walletInfo.name));

        ImageLoader.loadImage(ivWalletIcon, walletInfo.icon);
        tvTips1.setText(getString(R.string.auth_wallet_account, walletInfo.name));
        tvTips2.setText(getString(R.string.bind_phone_info, walletInfo.name));

        updateSendSmsCodeStyle();
        initAgreement();
    }

    private void initAgreement() {
        if (TextUtils.isEmpty(walletInfo.servicePolicyUrl) && TextUtils.isEmpty(walletInfo.privacyPolicyUrl)) {
            return;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.agreement_prefix));
        builder.append(" ");
        if (!TextUtils.isEmpty(walletInfo.servicePolicyUrl)) {
            String title = getString(R.string.user_agreement_info, walletInfo.name);
            builder.append(title, new ClickableSpan() {

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#147af9"));
                    ds.setFakeBoldText(true);
                    ds.setUnderlineText(false);
                    ds.clearShadowLayer();
                }

                @Override
                public void onClick(@NonNull View widget) {
                    startUrl(title, walletInfo.servicePolicyUrl);
                }
            }, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("、");
        }
        if (!TextUtils.isEmpty(walletInfo.privacyPolicyUrl)) {
            String title = getString(R.string.privacy_policy_info, walletInfo.name);
            builder.append(title, new ClickableSpan() {

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#147af9"));
                    ds.setFakeBoldText(true);
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(@NonNull View widget) {
                    startUrl(title, walletInfo.privacyPolicyUrl);
                }
            }, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvAgreement.setText(builder);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void startUrl(String title, String url) {
        RouterUtils.openUrl(this, title, url);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        etPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPhone.length() > 0) {
                    viewClearPhone.setVisibility(View.VISIBLE);
                } else {
                    viewClearPhone.setVisibility(View.GONE);
                }
            }
        });
        tvSmsCode.setOnClickListener(v -> {
            sendSmsCode();
        });
        tvConfirm.setOnClickListener(v -> {
            onClickConfirm();
        });
        KeyboardUtils.registerSoftInputChangedListener(this, new KeyboardUtils.OnSoftInputChangedListener() {
            @Override
            public void onSoftInputChanged(int height) {
                viewContainer.setPadding(0, 0, 0, height);
            }
        });
    }

    private void onClickConfirm() {
        String phone = HSTextUtils.getText(etPhone);
        String smsCode = HSTextUtils.getText(etCode);
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(smsCode)) {
            return;
        }
        tvConfirm.setEnabled(false);
        viewModel.bindCNWallet(walletInfo, HSUserInfo.userId + "", phone, smsCode, new ISudNFTListenerSmsCodeBindWallet() {
            @Override
            public void onSuccess(SudNFTSmsCodeBindWalletModel sudNFTSmsCodeBindWalletModel) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    ToastUtils.showLong(R.string.auth_success);
                    tvConfirm.setEnabled(true);
                    finish();
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    tvConfirm.setEnabled(true);
                    if (code == 1030) {
                        ToastUtils.showLong(R.string.auth_code_fail);
                    } else {
                        ToastUtils.showLong(ResponseUtils.conver(code, msg));
                    }
                });
            }
        });
    }

    private void sendSmsCode() {
        String phone = HSTextUtils.getText(etPhone);
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        etCode.requestFocus();
        tvSmsCode.setEnabled(false);
        viewModel.sendSmsCode(walletInfo.type, phone, new ISudNFTListenerSendSmsCode() {
            @Override
            public void onSuccess() {
                LifecycleUtils.safeLifecycle(context, () -> sendSmsCodeSuccess());
            }

            @Override
            public void onFailure(int code, String msg) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    tvSmsCode.setEnabled(true);
                    ToastUtils.showLong(ResponseUtils.conver(code, msg));
                });
            }
        });
    }

    private void sendSmsCodeSuccess() {
        tvSmsCode.setEnabled(false);
        sendSmsCodeTimestamp = System.currentTimeMillis();
        startSendSmsCodeCountdown(countdownTotalCount);
    }

    private void startSendSmsCodeCountdown(int countdown) {
        releaseSendSmsCodeCountdown();
        sendSmsCodeCountdownTimer = new CustomCountdownTimer(countdown) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onTick(int count) {
                tvSmsCode.setText(count + "s");
            }

            @Override
            protected void onFinish() {
                sendSmsCodeTimestamp = 0;
                updateSendSmsCodeStyle();
            }
        };
        sendSmsCodeCountdownTimer.start();
    }

    private void updateSendSmsCodeStyle() {
        long diff = Math.abs(sendSmsCodeTimestamp - System.currentTimeMillis()) / 1000;
        if (diff >= countdownTotalCount) {
            tvSmsCode.setEnabled(true);
            tvSmsCode.setText(R.string.get_auth_code);
        } else {
            tvSmsCode.setEnabled(false);
            startSendSmsCodeCountdown((int) (diff / 1000));
        }
    }

    private void releaseSendSmsCodeCountdown() {
        if (sendSmsCodeCountdownTimer != null) {
            sendSmsCodeCountdownTimer.cancel();
            sendSmsCodeCountdownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseSendSmsCodeCountdown();
    }

}
