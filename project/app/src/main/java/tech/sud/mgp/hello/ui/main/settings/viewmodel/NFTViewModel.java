package tech.sud.mgp.hello.ui.main.settings.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.service.login.repository.LoginRepository;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.ui.main.settings.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.settings.model.NftListResultModel;
import tech.sud.mgp.hello.ui.main.settings.model.NftModel;
import tech.sud.nft.core.SudNFT;
import tech.sud.nft.core.listener.ISudNFTListenerBindWallet;
import tech.sud.nft.core.listener.ISudNFTListenerGenNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerGetMetadata;
import tech.sud.nft.core.listener.ISudNFTListenerGetNFTList;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.listener.ISudNFTListenerInitNFT;
import tech.sud.nft.core.model.SudInitNFTParamModel;
import tech.sud.nft.core.model.SudNFTBindWalletModel;
import tech.sud.nft.core.model.SudNFTBindWalletParamModel;
import tech.sud.nft.core.model.SudNFTCredentialsTokenParamModel;
import tech.sud.nft.core.model.SudNFTGenNFTCredentialsTokenModel;
import tech.sud.nft.core.model.SudNFTGetMetadataModel;
import tech.sud.nft.core.model.SudNFTGetNFTListModel;
import tech.sud.nft.core.model.SudNFTGetNFTListParamModel;
import tech.sud.nft.core.model.SudNFTGetNFTMetadataParamModel;
import tech.sud.nft.core.model.SudNFTGetWalletListModel;
import tech.sud.nft.core.model.SudNFTGetWalletListModel.ChainInfo;
import tech.sud.nft.core.model.SudNFTGetWalletListModel.WalletInfo;

/**
 * nft 业务
 */
public class NFTViewModel extends BaseViewModel {

    /** 初始化数据，展示钱包列表 */
    public MutableLiveData<SudNFTGetWalletListModel> initDataShowWalletListLiveData = new MutableLiveData<>();

    /** 初始化数据，展示nft列表 */
    public MutableLiveData<NftListResultModel> initDataShowNftListLiveData = new MutableLiveData<>();

    /** 返回绑定的钱包信息 */
    public MutableLiveData<BindWalletInfoModel> bindWalletInfoMutableLiveData = new MutableLiveData<>();

    /** 穿戴nft有变化 */
    public MutableLiveData<Object> wearNftChangeLiveData = new MutableLiveData<>();

    private SudNFTGetWalletListModel walletListModel; // 钱包列表
    private static BindWalletInfoModel mBindWalletInfo; // 已绑定的钱包数据
    private static boolean isInitCompleted; // 是否初始化完成

    /**
     * 初始化数据
     * 1，未绑定时显示钱包列表
     * 2，已绑定时显示nft列表
     */
    public void initData(Context context) {
        if (isInitCompleted) {
            initDataGetBindWallet();
        } else {
            initNFT(context, new ISudNFTListenerInitNFT() {
                @Override
                public void onSuccess() {
                    isInitCompleted = true;
                    initDataGetBindWallet();
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    isInitCompleted = false;
                    ToastUtils.showLong("initNFT onFailure:" + retCode + "  retMsg:" + retMsg);
                }
            });
        }
    }

    // 判断是否绑定了钱包
    private void initDataGetBindWallet() {
        mBindWalletInfo = getBindWalletInfoByCache();
        if (mBindWalletInfo == null) {
            // 未绑定钱包，显示钱包列表
            showWalletList();
        } else {
            // 绑定了钱包，显示NFT列表
            initNftList(mBindWalletInfo);
        }
        bindWalletInfoMutableLiveData.setValue(mBindWalletInfo);
    }

    // 展示钱包列表
    private void showWalletList() {
        getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                if (mBindWalletInfo != null) {
                    return;
                }
                initDataShowWalletListLiveData.setValue(sudNFTGetWalletListModel);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("getWalletList onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    /** 绑定钱包 */
    public void bindWallet(WalletInfo walletInfo) {
        if (walletInfo == null) {
            return;
        }
        SudNFTBindWalletParamModel paramModel = new SudNFTBindWalletParamModel();
        paramModel.walletType = walletInfo.type;
        SudNFT.bindWallet(paramModel, new ISudNFTListenerBindWallet() {
            @Override
            public void onSuccess(SudNFTBindWalletModel model) {
                onBindWalletSuccess(walletInfo, model);
                LogUtils.d("nft: bindWallet onSuccess:" + GsonUtils.toJson(model));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("nft: bindWallet onFailure:" + retCode + "  retMsg:" + retMsg);
                ToastUtils.showLong("bindWallet onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    // 绑定钱包成功
    private void onBindWalletSuccess(WalletInfo walletInfo, SudNFTBindWalletModel model) {
        if (model == null) {
            return;
        }
        BindWalletInfoModel bindWalletInfoModel = new BindWalletInfoModel();
        bindWalletInfoModel.walletType = walletInfo.type;
        bindWalletInfoModel.walletToken = model.walletToken;
        bindWalletInfoModel.walletAddress = model.walletAddress;
        bindWalletInfoModel.chainInfoList = walletInfo.chains;
        bindWalletInfoModel.chainInfo = getDefaultChainType(walletInfo);
        putBindWalletInfo(bindWalletInfoModel);
        mBindWalletInfo = bindWalletInfoModel;
        HSUserInfo.walletAddress = bindWalletInfoModel.walletAddress;
        LoginRepository.saveUserInfo();
        bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);

        // 获取NFT列表
        initNftList(bindWalletInfoModel);
    }

    // 初始化数据获取nft列表
    private void initNftList(BindWalletInfoModel bindWalletInfoModel) {
        SudNFTGetNFTListParamModel paramModel = new SudNFTGetNFTListParamModel();
        paramModel.walletToken = bindWalletInfoModel.walletToken;
        paramModel.chainType = bindWalletInfoModel.chainInfo.type;
        paramModel.walletAddress = bindWalletInfoModel.walletAddress;
        getNftList(paramModel, new GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                if (mBindWalletInfo == null) {
                    LogUtils.d("nft: initNftList bindWalletInfo empty");
                    return;
                }
                initDataShowNftListLiveData.setValue(model);
                LogUtils.d("nft: initNftList onSuccess:" + GsonUtils.toJson(model));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("initNftList onFailure:" + retCode + "  retMsg:" + retMsg);
                LogUtils.e("nft: initNftList onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    /** 获取nft列表 */
    public void getNftList(SudNFTGetNFTListParamModel model, GetNftListListener listener) {
        SudNFT.getNFTList(model, new ISudNFTListenerGetNFTList() {
            @Override
            public void onSuccess(SudNFTGetNFTListModel sudNFTGetNFTListModel) {
                NftListResultModel nftListResultModel = new NftListResultModel();
                if (sudNFTGetNFTListModel != null) {
                    nftListResultModel.totalCount = sudNFTGetNFTListModel.totalCount;
                    nftListResultModel.pageKey = sudNFTGetNFTListModel.pageKey;
                }
                if (sudNFTGetNFTListModel == null || sudNFTGetNFTListModel.ownedNfts == null || sudNFTGetNFTListModel.ownedNfts.size() == 0) {
                    if (listener != null) {
                        listener.onSuccess(nftListResultModel);
                    }
                } else {
                    getMetadataList(nftListResultModel, sudNFTGetNFTListModel, listener);
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("getNftList onFailure:" + retCode + "  retMsg:" + retMsg);
                if (listener != null) {
                    listener.onFailure(retCode, retMsg);
                }
            }
        });
    }

    // 获取nft列表元数据
    private void getMetadataList(NftListResultModel nftListResultModel, SudNFTGetNFTListModel sudNFTGetNFTListModel, GetNftListListener listener) {
        HashMap<Integer, Boolean> resultMap = new HashMap<>();
        List<NftModel> nftList = new ArrayList<>();
        for (int i = 0; i < sudNFTGetNFTListModel.ownedNfts.size(); i++) {
            // 使用自定义的数据封装
            NftModel nftModel = new NftModel();
            nftList.add(nftModel);

            int finalI = i;
            SudNFTGetNFTListModel.NFTInfo nftInfo = sudNFTGetNFTListModel.ownedNfts.get(i);
            nftModel.contractAddress = nftInfo.contractAddress;
            nftModel.tokenId = nftInfo.tokenId;

            resultMap.put(i, false);
            // 获取nft列表的metadata数据
            SudNFTGetNFTMetadataParamModel getNFTMetadataParamModel = new SudNFTGetNFTMetadataParamModel();
            getNFTMetadataParamModel.walletToken = mBindWalletInfo.walletToken;
            getNFTMetadataParamModel.chainType = mBindWalletInfo.chainInfo.type;
            getNFTMetadataParamModel.contractAddress = nftInfo.contractAddress;
            getNFTMetadataParamModel.tokenId = nftInfo.tokenId;
            SudNFT.getNFTMetadata(getNFTMetadataParamModel, new ISudNFTListenerGetMetadata() {
                @Override
                public void onSuccess(SudNFTGetMetadataModel sudNFTGetMetadataModel) {
                    LogUtils.d("nft: getNFTMetadata onSuccess:" + GsonUtils.toJson(sudNFTGetMetadataModel));
                    nftModel.metadataModel = sudNFTGetMetadataModel;
                    resultMap.put(finalI, true);
                    checkGetMetadataListIsCompleted(nftListResultModel, resultMap, listener, sudNFTGetNFTListModel, nftList);
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    LogUtils.e("nft: getNFTMetadata onFailure:" + retCode + "  retMsg:" + retMsg);
                    ToastUtils.showLong("getNFTMetadata onFailure:" + retCode + "  retMsg:" + retMsg);
                    resultMap.put(finalI, true);
                    checkGetMetadataListIsCompleted(nftListResultModel, resultMap, listener, sudNFTGetNFTListModel, nftList);
                }
            });
        }
    }

    // 检查元数据列表是否已全部返回
    private void checkGetMetadataListIsCompleted(NftListResultModel nftListResultModel, HashMap<Integer, Boolean> resultMap, GetNftListListener listener,
                                                 SudNFTGetNFTListModel sudNFTGetNFTListModel, List<NftModel> nftList) {
        boolean isCompleted = true;
        Set<Integer> keySet = resultMap.keySet();
        for (Integer key : keySet) {
            if (Boolean.FALSE.equals(resultMap.get(key))) {
                isCompleted = false;
                break;
            }
        }

        if (isCompleted && listener != null) {
            nftListResultModel.list = nftList;
            listener.onSuccess(nftListResultModel);
        }
    }

    private ChainInfo getDefaultChainType(WalletInfo walletInfo) {
        List<ChainInfo> list = walletInfo.chains;
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void initNFT(Context context, ISudNFTListenerInitNFT listener) {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig == null || sudConfig.appId == null || sudConfig.appKey == null) {
            if (listener != null) {
                listener.onFailure(RetCode.FAIL, "SudConfig is empty");
            }
            return;
        }
        SudInitNFTParamModel model = new SudInitNFTParamModel();
        model.context = context;
        model.appId = sudConfig.appId;
        model.appKey = sudConfig.appKey;
        model.userId = HSUserInfo.userId + "";
        model.isTestEnv = APPConfig.GAME_IS_TEST_ENV;
        SudNFT.initNFT(model, listener);
    }

    /** 获取钱包列表 */
    public void getWalletList(ISudNFTListenerGetWalletList listener) {
        if (walletListModel != null) {
            if (listener != null) {
                listener.onSuccess(walletListModel);
            }
            return;
        }
        SudNFT.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                walletListModel = sudNFTGetWalletListModel;
                if (listener != null) {
                    listener.onSuccess(sudNFTGetWalletListModel);
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                if (listener != null) {
                    listener.onFailure(retCode, retMsg);
                }
            }
        });
    }

    /** 获取已绑定的钱包信息 */
    public BindWalletInfoModel getBindWalletInfo() {
        return mBindWalletInfo;
    }

    // 本地缓存当中获取已绑定钱包信息
    private BindWalletInfoModel getBindWalletInfoByCache() {
        String json = GlobalSP.getSP().getString(GlobalSP.NFT_BIND_WALLET_KEY);
        if (json != null) {
            return SudJsonUtils.fromJson(json, BindWalletInfoModel.class);
        }
        return null;
    }

    // 写入已绑定钱包信息
    private void putBindWalletInfo(BindWalletInfoModel model) {
        if (model != null) {
            GlobalSP.getSP().put(GlobalSP.NFT_BIND_WALLET_KEY, SudJsonUtils.toJson(model));
        }
    }

    /** 修改链 */
    public void changeChain(ChainInfo chainInfo) {
        if (mBindWalletInfo == null) {
            return;
        }
        mBindWalletInfo.chainInfo = chainInfo;
        bindWalletInfoMutableLiveData.setValue(mBindWalletInfo);
        putBindWalletInfo(mBindWalletInfo);
        initNftList(mBindWalletInfo);
    }

    /** 解绑钱包 */
    public void unbindWallet() {
        HSUserInfo.walletAddress = null;
        LoginRepository.saveUserInfo();
        mBindWalletInfo = null;
        bindWalletInfoMutableLiveData.setValue(null);
        GlobalSP.getSP().remove(GlobalSP.NFT_BIND_WALLET_KEY);
        initDataGetBindWallet();
        // TODO: 2022/8/9  如果有穿戴的数据，解绑之后，还需要解除穿戴
    }

    /** 穿戴nft */
    public void wearNft(NftModel wearNft) {
        if (mBindWalletInfo == null) {
            return;
        }
        SudNFTCredentialsTokenParamModel model = new SudNFTCredentialsTokenParamModel();
        model.walletToken = mBindWalletInfo.walletToken;
        model.chainType = mBindWalletInfo.getChainType();
        model.contractAddress = wearNft.contractAddress;
        model.tokenId = wearNft.tokenId;
        SudNFT.genNFTCredentialsToken(model, new ISudNFTListenerGenNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenNFTCredentialsTokenModel sudNFTGenNFTCredentialsTokenModel) {
                if (mBindWalletInfo == null || sudNFTGenNFTCredentialsTokenModel == null) {
                    return;
                }
                String nftToken = sudNFTGenNFTCredentialsTokenModel.nftDetailsToken;
                HomeRepository.wearNft(null, nftToken, 1, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (mBindWalletInfo == null) {
                            return;
                        }
                        mBindWalletInfo.wearNft = wearNft;
                        putBindWalletInfo(mBindWalletInfo);
                        wearNftChangeLiveData.setValue(null);
                        updateUserInfo(nftToken);
                    }
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("genNFTCredentialsToken onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    /** 取消穿戴nft */
    public void cancelWearNft() {
        HomeRepository.wearNft(null, null, 2, new RxCallback<>());
        mBindWalletInfo.wearNft = null;
        putBindWalletInfo(mBindWalletInfo);
        wearNftChangeLiveData.setValue(null);
        updateUserInfo(null);
    }

    /** 获取穿戴的nft */
    public NftModel getWearNft() {
        if (mBindWalletInfo != null) {
            return mBindWalletInfo.wearNft;
        }
        return null;
    }

    private void updateUserInfo(String nftToken) {
        NftModel wearNft = getWearNft();
        if (wearNft == null) {
            HSUserInfo.headerType = 0;
            HSUserInfo.headerNftToken = null;
            HSUserInfo.headerNftUrl = null;
        } else {
            HSUserInfo.headerType = 1;
            HSUserInfo.headerNftToken = nftToken;
            HSUserInfo.headerNftUrl = wearNft.getImageUrl();
        }
        LoginRepository.saveUserInfo();
    }

    public interface GetNftListListener {
        void onSuccess(NftListResultModel model);

        void onFailure(int retCode, String retMsg);
    }

}
