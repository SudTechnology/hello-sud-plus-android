package tech.sud.mgp.hello.ui.main;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.NftTokenInvalidEvent;
import tech.sud.mgp.hello.common.listener.CompletedListener;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.nft.activity.InternalWalletBindActivity;
import tech.sud.mgp.hello.ui.nft.activity.NftListActivity;
import tech.sud.mgp.hello.ui.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.nft.widget.NftGuideView;
import tech.sud.mgp.hello.ui.nft.widget.WalletInfoView;
import tech.sud.mgp.hello.ui.nft.widget.WalletListView;
import tech.sud.mgp.hello.ui.nft.widget.dialog.ChangeInternalAccountDialog;
import tech.sud.mgp.hello.ui.nft.widget.dialog.ChangeOverseasWalletDialog;
import tech.sud.mgp.hello.ui.nft.widget.dialog.InternalWalletListDialog;
import tech.sud.mgp.hello.ui.nft.widget.dialog.NftBindingDialog;
import tech.sud.mgp.hello.ui.nft.widget.dialog.NftChainDialog;
import tech.sud.mgp.hello.ui.nft.widget.dialog.OverseasWalletListDialog;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private WalletListView walletListView;
    private WalletInfoView walletInfoView;
    private NftGuideView guideViewChangeAddress;
    private NftGuideView guideViewChangeNetwork;
    private final NFTViewModel nftViewModel = new NFTViewModel();
    private MainUserInfoView userInfoView;

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        userInfoView = findViewById(R.id.user_info_view);
        walletListView = findViewById(R.id.wallet_list_view);
        walletInfoView = findViewById(R.id.wallet_info_view);
        guideViewChangeAddress = findViewById(R.id.guide_view_change_address);
        guideViewChangeNetwork = findViewById(R.id.guide_view_change_network);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_gray);
        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(context));
    }

    @Override
    protected void initData() {
        super.initData();
        nftViewModel.initData(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        nftViewModel.initData(context);
        userInfoView.updateUserInfo();
        checkShowGuide();
    }

    private void checkShowGuide() {
        BindWalletInfoModel bindWalletInfoModel = NFTViewModel.sBindWalletInfo;
        // 先展示切换地址引导
        if (bindWalletInfoModel == null) {
            hideChangeNetworkGuide();
            hideChangeAddressGuide();
            return;
        }
        WalletInfoModel walletInfoModel = bindWalletInfoModel.getWalletInfoModel(bindWalletInfoModel.walletType);
        if (walletInfoModel == null || walletInfoModel.zoneType != ZoneType.OVERSEAS
                || TextUtils.isEmpty(walletInfoModel.walletAddress)) {
            hideChangeNetworkGuide();
            hideChangeAddressGuide();
            return;
        }
        showChangeAddressGuide();

        if (guideViewChangeAddress.getVisibility() == View.VISIBLE) {
            return;
        }

        // 切换地址引导不展示再判断是否要展示切换网络引导
        WalletInfoModel firstWalletInfoModel = bindWalletInfoModel.getFirstWalletInfoModel();
        if (firstWalletInfoModel != null && firstWalletInfoModel.zoneType == ZoneType.OVERSEAS) {
            showChangeNetworkGuide();
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        setClickListeners();
        setNftListeners();
    }

    private void setNftListeners() {
        nftViewModel.initDataShowWalletListLiveData.observe(this, model -> {
            LogUtils.d("nft:showWallet");
        });
        nftViewModel.initDataShowNftListLiveData.observe(this, model -> {
            walletInfoView.setDatas(model);
            BindWalletInfoModel bindWalletInfo = nftViewModel.getBindWalletInfo();
            if (bindWalletInfo != null) {
                setChainInfo(bindWalletInfo);
            }
        });
        nftViewModel.bindWalletInfoMutableLiveData.observe(this, this::showWalletInfo);
        LiveEventBus.<NftTokenInvalidEvent>get(LiveEventBusKey.KEY_NFT_TOKEN_INVALID).observe(this, new Observer<NftTokenInvalidEvent>() {
            @Override
            public void onChanged(NftTokenInvalidEvent nftTokenInvalidEvent) {
                nftViewModel.initData(context);
            }
        });
    }

    private void showChangeOverseasWalletDialog() {
        ChangeOverseasWalletDialog dialog = new ChangeOverseasWalletDialog();
        dialog.setChangeWalletListener(new ChangeOverseasWalletDialog.ChangeWalletListener() {
            @Override
            public void onChange(WalletInfoModel model) {
                nftViewModel.initData(context);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
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
        checkShowGuide();

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
        nftViewModel.getWalletList(new ISudNFTListenerGetWalletList() {
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

    /** 显示切换地址的引导 */
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
        walletInfoView.setChainOnClickListener(v -> onClickChain());
        userInfoView.setUnbindOnClickListener(v -> {
            onClickUnbindWallet();
        });
        userInfoView.setWalletAddressOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChangeAddressGuide();
                GlobalSP.getSP().put(GlobalSP.KEY_SHOWN_CHANGE_ADDRESS_GUIDE, true);
                showChangeOverseasWalletDialog();
                checkShowGuide();
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
                LifecycleUtils.safeLifecycle(MainActivity.this, () -> {
                    nftViewModel.initData(context);
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 展示国内钱包列表弹窗
    private void showInternalWalletListDialog() {
        InternalWalletListDialog dialog = new InternalWalletListDialog();
        dialog.setOperateListener(new InternalWalletListDialog.OperateListener() {
            @Override
            public void onBind(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                dialog.dismiss();
                InternalWalletBindActivity.start(context, walletInfo);
            }

            @Override
            public void onUnbindCompleted(SudNFTGetWalletListModel.WalletInfo walletInfo) {
                LifecycleUtils.safeLifecycle(MainActivity.this, () -> {
                    nftViewModel.initData(context);
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    // 打开nft列表页面
    private void startNftList() {
        startActivity(new Intent(context, NftListActivity.class));
    }

    private void onClickUnbindWallet() {
        BindWalletInfoModel bindWalletInfo = nftViewModel.getBindWalletInfo();
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
        BindWalletInfoModel bindWalletInfo = nftViewModel.getBindWalletInfo();
        if (bindWalletInfo == null) {
            return;
        }
        if (bindWalletInfo.getZoneType() == ZoneType.OVERSEAS) {
            hideChangeNetworkGuide();
            GlobalSP.getSP().put(GlobalSP.KEY_SHOWN_CHANGE_NETWORK_GUIDE, true);

            NftChainDialog dialog = NftChainDialog.newInstance(bindWalletInfo.getChainInfo(), bindWalletInfo.getChainInfoList());
            dialog.setOnSelectedListener(nftViewModel::changeChain);
            dialog.show(getSupportFragmentManager(), null);
        } else if (bindWalletInfo.getZoneType() == ZoneType.INTERNAL) {
            ChangeInternalAccountDialog dialog = new ChangeInternalAccountDialog();
            dialog.setChangeAccountListener(new ChangeInternalAccountDialog.ChangeAccountListener() {
                @Override
                public void onChange(WalletInfoModel model) {
                    nftViewModel.initData(context);
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    // 点击了钱包
    private void walletOnClick(SudNFTGetWalletListModel.WalletInfo walletInfo) {
        NftBindingDialog dialog = new NftBindingDialog();
        dialog.viewModel = nftViewModel;
        dialog.walletInfo = walletInfo;
        dialog.setOnBindSuccessListener(new NftBindingDialog.onBindSuccessListener() {
            @Override
            public void onBindSuccess() {
                LifecycleUtils.safeLifecycle(MainActivity.this, new CompletedListener() {
                    @Override
                    public void onCompleted() {
                        nftViewModel.initData(context);
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

}
