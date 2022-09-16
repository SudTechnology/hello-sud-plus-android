package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

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
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.nft.activity.InternalWalletBindActivity;
import tech.sud.mgp.hello.ui.main.nft.activity.NftListActivity;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.main.nft.widget.NftGuideView;
import tech.sud.mgp.hello.ui.main.nft.widget.WalletInfoView;
import tech.sud.mgp.hello.ui.main.nft.widget.WalletListView;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.ChangeInternalAccountDialog;
import tech.sud.mgp.hello.ui.main.nft.widget.dialog.ChangeOverseasWalletDialog;
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
    private NftGuideView guideViewChangeAddress;
    private NftGuideView guideViewChangeNetwork;
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
        guideViewChangeAddress = findViewById(R.id.guide_view_change_address);
        guideViewChangeNetwork = findViewById(R.id.guide_view_change_network);

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

    private void showChangeOverseasWalletDialog() {
        ChangeOverseasWalletDialog dialog = new ChangeOverseasWalletDialog();
        dialog.setChangeWalletListener(new ChangeOverseasWalletDialog.ChangeWalletListener() {
            @Override
            public void onChange(WalletInfoModel model) {
                viewModel.initData(requireContext());
            }
        });
        dialog.show(getChildFragmentManager(), null);
    }

    private void showWalletInfo(BindWalletInfoModel model) {
        if (model == null) {
            walletListView.setVisibility(View.VISIBLE);
            walletInfoView.setVisibility(View.GONE);
            walletInfoView.setDatas(null);
            hideChangeNetworkGuide();
        } else {
            walletListView.setVisibility(View.GONE);
            walletInfoView.setVisibility(View.VISIBLE);
            WalletInfoModel firstWalletInfoModel = model.getFirstWalletInfoModel();
            if (firstWalletInfoModel != null && firstWalletInfoModel.zoneType == ZoneType.OVERSEAS) {
                showChangeNetworkGuide();
            }
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
                userInfoView.setUnbindDrawable(R.drawable.ic_change_overseas_wallet);
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

    private void showChangeAddressGuide() {
        boolean shown = GlobalSP.getSP().getBoolean(GlobalSP.KEY_SHOWN_CHANGE_ADDRESS_GUIDE);
        if (shown) {
            return;
        }
        guideViewChangeAddress.setVisibility(View.VISIBLE);
        View viewWalletAddressArrow = userInfoView.getViewWalletAddressArrow();
        guideViewChangeAddress.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                viewWalletAddressArrow.getLocationInWindow(location);
                ViewGroup.LayoutParams params = guideViewChangeAddress.getLayoutParams();
                if (params instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) params;
                    marginLayoutParams.leftMargin = location[0] + viewWalletAddressArrow.getMeasuredWidth() / 2 - guideViewChangeAddress.getMeasuredWidth() / 2;
                    marginLayoutParams.topMargin = location[1] - guideViewChangeAddress.getMeasuredHeight();
                    guideViewChangeAddress.setLayoutParams(params);
                }
            }
        });
    }

    private void showChangeNetworkGuide() {
        boolean shown = GlobalSP.getSP().getBoolean(GlobalSP.KEY_SHOWN_CHANGE_NETWORK_GUIDE);
        if (shown) {
            return;
        }
        guideViewChangeNetwork.setVisibility(View.VISIBLE);
    }

    private void hideChangeNetworkGuide() {
        guideViewChangeNetwork.setVisibility(View.INVISIBLE);
    }

    private void setClickListeners() {
        btnSettings.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        walletInfoView.setChainOnClickListener(v -> onClickChain());
        userInfoView.setUnbindOnClickListener(v -> {
            onClickUnbindWallet();
        });
        userInfoView.setOnShowContentListener(new MainUserInfoView.OnShowContentListener() {
            @Override
            public void onShowUserId() {
                hideChangeAddressGuide();
            }

            @Override
            public void onShowWalletAddress() {
                showChangeAddressGuide();
            }
        });
        userInfoView.setWalletAddressOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChangeAddressGuide();
                GlobalSP.getSP().put(GlobalSP.KEY_SHOWN_CHANGE_ADDRESS_GUIDE, true);
                showChangeOverseasWalletDialog();
            }
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

    private void hideChangeAddressGuide() {
        guideViewChangeAddress.setVisibility(View.INVISIBLE);
    }

    private void showOverseasWalletListDialog() {
        OverseasWalletListDialog dialog = new OverseasWalletListDialog();
        dialog.setOperateListener(new OverseasWalletListDialog.OperateListener() {
            @Override
            public void onBind(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                dialog.dismiss();
                walletOnClick(walletInfo);
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
            showOverseasWalletListDialog();
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
            hideChangeNetworkGuide();
            GlobalSP.getSP().put(GlobalSP.KEY_SHOWN_CHANGE_NETWORK_GUIDE, true);

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
        dialog.setOnBindSuccessListener(new NftBindingDialog.onBindSuccessListener() {
            @Override
            public void onBindSuccess() {
                LifecycleUtils.safeLifecycle(SettingsFragment.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        viewModel.initData(requireContext());
                    }
                });
            }
        });
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