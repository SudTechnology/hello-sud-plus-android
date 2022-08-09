package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.ui.main.settings.activity.AboutActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.SettingsActivity;
import tech.sud.mgp.hello.ui.main.settings.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.settings.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.main.settings.widget.NftChainDialog;
import tech.sud.mgp.hello.ui.main.settings.widget.WalletInfoView;
import tech.sud.mgp.hello.ui.main.settings.widget.WalletListView;
import tech.sud.mgp.hello.ui.main.widget.MainUserInfoView;
import tech.sud.nft.core.model.SudNFTGetWalletListModel;

/**
 * 首页当中的设置页
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private WalletListView walletListView;
    private WalletInfoView walletInfoView;
    private SettingButton btnSettings;
    private SettingButton btnAbout;
    private final NFTViewModel viewModel = new NFTViewModel();
    private MainUserInfoView userInfoView;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        userInfoView = findViewById(R.id.user_info_view);
        walletListView = findViewById(R.id.wallet_list_view);
        walletInfoView = findViewById(R.id.wallet_info_view);
        btnSettings = findViewById(R.id.button_settings);
        btnAbout = findViewById(R.id.button_about);
        userInfoView.setShowUnbind(true);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.initData(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.initData(requireContext());
        userInfoView.updateUserInfo();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        walletListView.setWalletOnClickListener(this::walletOnClick);
        walletInfoView.setChainOnClickListener(v -> onClickChain());
        userInfoView.setUnbindOnClickListener(v -> {
            onClickUnbindWallet();
        });

        viewModel.initDataShowWalletListLiveData.observe(this, model -> {
            LogUtils.d("nft:showWallet");
            walletListView.setDatas(model.wallets);
        });
        viewModel.initDataShowNftListLiveData.observe(this, model -> {
            LogUtils.d("nft:showNftList");
            walletInfoView.setDatas(model);
            BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
            if (bindWalletInfo != null) {
                walletInfoView.setChainInfo(bindWalletInfo.chainInfo);
            }
        });
        viewModel.bindWalletInfoMutableLiveData.observe(this, model -> {
            if (model == null) {
                walletListView.setVisibility(View.VISIBLE);
                walletInfoView.setVisibility(View.GONE);
            } else {
                walletListView.setVisibility(View.GONE);
                walletInfoView.setVisibility(View.VISIBLE);
                walletInfoView.setChainInfo(model.chainInfo);
            }
            userInfoView.updateUserInfo();
            userInfoView.setShowUnbind(model != null);
        });
    }

    private void onClickUnbindWallet() {
        SimpleChooseDialog dialog = new SimpleChooseDialog(requireContext(), getString(R.string.unbind_wallet_title));
        dialog.setOnChooseListener(index -> {
            if (index == 1) {
                viewModel.unbindWallet();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    // 点击了nft链
    private void onClickChain() {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo == null) {
            return;
        }
        NftChainDialog dialog = NftChainDialog.newInstance(bindWalletInfo.chainInfo, bindWalletInfo.chainInfoList);
        dialog.setOnSelectedListener(viewModel::changeChain);
        dialog.show(getChildFragmentManager(), null);
    }

    // 点击了钱包
    private void walletOnClick(SudNFTGetWalletListModel.WalletInfo walletInfo) {
        viewModel.bindWallet(walletInfo);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSettings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        } else if (v == btnAbout) {
            startActivity(new Intent(getContext(), AboutActivity.class));
        }
    }

}