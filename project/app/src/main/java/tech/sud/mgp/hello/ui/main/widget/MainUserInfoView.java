package tech.sud.mgp.hello.ui.main.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ImageLoader;

/**
 * 首页顶部的个人信息弹窗
 */
public class MainUserInfoView extends ConstraintLayout {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvUserId;
    private TextView tvWalletAddress;
    private View viewUnbind;
    private String walletAddress;

    public MainUserInfoView(@NonNull Context context) {
        this(context, null);
    }

    public MainUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainUserInfoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        updateUserInfo();
        setListeners();
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_main_user_info, this);
        ivIcon = findViewById(R.id.user_info_iv_icon);
        tvName = findViewById(R.id.user_info_tv_name);
        tvUserId = findViewById(R.id.tv_user_id);
        tvWalletAddress = findViewById(R.id.tv_wallet_address);
        viewUnbind = findViewById(R.id.view_unbind);
    }

    public void updateUserInfo() {
        tvName.setText(HSUserInfo.nickName);
        tvUserId.setText(getContext().getString(R.string.setting_userid, HSUserInfo.userId + ""));
        try {
            ImageLoader.loadAvatar(ivIcon, HSUserInfo.getUseAvatar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String walletAddress = HSUserInfo.walletAddress;
        if (TextUtils.isEmpty(walletAddress)) {
            showUserId();
        } else {
            showWalletAddress(walletAddress);
        }
    }

    public void setAvatarOnClickListener(OnClickListener listener) {
        ivIcon.setOnClickListener(listener);
    }

    private void setListeners() {
        tvWalletAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence address = tvWalletAddress.getText();
                if (address == null) {
                    return;
                }
                ClipboardUtils.copyText(address);
                ToastUtils.showShort(R.string.copy_success);
            }
        });
    }

    public void setUnbindOnClickListener(OnClickListener listener) {
        viewUnbind.setOnClickListener(listener);
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    /** 展示钱包信息 */
    private void showWalletAddress(String address) {
        this.walletAddress = address;
        tvWalletAddress.setVisibility(View.VISIBLE);
        tvUserId.setVisibility(View.GONE);
        tvWalletAddress.setText(address);
    }

    /** 展示用户id */
    private void showUserId() {
        tvWalletAddress.setVisibility(View.GONE);
        tvUserId.setVisibility(View.VISIBLE);
    }

    public void setShowUnbind(boolean showUnbind) {
        viewUnbind.setVisibility(showUnbind ? View.VISIBLE : View.GONE);
    }

}
