package tech.sud.mgp.hello.ui.main.nft.viewmodel;

import android.app.Activity;
import android.content.Intent;

import com.blankj.utilcode.util.ActivityUtils;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;

import tech.sud.mgp.hello.common.event.LiveEventBusKey;
import tech.sud.mgp.hello.common.event.NftTokenInvalidEvent;
import tech.sud.mgp.hello.ui.main.base.activity.MainActivity;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
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
import tech.sud.nft.core.listener.ISudNFTListenerUnbindCNWallet;
import tech.sud.nft.core.model.param.SudInitNFTParamModel;
import tech.sud.nft.core.model.param.SudNFTBindWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTCNCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTCredentialsTokenParamModel;
import tech.sud.nft.core.model.param.SudNFTGetCNNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTGetNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTSendSmsCodeParamModel;
import tech.sud.nft.core.model.param.SudNFTSmsCodeBindWalletParamModel;
import tech.sud.nft.core.model.param.SudNFTUnbindCNWalletParamModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletEvent;
import tech.sud.nft.core.model.resp.SudNFTBindWalletModel;
import tech.sud.nft.core.model.resp.SudNFTBindWalletStage;
import tech.sud.nft.core.model.resp.SudNFTGenCNNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGenNFTCredentialsTokenModel;
import tech.sud.nft.core.model.resp.SudNFTGetCNNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetNFTListModel;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;
import tech.sud.nft.core.model.resp.SudNFTSmsCodeBindWalletModel;

/**
 * SudNFT代理类
 */
public class SudNFTProxy {

    public static NFTViewModel viewModel = new NFTViewModel();

    /**
     * 初始化NFT
     *
     * @param model    初始化参数
     * @param listener 回调
     */
    public static void initNFT(SudInitNFTParamModel model, ISudNFTListenerInitNFT listener) {
        Integer walletType = getWalletType();
        SudNFT.initNFT(model, new ISudNFTListenerInitNFT() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 获取支持钱包列表
     *
     * @param listener 回调
     */
    public static void getWalletList(ISudNFTListenerGetWalletList listener) {
        Integer walletType = getWalletType();
        SudNFT.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    // region 国外钱包接口

    /**
     * 绑定(授权)钱包
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void bindWallet(SudNFTBindWalletParamModel model, ISudNFTListenerBindWallet listener) {
        Integer walletType = getWalletType();
        SudNFT.bindWallet(model, new ISudNFTListenerBindWallet() {
            @Override
            public void onSuccess(SudNFTBindWalletModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
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

    /**
     * 获取NFT列表,必须授权成功之后才能获取NFT列表
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void getNFTList(SudNFTGetNFTListParamModel model, ISudNFTListenerGetNFTList listener) {
        Integer walletType = getWalletType();
        SudNFT.getNFTList(model, new ISudNFTListenerGetNFTList() {
            @Override
            public void onSuccess(SudNFTGetNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 生成元数据使用唯一认证token
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void genNFTCredentialsToken(SudNFTCredentialsTokenParamModel model, ISudNFTListenerGenNFTCredentialsToken listener) {
        Integer walletType = getWalletType();
        SudNFT.genNFTCredentialsToken(model, new ISudNFTListenerGenNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenNFTCredentialsTokenModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }
    // endregion 国外钱包接口

    // region 国内钱包接口

    /**
     * 发送短信验证码
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void sendSmsCode(SudNFTSendSmsCodeParamModel model, ISudNFTListenerSendSmsCode listener) {
        Integer walletType = getWalletType();
        SudNFT.sendSmsCode(model, new ISudNFTListenerSendSmsCode() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 短信验证码 绑定钱包
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void smsCodeBindWallet(SudNFTSmsCodeBindWalletParamModel model, ISudNFTListenerSmsCodeBindWallet listener) {
        Integer walletType = getWalletType();
        SudNFT.smsCodeBindWallet(model, new ISudNFTListenerSmsCodeBindWallet() {
            @Override
            public void onSuccess(SudNFTSmsCodeBindWalletModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 获取NFT列表
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void getCNNFTList(SudNFTGetCNNFTListParamModel model, ISudNFTListenerGetCNNFTList listener) {
        Integer walletType = getWalletType();
        SudNFT.getCNNFTList(model, new ISudNFTListenerGetCNNFTList() {
            @Override
            public void onSuccess(SudNFTGetCNNFTListModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 生成元数据使用唯一认证token
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void genCNNFTCredentialsToken(SudNFTCNCredentialsTokenParamModel model, ISudNFTListenerGenCNNFTCredentialsToken listener) {
        Integer walletType = getWalletType();
        SudNFT.genCNNFTCredentialsToken(model, new ISudNFTListenerGenCNNFTCredentialsToken() {
            @Override
            public void onSuccess(SudNFTGenCNNFTCredentialsTokenModel resp) {
                if (listener != null) {
                    listener.onSuccess(resp);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }

    /**
     * 解绑国内钱包
     *
     * @param model    参数
     * @param listener 回调
     */
    public static void unbindCNWallet(SudNFTUnbindCNWalletParamModel model, ISudNFTListenerUnbindCNWallet listener) {
        Integer walletType = getWalletType();
        SudNFT.unbindCNWallet(model, new ISudNFTListenerUnbindCNWallet() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onFailure(code, msg);
                }
                processonFailure(walletType, code, msg);
            }
        });
    }
    // endregion 国内钱包接口

    private static Integer getWalletType() {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            return bindWalletInfo.walletType;
        }
        return null;
    }

    /** 抽取错误码共性，统一处理 */
    private static void processonFailure(Integer walletType, int code, String msg) {
        if (code == 1008) { // 钱包令牌无效，执行解绑
            if (walletType != null) {
                // 清除本地信息
                viewModel.tokenFailed(walletType);

                // 跳转到首页
                Activity topActivity = ActivityUtils.getTopActivity();
                if (topActivity != null && !(topActivity instanceof MainActivity)) {
                    Intent intent = new Intent(topActivity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    topActivity.startActivity(intent);
                }

                // 发送通知
                LiveEventBus.<NftTokenInvalidEvent>get(LiveEventBusKey.KEY_NFT_TOKEN_INVALID).post(new NftTokenInvalidEvent());
            }
        }
    }

}
