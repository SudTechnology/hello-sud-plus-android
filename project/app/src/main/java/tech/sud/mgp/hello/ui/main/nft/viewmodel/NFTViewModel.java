package tech.sud.mgp.hello.ui.main.nft.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.BuildConfig;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.base.BaseViewModel;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.GlobalSP;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
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
import tech.sud.nft.core.ISudNFTD;
import tech.sud.nft.core.listener.ISudNFTListenerBindCnWallet;
import tech.sud.nft.core.listener.ISudNFTListenerBindWallet;
import tech.sud.nft.core.listener.ISudNFTListenerGenCnNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerGenNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerGetCnNFTList;
import tech.sud.nft.core.listener.ISudNFTListenerGetNFTList;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.listener.ISudNFTListenerInitNFT;
import tech.sud.nft.core.listener.ISudNFTListenerRemoveCnNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerRemoveNFTCredentialsToken;
import tech.sud.nft.core.listener.ISudNFTListenerSendSmsCode;
import tech.sud.nft.core.listener.ISudNFTListenerUnbindCnWallet;
import tech.sud.nft.core.listener.ISudNFTListenerUnbindWallet;
import tech.sud.nft.core.model.param.SudInitNFTParamModel;
import tech.sud.nft.core.model.param.SudNFTBindCnWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTBindWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTCnCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTGetCnNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTGetNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTRemoveCnCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTRemoveCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTSendSmsCodeParamModel;
import tech.sud.nft.core.model.param.SudNFTUnbindCnWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTUnbindWalletParamModel;
import tech.sud.nft.core.model.resp.SudNFTBindCnWalletModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletEvent;
import tech.sud.nft.core.model.resp.SudNFTBindWalletModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletStage;
import tech.sud.nft.core.model.resp.SudNFTGenCnNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGenNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGetCnNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel.WalletInfo;

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

    private final SudNFTProxy mSudNFTProxy = new SudNFTProxy(this);
    private SudNFTGetWalletListModel mWalletListModel; // 钱包列表
    public static BindWalletInfoModel sBindWalletInfo; // 已绑定的钱包数据
    private static boolean sIsInitCompleted; // 是否初始化完成

    /**
     * 初始化数据
     * 1，未绑定时显示钱包列表
     * 2，已绑定时显示nft列表
     */
    public void initData(Context context) {
        sBindWalletInfo = getBindWalletInfoByCache();
        if (sIsInitCompleted) {
            initDataGetBindWallet();
        } else {
            initNFT(context, new ISudNFTListenerInitNFT() {
                @Override
                public void onSuccess() {
                    sIsInitCompleted = true;
                    initDataGetBindWallet();
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    sIsInitCompleted = false;
                    ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
                }
            });
        }
    }

    // 判断是否绑定了钱包
    private void initDataGetBindWallet() {
        sBindWalletInfo = getBindWalletInfoByCache();
        if (sBindWalletInfo == null) {
            // 未绑定钱包，显示钱包列表
            showWalletList();
        } else {
            // 绑定了钱包，显示NFT列表
            initNftList(sBindWalletInfo);
        }
        bindWalletInfoMutableLiveData.setValue(sBindWalletInfo);
    }

    // 展示钱包列表
    private void showWalletList() {
        getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                if (sBindWalletInfo != null) {
                    return;
                }
                initDataShowWalletListLiveData.setValue(sudNFTGetWalletListModel);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
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
        mSudNFTProxy.bindWallet(paramModel, new ISudNFTListenerBindWallet() {
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
    public void sendSmsCode(long walletType, String phone, ISudNFTListenerSendSmsCode listener) {
        SudNFTSendSmsCodeParamModel model = new SudNFTSendSmsCodeParamModel();
        model.walletType = walletType;
        model.phone = phone;
        mSudNFTProxy.sendSmsCode(model, listener);
    }

    /** 短信验证码绑定钱包 */
    public void bindCnWallet(WalletInfo walletInfo, String userId, String phone, String phoneCode, ISudNFTListenerBindCnWallet listener) {
        SudNFTBindCnWalletParamModel paramModel = new SudNFTBindCnWalletParamModel();
        paramModel.walletType = walletInfo.type;
        paramModel.userId = userId;
        paramModel.phone = phone;
        paramModel.smsCode = phoneCode;
        mSudNFTProxy.smsCodeBindWallet(paramModel, new ISudNFTListenerBindCnWallet() {

            @Override
            public void onSuccess(SudNFTBindCnWalletModel resp) {
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
    private void onBindCNWalletSuccess(WalletInfo walletInfo, SudNFTBindCnWalletParamModel paramModel, SudNFTBindCnWalletModel resp) {
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
        walletInfoModel.chainInfoList = WalletChainInfoConvertor.conver(walletInfo.chainList);
        walletInfoModel.chainInfo = walletInfoModel.getDefaultChainInfo();
        walletInfoModel.walletToken = resp.walletToken;
        walletInfoModel.phone = paramModel.phone;
        bindWalletInfoModel.addBindWallet(walletInfoModel);

        // 记录当前使用的钱包信息
        bindWalletInfoModel.walletType = walletInfoModel.type;
        saveBindWalletInfo(bindWalletInfoModel);
        sBindWalletInfo = bindWalletInfoModel;

        // 全局用户信息里显示
        bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);

        // 获取NFT列表
        initNftList(bindWalletInfoModel);
    }

    /** 获取国内钱包nft列表 */
    public void getCNNftList(SudNFTGetCnNFTListParamModel model, GetNftListListener listener) {
        mSudNFTProxy.getCNNFTList(model, new ISudNFTListenerGetCnNFTList() {
            @Override
            public void onSuccess(SudNFTGetCnNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(NftListResultModelConvertor.conver(resp));
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
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
        walletInfoModel.chainInfoList = WalletChainInfoConvertor.conver(walletInfo.chainList);
        walletInfoModel.chainInfo = walletInfoModel.getDefaultChainInfo();
        walletInfoModel.walletToken = model.walletToken;
        walletInfoModel.walletAddress = model.walletAddress;
        bindWalletInfoModel.addBindWallet(walletInfoModel);

        // 记录当前使用的钱包信息
        bindWalletInfoModel.walletType = walletInfoModel.type;
        saveBindWalletInfo(bindWalletInfoModel);
        sBindWalletInfo = bindWalletInfoModel;

        // 全局用户信息里显示
        bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);

        // 获取NFT列表
        initNftList(bindWalletInfoModel);
    }

    // 初始化数据获取nft列表
    private void initNftList(BindWalletInfoModel bindWalletInfoModel) {
        if (bindWalletInfoModel.getZoneType() == ZoneType.OVERSEAS) {
            getNftList(bindWalletInfoModel);
        } else if (bindWalletInfoModel.getZoneType() == ZoneType.INTERNAL) {
            getCNNftList(bindWalletInfoModel);
        }
    }

    private void getCNNftList(BindWalletInfoModel bindWalletInfoModel) {
        SudNFTGetCnNFTListParamModel paramModel = new SudNFTGetCnNFTListParamModel();
        paramModel.walletType = bindWalletInfoModel.walletType;
        paramModel.walletToken = bindWalletInfoModel.getWalletToken();
        paramModel.pageNumber = 0;
        paramModel.pageSize = APPConfig.GLOBAL_PAGE_SIZE;
        getCNNftList(paramModel, new GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                if (sBindWalletInfo == null) {
                    LogUtils.d("nft: getCNNftList bindWalletInfo empty");
                    return;
                }
                if (sBindWalletInfo.walletType == paramModel.walletType) {
                    initDataShowNftListLiveData.setValue(model);
                }
                LogUtils.d("nft: getCNNftList onSuccess:" + GsonUtils.toJson(model));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
                LogUtils.e("nft: getCNNftList onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    private void getNftList(BindWalletInfoModel bindWalletInfoModel) {
        SudNFTGetNFTListParamModel paramModel = new SudNFTGetNFTListParamModel();
        paramModel.walletToken = bindWalletInfoModel.getWalletToken();
        paramModel.chainType = bindWalletInfoModel.getChainType();
        paramModel.walletAddress = bindWalletInfoModel.getWalletAddress();
        getNftList(paramModel, new GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                if (sBindWalletInfo == null) {
                    LogUtils.d("nft: initNftList bindWalletInfo empty");
                    return;
                }
                if (sBindWalletInfo.getChainType() == paramModel.chainType) {
                    initDataShowNftListLiveData.setValue(model);
                }
                LogUtils.d("nft: initNftList onSuccess:" + GsonUtils.toJson(model));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
                LogUtils.e("nft: initNftList onFailure:" + retCode + "  retMsg:" + retMsg);
            }
        });
    }

    /** 获取nft列表 */
    public void getNftList(SudNFTGetNFTListParamModel model, GetNftListListener listener) {
        mSudNFTProxy.getNFTList(model, new ISudNFTListenerGetNFTList() {
            @Override
            public void onSuccess(SudNFTGetNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(NftListResultModelConvertor.conver(resp));
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
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
        initEnv();

        SudInitNFTParamModel model = new SudInitNFTParamModel();
        model.context = context;
        model.appId = sudConfig.appId;
        model.appKey = sudConfig.appKey;
        model.userId = HSUserInfo.userId + "";
        model.isTestEnv = APPConfig.GAME_IS_TEST_ENV;
        mSudNFTProxy.initNFT(model, listener);
    }

    private void initEnv() {
        // region 为demo代码，直接忽略
        if ("dev".equalsIgnoreCase(BuildConfig.nftEnv)) {
            ISudNFTD.e(4);
            ISudNFTD.d();
        } else if ("fat".equalsIgnoreCase(BuildConfig.nftEnv)) {
            ISudNFTD.e(3);
        }
        // endregion 为demo代码，直接忽略
    }

    /** 获取钱包列表 */
    public void getWalletList(ISudNFTListenerGetWalletList listener) {
        if (mWalletListModel != null) {
            if (listener != null) {
                listener.onSuccess(mWalletListModel);
            }
            return;
        }
        mSudNFTProxy.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                mWalletListModel = sudNFTGetWalletListModel;
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
        return sBindWalletInfo;
    }

    // 本地缓存当中获取已绑定钱包信息
    private BindWalletInfoModel getBindWalletInfoByCache() {
        String json = GlobalSP.getSP().getString(GlobalSP.KEY_NFT_BIND_WALLET);
        if (json != null) {
            return SudJsonUtils.fromJson(json, BindWalletInfoModel.class);
        }
        return null;
    }

    // 写入已绑定钱包信息
    private void saveBindWalletInfo(BindWalletInfoModel model) {
        if (model != null) {
            GlobalSP.getSP().put(GlobalSP.KEY_NFT_BIND_WALLET, SudJsonUtils.toJson(model));
        }
    }

    /** 修改链 */
    public void changeChain(WalletChainInfo chainInfo) {
        if (sBindWalletInfo == null) {
            return;
        }
        sBindWalletInfo.setChainInfo(chainInfo);
        bindWalletInfoMutableLiveData.setValue(sBindWalletInfo);
        saveBindWalletInfo(sBindWalletInfo);
        initNftList(sBindWalletInfo);
    }

    /** 解绑国外钱包的处理 */
    public void unbindWallet(long walletType, ISudNFTListenerUnbindWallet listener) {
        WalletInfoModel walletInfo = getWalletInfo(walletType);
        if (walletInfo == null) {
            unbindWalletSuccess(walletType);
            if (listener != null) {
                listener.onSuccess();
            }
        } else {
            SudNFTUnbindWalletParamModel model = new SudNFTUnbindWalletParamModel();
            model.userId = HSUserInfo.userId + "";
            model.walletType = walletInfo.type;
            model.walletAddress = walletInfo.walletAddress;
            mSudNFTProxy.unbindWallet(model, new ISudNFTListenerUnbindWallet() {
                @Override
                public void onSuccess() {
                    unbindWalletSuccess(walletType);
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    if (listener != null) {
                        listener.onFailure(code, msg);
                    }
                }
            });
        }
    }

    /** 解绑国外钱包，因为只能绑定一个钱包，所以这里是清除所有的逻辑处理 */
    public void unbindWallet() {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        SudNFTUnbindWalletParamModel model = new SudNFTUnbindWalletParamModel();
        model.userId = HSUserInfo.userId + "";
        model.walletType = bindWalletInfoModel.walletType;
        model.walletAddress = bindWalletInfoModel.getWalletAddress();
        mSudNFTProxy.unbindWallet(model, new ISudNFTListenerUnbindWallet() {
            @Override
            public void onSuccess() {
                clearBindWallet();
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
            }
        });
    }

    private void clearBindWallet() {
        sBindWalletInfo = null;
        bindWalletInfoMutableLiveData.setValue(null);
        GlobalSP.getSP().remove(GlobalSP.KEY_NFT_BIND_WALLET);

        clearWearNft();

        initDataGetBindWallet();
    }

    /** 解绑国内钱包的处理 */
    public void unbindCNWallet(long walletType, ISudNFTListenerUnbindCnWallet listener) {
        WalletInfoModel walletInfo = getWalletInfo(walletType);
        if (walletInfo == null) {
            unbindWalletSuccess(walletType);
            if (listener != null) {
                listener.onSuccess();
            }
        } else {
            SudNFTUnbindCnWalletParamModel model = new SudNFTUnbindCnWalletParamModel();
            model.userId = HSUserInfo.userId + "";
            model.phone = walletInfo.phone;
            model.walletType = walletType;
            mSudNFTProxy.unbindCnWallet(model, new ISudNFTListenerUnbindCnWallet() {
                @Override
                public void onSuccess() {
                    unbindWalletSuccess(walletType);
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    if (listener != null) {
                        listener.onFailure(code, msg);
                    }
                }
            });
        }
    }

    private WalletInfoModel getWalletInfo(long walletType) {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel != null) {
            return bindWalletInfoModel.getWalletInfoModel(walletType);
        }
        return null;
    }

    /** 解绑钱包成功，清除本地数据 */
    private void unbindWalletSuccess(long walletType) {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            clearBindWallet();
        } else {
            bindWalletInfoModel.removeBindWallet(walletType);
            WalletInfoModel walletInfoModel = bindWalletInfoModel.getFirstWalletInfoModel();
            if (walletInfoModel == null) {
                clearBindWallet();
                return;
            }

            // 还有其它的钱包，切换到其它的钱包
            bindWalletInfoModel.walletType = walletInfoModel.type;
            saveBindWalletInfo(bindWalletInfoModel);
            bindWalletInfoMutableLiveData.setValue(bindWalletInfoModel);
        }
    }

    /** 钱包令牌失效，解绑当前钱包 */
    public void tokenFailed(long walletType) {
        unbindWalletSuccess(walletType);
    }

    /** 穿戴nft */
    public void wearNft(NftModel wearNft) {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        if (bindWalletInfoModel.getZoneType() == ZoneType.OVERSEAS) {
            wearOverseasNft(wearNft, bindWalletInfoModel);
        } else if (bindWalletInfoModel.getZoneType() == ZoneType.INTERNAL) {
            wearInternalNft(wearNft, bindWalletInfoModel);
        }
    }

    /** 穿戴国外钱包NFT */
    private void wearOverseasNft(NftModel wearNft, BindWalletInfoModel bindWalletInfoModel) {
        SudNFTCredentialsTokenParamModel model = new SudNFTCredentialsTokenParamModel();
        int zoneType = bindWalletInfoModel.getZoneType();
        long walletType = bindWalletInfoModel.walletType;
        model.walletToken = bindWalletInfoModel.getWalletToken();
        model.chainType = bindWalletInfoModel.getChainType();
        model.contractAddress = wearNft.contractAddress;
        model.tokenId = wearNft.tokenId;
        model.extension = wearNft.extension;
        mSudNFTProxy.genNFTCredentialsToken(model, new ISudNFTListenerGenNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenNFTCredentialsTokenModel resp) {
                if (sBindWalletInfo == null || resp == null) {
                    return;
                }
                String nftToken = resp.detailsToken;
                HomeRepository.wearNft(null, nftToken, 1, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (sBindWalletInfo == null) {
                            return;
                        }
                        wearNft.detailsToken = nftToken;
                        wearNft.zoneType = zoneType;
                        wearNft.walletType = walletType;
                        sBindWalletInfo.clearWearNft();
                        sBindWalletInfo.addWearNft(wearNft);
                        saveBindWalletInfo(sBindWalletInfo);
                        wearNftChangeLiveData.setValue(null);
                        updateUserInfo(nftToken);
                    }
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
            }
        });
    }

    /** 穿戴国内钱包NFT */
    private void wearInternalNft(NftModel wearNft, BindWalletInfoModel bindWalletInfoModel) {
        SudNFTCnCredentialsTokenParamModel model = new SudNFTCnCredentialsTokenParamModel();
        int zoneType = bindWalletInfoModel.getZoneType();
        long walletType = bindWalletInfoModel.walletType;
        model.walletType = bindWalletInfoModel.walletType;
        model.walletToken = bindWalletInfoModel.getWalletToken();
        model.cardId = wearNft.cardId;
        mSudNFTProxy.genCnNFTCredentialsToken(model, new ISudNFTListenerGenCnNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenCnNFTCredentialsTokenModel resp) {
                if (sBindWalletInfo == null || resp == null) {
                    return;
                }
                String nftToken = resp.detailsToken;
                HomeRepository.wearNft(null, nftToken, 1, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                        if (sBindWalletInfo == null) {
                            return;
                        }
                        wearNft.detailsToken = nftToken;
                        wearNft.zoneType = zoneType;
                        wearNft.walletType = walletType;
                        sBindWalletInfo.clearWearNft();
                        sBindWalletInfo.addWearNft(wearNft);
                        saveBindWalletInfo(sBindWalletInfo);
                        wearNftChangeLiveData.setValue(null);
                        updateUserInfo(nftToken);
                    }
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
            }
        });
    }

    /** 取消穿戴nft */
    public void cancelWearNft(CancelWearNftListener listener) {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        NftModel wearNft = bindWalletInfoModel.getWearNft();
        if (wearNft == null) {
            return;
        }
        int zoneType = bindWalletInfoModel.getZoneType();
        if (zoneType == ZoneType.OVERSEAS) {
            SudNFTRemoveCredentialsTokenParamModel model = new SudNFTRemoveCredentialsTokenParamModel();
            model.walletToken = bindWalletInfoModel.getWalletToken();
            model.detailsToken = wearNft.detailsToken;
            mSudNFTProxy.removeNFTCredentialsToken(model, new ISudNFTListenerRemoveNFTCredentialsToken() {
                @Override
                public void onSuccess() {
                    clearWearNft();
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
                    if (listener != null) {
                        listener.onFailure(retCode, retMsg);
                    }
                }
            });
        } else if (zoneType == ZoneType.INTERNAL) {
            SudNFTRemoveCnCredentialsTokenParamModel model = new SudNFTRemoveCnCredentialsTokenParamModel();
            model.walletToken = bindWalletInfoModel.getWalletToken();
            model.detailsToken = wearNft.detailsToken;
            mSudNFTProxy.removeCnNFTCredentialsToken(model, new ISudNFTListenerRemoveCnNFTCredentialsToken() {
                @Override
                public void onSuccess() {
                    clearWearNft();
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    ToastUtils.showLong(ResponseUtils.nftConver(retCode, retMsg));
                    if (listener != null) {
                        listener.onFailure(retCode, retMsg);
                    }
                }
            });
        }
    }

    /** 清除穿戴信息 */
    public void clearWearNft() {
        // 发送后端接口进行取消穿戴
        HomeRepository.wearNft(null, null, 2, new RxCallback<>());

        if (sBindWalletInfo != null) {
            sBindWalletInfo.clearWearNft();
            saveBindWalletInfo(sBindWalletInfo);
        }
        wearNftChangeLiveData.setValue(null);
        updateUserInfo(null);
    }

    /** 获取穿戴的nft */
    public NftModel getWearNft() {
        if (sBindWalletInfo != null) {
            return sBindWalletInfo.getWearNft();
        }
        return null;
    }

    private void updateUserInfo(String nftToken) {
        NftModel wearNft = getWearNft();
        if (wearNft == null) {
            HSUserInfo.headerType = 0;
            HSUserInfo.headerNftUrl = null;
        } else {
            HSUserInfo.headerType = 1;
            HSUserInfo.headerNftUrl = wearNft.getShowUrl();
        }
        LoginRepository.saveUserInfo();
    }

    /**
     * 切换当前所使用的钱包
     */
    public void changeWallet(long walletType) {
        BindWalletInfoModel bindWalletInfoModel = sBindWalletInfo;
        if (bindWalletInfoModel == null) {
            return;
        }
        WalletInfoModel model = bindWalletInfoModel.getWalletInfoModel(walletType);
        if (model == null) {
            return;
        }
        bindWalletInfoModel.walletType = model.type;

        saveBindWalletInfo(bindWalletInfoModel);
    }

    public interface GetNftListListener {
        void onSuccess(NftListResultModel model);

        void onFailure(int retCode, String retMsg);
    }

}
