package tech.sud.mgp.hello.ui.main.base.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.main.home.view.CoinDialog;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.NftDetailDialog;

/**
 * 首页顶部的个人信息弹窗
 */
public class MainUserInfoView extends ConstraintLayout {

    private ImageView ivIcon;
    private ImageView ivNftIcon;
    private TextView tvName;
    private TextView tvUserId;
    private View viewWalletAddress;
    private TextView tvWalletAddress;
    private View viewUnbind;

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
        ivNftIcon = findViewById(R.id.iv_nft_icon);
        tvName = findViewById(R.id.user_info_tv_name);
        tvUserId = findViewById(R.id.tv_user_id);
        viewWalletAddress = findViewById(R.id.container_wallet_address);
        tvWalletAddress = findViewById(R.id.tv_wallet_address);
        viewUnbind = findViewById(R.id.view_unbind);
    }

    public void updateUserInfo() {
        tvName.setText(HSUserInfo.nickName);
        tvUserId.setText(getContext().getString(R.string.setting_userid, HSUserInfo.userId + ""));
        try {
            ImageLoader.loadAvatar(getShowIvIcon(), HSUserInfo.getUseAvatar());
        } catch (Exception e) {
            e.printStackTrace();
        }
        BindWalletInfoModel bindWalletInfoModel = NFTViewModel.sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            showUserId();
        } else {
            WalletInfoModel walletInfoModel = bindWalletInfoModel.getWalletInfoModel(bindWalletInfoModel.walletType);
            if (walletInfoModel == null || walletInfoModel.zoneType == ZoneType.INTERNAL
                    || TextUtils.isEmpty(walletInfoModel.walletAddress)) {
                showUserId();
            } else {
                showWalletAddress(walletInfoModel.walletAddress);
            }
        }
    }

    private ImageView getShowIvIcon() {
        if (HSUserInfo.headerType == 1) {
            ivNftIcon.setVisibility(View.VISIBLE);
            ivIcon.setVisibility(View.INVISIBLE);
            return ivNftIcon;
        } else {
            ivNftIcon.setVisibility(View.INVISIBLE);
            ivIcon.setVisibility(View.VISIBLE);
            return ivIcon;
        }
    }

    private void setListeners() {
        viewWalletAddress.setOnClickListener(new OnClickListener() {
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
        ivIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    new CoinDialog().show(fragmentManager, null);
                }
            }
        });
        ivNftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                BindWalletInfoModel bindWalletInfoModel = NFTViewModel.sBindWalletInfo;
                if (fragmentManager != null && bindWalletInfoModel != null) {
                    NftModel wearNft = bindWalletInfoModel.getWearNft();
                    if (wearNft != null) {
                        NftDetailDialog dialog = NftDetailDialog.newInstance(wearNft);
                        dialog.show(fragmentManager, null);
                    }
                }
            }
        });
    }

    private FragmentManager getFragmentManager() {
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity instanceof FragmentActivity) {
            return ((FragmentActivity) topActivity).getSupportFragmentManager();
        }
        return null;
    }

    public void setWalletAddressOnClickListener(OnClickListener listener) {
        viewWalletAddress.setOnClickListener(listener);
    }

    public void setUnbindOnClickListener(OnClickListener listener) {
        viewUnbind.setOnClickListener(listener);
    }

    /** 展示钱包信息 */
    private void showWalletAddress(String address) {
        viewWalletAddress.setVisibility(View.VISIBLE);
        tvUserId.setVisibility(View.GONE);
        tvWalletAddress.setText(address);
    }

    /** 展示用户id */
    private void showUserId() {
        viewWalletAddress.setVisibility(View.GONE);
        tvUserId.setVisibility(View.VISIBLE);
    }

    public void setShowUnbind(boolean showUnbind) {
        if (showUnbind) {
            viewUnbind.setVisibility(View.VISIBLE);
        } else {
            viewUnbind.setVisibility(View.GONE);
        }
    }

    public void setUnbindDrawable(@DrawableRes int resId) {
        viewUnbind.setBackgroundResource(resId);
    }

}
