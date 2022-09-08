package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.NftTokenInvalidEvent;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.common.widget.dialog.SimpleChooseDialog;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.home.view.CoinDialog;
import tech.sud.mgp.hello.ui.main.nft.activity.InternalWalletBindActivity;
import tech.sud.mgp.hello.ui.main.nft.activity.NftListActivity;
import tech.sud.mgp.hello.ui.main.nft.listener.OnSelectedWalletListener;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.main.nft.widget.WalletInfoView;
import tech.sud.mgp.hello.ui.main.nft.widget.WalletListView;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.ChangeInternalAccountDialog;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.InternalWalletListDialog;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.NftBindingDialog;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.NftChainDialog;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.OverseasWalletListDialog;
import tech.sud.mgp.hello.ui.main.settings.activity.AboutActivity;
import tech.sud.mgp.hello.ui.main.settings.activity.SettingsActivity;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

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

        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));
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
        setClickListeners();
        setNftListeners();
    }

    private void setNftListeners() {
        viewModel.initDataShowWalletListLiveData.observe(this, model -> {
            LogUtils.d("nft:showWallet");
        });
        viewModel.initDataShowNftListLiveData.observe(this, model -> {
            walletInfoView.setDatas(model);
            BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
            if (bindWalletInfo != null) {
                setChainInfo(bindWalletInfo);
            }
        });
        viewModel.bindWalletInfoMutableLiveData.observe(this, this::showWalletInfo);
        LiveEventBus.<NftTokenInvalidEvent>get(LiveEventBusKey.KEY_NFT_TOKEN_INVALID).observe(this, new Observer<NftTokenInvalidEvent>() {
            @Override
            public void onChanged(NftTokenInvalidEvent nftTokenInvalidEvent) {
                viewModel.initData(requireContext());
            }
        });
    }

    private void showWalletInfo(BindWalletInfoModel model) {
        if (model == null) {
            walletListView.setVisibility(View.VISIBLE);
            walletInfoView.setVisibility(View.GONE);
            walletInfoView.setDatas(null);
        } else {
            walletListView.setVisibility(View.GONE);
            walletInfoView.setVisibility(View.VISIBLE);
            setChainInfo(model);
        }
        walletInfoView.setBindWallet(model);
        userInfoView.updateUserInfo();

        if (model == null) {
            userInfoView.setShowUnbind(false);
        } else {
            userInfoView.setShowUnbind(true);
            if (model.getZoneType() == ZoneType.INTERNAL) {
                userInfoView.setUnbindDrawable(R.drawable.ic_cn_nft_unbind);
            } else {
                userInfoView.setUnbindDrawable(R.drawable.ic_unbind);
            }
        }
    }

    private void setChainInfo(BindWalletInfoModel model) {
        viewModel.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel resp) {
                List<SudNFTGetWalletListModel.WalletInfo> walletList;
                if (resp == null) {
                    walletList = null;
                } else {
                    walletList = resp.walletList;
                }
                walletInfoView.setChainInfo(model, walletList);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.conver(retCode, retMsg));
            }
        });
    }

    private void setClickListeners() {
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        walletInfoView.setChainOnClickListener(v -> onClickChain());
        userInfoView.setUnbindOnClickListener(v -> {
            onClickUnbindWallet();
        });
        userInfoView.setAvatarOnClickListener(v -> {
            new CoinDialog().show(getChildFragmentManager(), null);
        });
        walletInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNftList();
            }
        });
        walletListView.setOverseasOnClickListener(v -> {
            showOverseasWalletListDialog();
        });
        walletListView.setInternalOnClickListener(v -> {
            showInternalWalletListDialog();
        });
    }

    private void showOverseasWalletListDialog() {
        OverseasWalletListDialog dialog = new OverseasWalletListDialog();
        dialog.setOnSelectedWalletListener(new OnSelectedWalletListener() {
            @Override
            public void onSelected(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                walletOnClick(walletInfo);
                dialog.dismiss();
            }
        });
        dialog.show(getChildFragmentManager(), null);
    }

    // 展示国内钱包列表弹窗
    private void showInternalWalletListDialog() {
        InternalWalletListDialog dialog = new InternalWalletListDialog();
        dialog.setOperateListener(new InternalWalletListDialog.OperateListener() {
            @Override
            public void onBind(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                dialog.dismiss();
                InternalWalletBindActivity.start(requireContext(), walletInfo);
            }

            @Override
            public void onUnbindCompleted(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                LifecycleUtils.safeLifecycle(SettingsFragment.this, () -> {
                    viewModel.initData(requireContext());
                });
            }
        });
        dialog.show(getChildFragmentManager(), null);
    }

    // 打开nft列表页面
    private void startNftList() {
        startActivity(new Intent(requireContext(), NftListActivity.class));
    }

    private void onClickUnbindWallet() {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo == null) {
            return;
        }
        if (bindWalletInfo.getZoneType() == ZoneType.OVERSEAS) {
            SimpleChooseDialog dialog = new SimpleChooseDialog(requireContext(), getString(R.string.unbind_wallet_title));
            dialog.setOnChooseListener(index -> {
                if (index == 1) {
                    viewModel.unbindWallet();
                }
                dialog.dismiss();
            });
            dialog.show();
        } else if (bindWalletInfo.getZoneType() == ZoneType.INTERNAL) {
            showInternalWalletListDialog();
        }
    }

    // 点击了nft链
    private void onClickChain() {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo == null) {
            return;
        }
        if (bindWalletInfo.getZoneType() == ZoneType.OVERSEAS) {
            NftChainDialog dialog = NftChainDialog.newInstance(bindWalletInfo.getChainInfo(), bindWalletInfo.getChainInfoList());
            dialog.setOnSelectedListener(viewModel::changeChain);
            dialog.show(getChildFragmentManager(), null);
        } else if (bindWalletInfo.getZoneType() == ZoneType.INTERNAL) {
            ChangeInternalAccountDialog dialog = new ChangeInternalAccountDialog();
            dialog.setChangeAccountListener(new ChangeInternalAccountDialog.ChangeAccountListener() {
                @Override
                public void onChange(WalletInfoModel model) {
                    viewModel.initData(requireContext());
                }
            });
            dialog.show(getChildFragmentManager(), null);
        }
    }

    // 点击了钱包
    private void walletOnClick(SudNFTGetWalletListModel.WalletInfo walletInfo) {
        NftBindingDialog dialog = new NftBindingDialog();
        dialog.viewModel = viewModel;
        dialog.walletInfo = walletInfo;
        dialog.show(getChildFragmentManager(), null);
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