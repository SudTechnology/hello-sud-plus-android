package tech.sud.mgp.hello.ui.main.nft.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

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
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftListResultModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftListResultModelConvertor;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletChainInfo;
import tech.sud.mgp.hello.ui.main.nft.model.WalletChainInfoConvertor;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.nft.core.SudNFT;
import tech.sud.nft.core.listener.ISudNFTListenerBindWallet;
import tech.sud.nft.core.listener.ISudNFTListenerGenCNNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerGenNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerGetCNNFTList;
import tech.sud.nft.core.listener.ISudNFTListenerGetNFTList;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.listener.ISudNFTListenerInitNFT;
import tech.sud.nft.core.listener.ISudNFTListenerSendSmsCode;
import tech.sud.nft.core.listener.ISudNFTListenerSmsCodeBindWallet;
import tech.sud.nft.core.model.param.SudInitNFTParamModel;
import tech.sud.nft.core.model.param.SudNFTBindWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTCNCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTGetCNNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTGetNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTSendSmsCodeParamModel;
import tech.sud.nft.core.model.param.SudNFTSmsCodeBindWalletParamModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletEvent;
import tech.sud.nft.core.model.resp.SudNFTBindWalletModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletStage;
import tech.sud.nft.core.model.resp.SudNFTGenCNNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGenNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGetCNNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel.WalletInfo;
import tech.sud.nft.core.model.resp.SudNFTSmsCodeBindWalletModel;

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
        mBindWalletInfo = getBindWalletInfoByCache();
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
    public void bindWallet(Context context, WalletInfo walletInfo, ISudNFTListenerBindWallet listener) {
        if (walletInfo == null) {
            return;
        }
        SudNFTBindWalletParamModel paramModel = new SudNFTBindWalletParamModel();
        paramModel.context = context;
        paramModel.walletType = walletInfo.type;
        SudNFT.bindWallet(paramModel, new ISudNFTListenerBindWallet() {
            @Override
            public void onSuccess(SudNFTBindWalletModel model) {
                onBindWalletSuccess(walletInfo, model);
                LogUtils.d("nft: bindWallet onSuccess:" + GsonUtils.toJson(model));
                if (listener != null) {
                    listener.onSuccess(model);
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("nft: bindWallet onFailure:" + retCode + "  retMsg:" + retMsg);
                if (listener != null) {
                    listener.onFailure(retCode, retMsg);
                }
            }

            @Override
            public void onBindStageList(List<SudNFTBindWalletStage> list) {
                if (listener != null) {
                    listener.onBindStageList(list);
                }
            }

            @Override
            public void onBindStageEvent(SudNFTBindWalletStage stage, SudNFTBindWalletEvent event) {
                if (listener != null) {
                    listener.onBindStageEvent(stage, event);
                }
            }
        });
    }

    /** 发送短信验证码 */
    public void sendSmsCode(int walletType, String phone, ISudNFTListenerSendSmsCode listener) {
        SudNFTSendSmsCodeParamModel model = new SudNFTSendSmsCodeParamModel();
        model.walletType = walletType;
        model.phone = phone;
        SudNFT.sendSmsCode(model, listener);
    }

    /** 短信验证码绑定钱包 */
    public void bindCNWallet(WalletInfo walletInfo, String userId, String phone, String phoneCode, ISudNFTListenerSmsCodeBindWallet listener) {
        SudNFTSmsCodeBindWalletParamModel paramModel = new SudNFTSmsCodeBindWalletParamModel();
        paramModel.walletType = walletInfo.type;
        paramModel.userId = userId;
        paramModel.phone = phone;
        paramModel.phoneCode = phoneCode;
        SudNFT.smsCodeBindWallet(paramModel, new ISudNFTListenerSmsCodeBindWallet() {

            @Override
            public void onSuccess(SudNFTSmsCodeBindWalletModel resp) {
                LogUtils.d("nft: bindCNWallet onSuccess:" + GsonUtils.toJson(resp));
                onBindCNWalletSuccess(walletInfo, paramModel, resp);
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("nft: bindCNWallet onFailure:" + retCode + "  retMsg:" + retMsg);
                if (listener != null) {
                    listener.onFailure(retCode, retMsg);
                }
            }

        });
    }

    // 绑定国内钱包成功之后的处理
    private void onBindCNWalletSuccess(WalletInfo walletInfo, SudNFTSmsCodeBindWalletParamModel paramModel, SudNFTSmsCodeBindWalletModel resp) {
        if (resp == null) {
            return;
        }
        BindWalletInfoModel bindWalletInfoModel = getBindWalletInfoByCache();
        if (bindWalletInfoModel == null) {
            bindWalletInfoModel = new BindWalletInfoModel();
        }
        // 记录绑定的钱包数据
        WalletInfoModel walletInfoModel = new WalletInfoModel();
        walletInfoModel.type = walletInfo.type;
        walletInfoModel.name = walletInfo.name;
        walletInfoModel.icon = walletInfo.icon;
        walletInfoModel.zoneType = walletInfo.zoneType;
        walletInfoModel.chainList = WalletChainInfoConvertor.conver(walletInfo.chainList);
        walletInfoModel.walletToken = resp.walletToken;
        walletInfoModel.phone = paramModel.phone;
        bindWalletInfoModel.addBindWallet(walletInfoModel);

        // 记录当前使用的钱包信息
        bindWalletInfoModel.walletType = walletInfoModel.type;
        bindWalletInfoModel.walletToken = walletInfoModel.walletToken;
        bindWalletInfoModel.chainInfoList = walletInfoModel.chainList;
        bindWalletInfoModel.chainInfo = bindWalletInfoModel.getDefaultChainInfo();
        bindWalletInfoModel.zoneType = walletInfoModel.zoneType;
        bindWalletInfoModel.phone = walletInfoModel.phone;
        saveBindWalletInfo(bindWalletInfoModel);
        mBindWalletInfo = bindWalletInfoModel;

        // 全局用户信息里显示
        HSUserInfo.walletAddress = bindWalletInfoModel.walletAddress;
        HSUserInfo.zoneType = bindWalletInfoModel.zoneType;
        LoginRepository.saveUserInfo();
        bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);

        // 获取NFT列表
        initNftList(bindWalletInfoModel);
    }

    /** 获取国内钱包nft列表 */
    public void getCNNftList(SudNFTGetCNNFTListParamModel model, GetNftListListener listener) {
        SudNFT.getCNNFTList(model, new ISudNFTListenerGetCNNFTList() {
            @Override
            public void onSuccess(SudNFTGetCNNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(NftListResultModelConvertor.conver(resp));
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("getCNNftList onFailure:" + retCode + "  retMsg:" + retMsg);
                if (listener != null) {
                    listener.onFailure(retCode, retMsg);
                }
            }
        });
    }

    /**
     * 绑定钱包成功
     *
     * @param walletInfo 钱包数据
     * @param model      绑定钱包请求参数
     */
    private void onBindWalletSuccess(WalletInfo walletInfo, SudNFTBindWalletModel model) {
        if (model == null) {
            return;
        }
        BindWalletInfoModel bindWalletInfoModel = getBindWalletInfoByCache();
        if (bindWalletInfoModel == null) {
            bindWalletInfoModel = new BindWalletInfoModel();
        }
        // 记录绑定的钱包数据
        WalletInfoModel walletInfoModel = new WalletInfoModel();
        walletInfoModel.type = walletInfo.type;
        walletInfoModel.name = walletInfo.name;
        walletInfoModel.icon = walletInfo.icon;
        walletInfoModel.zoneType = walletInfo.zoneType;
        walletInfoModel.chainList = WalletChainInfoConvertor.conver(walletInfo.chainList);
        walletInfoModel.walletToken = model.walletToken;
        walletInfoModel.walletAddress = model.walletAddress;
        bindWalletInfoModel.addBindWallet(walletInfoModel);

        // 记录当前使用的钱包信息
        bindWalletInfoModel.walletType = walletInfoModel.type;
        bindWalletInfoModel.walletToken = walletInfoModel.walletToken;
        bindWalletInfoModel.walletAddress = walletInfoModel.walletAddress;
        bindWalletInfoModel.chainInfoList = walletInfoModel.chainList;
        bindWalletInfoModel.chainInfo = bindWalletInfoModel.getDefaultChainInfo();
        bindWalletInfoModel.zoneType = walletInfoModel.zoneType;
        saveBindWalletInfo(bindWalletInfoModel);
        mBindWalletInfo = bindWalletInfoModel;

        // 全局用户信息里显示
        HSUserInfo.walletAddress = bindWalletInfoModel.walletAddress;
        HSUserInfo.zoneType = bindWalletInfoModel.zoneType;
        LoginRepository.saveUserInfo();
        bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);

        // 获取NFT列表
        initNftList(bindWalletInfoModel);
    }

    // 初始化数据获取nft列表
    private void initNftList(BindWalletInfoModel bindWalletInfoModel) {
        if (bindWalletInfoModel.zoneType == ZoneType.OVERSEAS) {
            getNftList(bindWalletInfoModel);
        } else if (bindWalletInfoModel.zoneType == ZoneType.INTERNAL) {
            getCNNftList(bindWalletInfoModel);
        }
    }

    private void getCNNftList(BindWalletInfoModel bindWalletInfoModel) {
        SudNFTGetCNNFTListParamModel paramModel = new SudNFTGetCNNFTListParamModel();
        paramModel.walletType = bindWalletInfoModel.walletType;
        paramModel.walletToken = bindWalletInfoModel.walletToken;
        paramModel.pageNumber = 0;
        paramModel.pageSize = APPConfig.GLOBAL_PAGE_SIZE;
        getCNNftList(paramModel, new GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                if (mBindWalletInfo == null) {
                    LogUtils.d("nft: getCNNftList bindWalletInfo empty");
                    return;
                }
                if (mBindWalletInfo.walletType == paramModel.walletType) {
                    initDataShowNftListLiveData.setValue(model);
                }
                LogUtils.d("nft: getCNNftList onSuccess:" + GsonUtils.toJson(model));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong("getCNNftList onFailure:" + retCode + "  retMsg:" + retMsg);
                LogUtils.e("nft: getCNNftList onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    private void getNftList(BindWalletInfoModel bindWalletInfoModel) {
        SudNFTGetNFTListParamModel paramModel = new SudNFTGetNFTListParamModel();
        paramModel.walletToken = bindWalletInfoModel.walletToken;
        paramModel.chainType = bindWalletInfoModel.getChainType();
        paramModel.walletAddress = bindWalletInfoModel.walletAddress;
        getNftList(paramModel, new GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                if (mBindWalletInfo == null) {
                    LogUtils.d("nft: initNftList bindWalletInfo empty");
                    return;
                }
                if (mBindWalletInfo.getChainType() == paramModel.chainType) {
                    initDataShowNftListLiveData.setValue(model);
                }
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
            public void onSuccess(SudNFTGetNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(NftListResultModelConvertor.conver(resp));
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
    private void saveBindWalletInfo(BindWalletInfoModel model) {
        if (model != null) {
            GlobalSP.getSP().put(GlobalSP.NFT_BIND_WALLET_KEY, SudJsonUtils.toJson(model));
        }
    }

    /** 修改链 */
    public void changeChain(WalletChainInfo chainInfo) {
        if (mBindWalletInfo == null) {
            return;
        }
        mBindWalletInfo.chainInfo = chainInfo;
        bindWalletInfoMutableLiveData.setValue(mBindWalletInfo);
        saveBindWalletInfo(mBindWalletInfo);
        initNftList(mBindWalletInfo);
    }

    /** 解绑国外钱包，因为只能绑定一个钱包，所以这里是清除所有的逻辑处理 */
    public void unbindWallet() {
        HSUserInfo.walletAddress = null;
        LoginRepository.saveUserInfo();

        mBindWalletInfo = null;
        bindWalletInfoMutableLiveData.setValue(null);
        GlobalSP.getSP().remove(GlobalSP.NFT_BIND_WALLET_KEY);

        cancelWearNft();

        initDataGetBindWallet();
    }

    /** 解绑国内钱包的处理 */
    public void unbindCNWallet(int walletType) {
        BindWalletInfoModel bindWalletInfoModel = mBindWalletInfo;
        if (bindWalletInfoModel == null) {
            unbindWallet();
        } else {
            bindWalletInfoModel.removeBindWallet(walletType);
            WalletInfoModel walletInfoModel = bindWalletInfoModel.getWalletInfoModel();
            if (walletInfoModel == null) {
                unbindWallet();
                return;
            }

            // 还有其它的钱包，切换到其它的国内钱包
            bindWalletInfoModel.walletType = walletInfoModel.type;
            bindWalletInfoModel.walletToken = walletInfoModel.walletToken;
            bindWalletInfoModel.phone = walletInfoModel.phone;
            saveBindWalletInfo(bindWalletInfoModel);
            bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);
        }
    }

    /** 穿戴nft */
    public void wearNft(NftModel wearNft) {
        BindWalletInfoModel bindWalletInfoModel = mBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        if (bindWalletInfoModel.zoneType == ZoneType.OVERSEAS) {
            wearOverseasNft(wearNft, bindWalletInfoModel);
        } else if (bindWalletInfoModel.zoneType == ZoneType.INTERNAL) {
            wearInternalNft(wearNft, bindWalletInfoModel);
        }
    }

    /** 穿戴国外钱包NFT */
    private void wearOverseasNft(NftModel wearNft, BindWalletInfoModel bindWalletInfoModel) {
        SudNFTCredentialsTokenParamModel model = new SudNFTCredentialsTokenParamModel();
        model.walletToken = bindWalletInfoModel.walletToken;
        model.chainType = bindWalletInfoModel.getChainType();
        model.contractAddress = wearNft.contractAddress;
        model.tokenId = wearNft.tokenId;
        SudNFT.genNFTCredentialsToken(model, new ISudNFTListenerGenNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenNFTCredentialsTokenModel resp) {
                if (mBindWalletInfo == null || resp == null) {
                    return;
                }
                String nftToken = resp.nftDetailsToken;
                HomeRepository.wearNft(null, nftToken, 1, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (mBindWalletInfo == null) {
                            return;
                        }
                        mBindWalletInfo.wearNft = wearNft;
                        saveBindWalletInfo(mBindWalletInfo);
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

    /** 穿戴国内钱包NFT */
    private void wearInternalNft(NftModel wearNft, BindWalletInfoModel bindWalletInfoModel) {
        SudNFTCNCredentialsTokenParamModel model = new SudNFTCNCredentialsTokenParamModel();
        model.walletType = bindWalletInfoModel.walletType;
        model.walletToken = bindWalletInfoModel.walletToken;
        model.cardId = wearNft.cardId;
        SudNFT.genCNNFTCredentialsToken(model, new ISudNFTListenerGenCNNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenCNNFTCredentialsTokenModel resp) {
                if (mBindWalletInfo == null || resp == null) {
                    return;
                }
                String nftToken = resp.detailsToken;
                HomeRepository.wearNft(null, nftToken, 1, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (mBindWalletInfo == null) {
                            return;
                        }
                        mBindWalletInfo.wearNft = wearNft;
                        saveBindWalletInfo(mBindWalletInfo);
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
        if (mBindWalletInfo != null) {
            mBindWalletInfo.wearNft = null;
            saveBindWalletInfo(mBindWalletInfo);
        }
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
            HSUserInfo.headerNftUrl = wearNft.getShowUrl();
        }
        LoginRepository.saveUserInfo();
    }

    /**
     * 切换当前所使用的钱包
     */
    public void changeAccount(int walletType) {
        BindWalletInfoModel bindWalletInfoModel = mBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        WalletInfoModel model = bindWalletInfoModel.getWalletInfoModel(walletType);
        if (model == null) {
            return;
        }
        bindWalletInfoModel.walletType = model.type;
        bindWalletInfoModel.walletToken = model.walletToken;
        bindWalletInfoModel.phone = model.phone;
        bindWalletInfoModel.chainInfoList = model.chainList;
        bindWalletInfoModel.chainInfo = bindWalletInfoModel.getDefaultChainInfo();
        bindWalletInfoModel.walletAddress = model.walletAddress;
        bindWalletInfoModel.zoneType = model.zoneType;

        saveBindWalletInfo(bindWalletInfoModel);
    }

    public interface GetNftListListener {
        void onSuccess(NftListResultModel model);

        void onFailure(int retCode, String retMsg);
    }

}
