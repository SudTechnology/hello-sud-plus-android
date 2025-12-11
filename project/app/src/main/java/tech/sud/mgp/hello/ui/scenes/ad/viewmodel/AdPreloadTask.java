package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.gip.r2.core.ISudRuntime2FSMGame;
import tech.sud.gip.r2.core.ISudRuntime2FSTAPP;
import tech.sud.gip.r2.core.ISudRuntime2ListenerInitSDK;
import tech.sud.gip.r2.core.SudRuntime2;
import tech.sud.gip.r2.core.SudRuntime2GameCoreHandle;
import tech.sud.gip.r2.core.SudRuntime2GameRuntime;
import tech.sud.gip.r2.core.SudRuntime2InitSDKParamModel;
import tech.sud.gip.r2.core.SudRuntime2LoadGameParamModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.service.room.resp.GiAdModel;
import tech.sud.mgp.hello.service.room.resp.OpenPassGameInfo;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnvUtils;

public class AdPreloadTask {

    private AppCompatActivity mActivity;
    private GiAdModel mGiAdModel;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ISudRuntime2FSTAPP mISudRuntime2FSTAPP;
    private PreloadListener mPreloadListener;

    public AdPreloadTask(AppCompatActivity activity, GiAdModel model) {
        mActivity = activity;
        mGiAdModel = model;
    }

    public void start(PreloadListener listener) {
        OpenPassGameInfo info = mGiAdModel.gameInfo;
        if (info == null) {
            return;
        }
        mPreloadListener = listener;
        switchGame(mActivity, info.gameId, info.url, info.version);
    }

    public void switchGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl) || TextUtils.isEmpty(gamePkgVersion)) {
            LogUtils.d("switchGame gameId or gameUrl or gamePkgVersion can not be empty");
            LogUtils.file("switchGame gameId or gameUrl or gamePkgVersion can not be empty");
            return;
        }

        login(activity, gameId, gameUrl, gamePkgVersion);
    }

    private void login(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed()) {
            return;
        }
        getCode(activity, getUserId(), getAppId(), new BaseRuntime2GameViewModel.GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                initSdk(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailed(int retCode, String retMsg) {
                ToastUtils.showLong("onFailed:(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    protected void getCode(Activity activity, String userId, String appId, BaseRuntime2GameViewModel.GameGetCodeListener listener) {
        LifecycleOwner owner;
        if (activity instanceof LifecycleOwner) {
            owner = (LifecycleOwner) activity;
        } else {
            owner = null;
        }
        // 请求登录code
        GameRepository.login(owner, getAppId(), new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null && !TextUtils.isEmpty(t.getData().runtimeCode)) {
                    listener.onSuccess(t.getData().runtimeCode);
                } else {
                    listener.onFailed(-1, "error:" + t.getRetCode());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onFailed(-1, "error:" + e);
            }
        });
    }

    protected String getUserId() {
        return HSUserInfo.userId + "";
    }

    protected String getAppId() {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            return sudConfig.appId;
        }
        return "";
    }

    protected String getAppKey() {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            return sudConfig.appKey;
        }
        return "";
    }

    private void initSdk(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        EnvUtils.initMgpEnv();
        SudRuntime2InitSDKParamModel initSDKParamModel = new SudRuntime2InitSDKParamModel();
        initSDKParamModel.context = activity.getApplicationContext();
        initSDKParamModel.appId = getAppId();
        initSDKParamModel.appKey = getAppKey();
        initSDKParamModel.userId = getUserId();
        initSDKParamModel.code = code;

        SudRuntime2.initSDK(initSDKParamModel, new ISudRuntime2ListenerInitSDK() {
            @Override
            public void onSuccess() {
                createRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("initSDK fail(" + retCode + ")" + retMsg);
                LogUtils.file("initSDK fail(" + retCode + ")" + retMsg);
                ToastUtils.showLong("initSDK fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLogin(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void createRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudRuntime2InitManager.createRuntime(activity, new SudRuntime2InitManager.CreateRuntimeListener() {
            @Override
            public void onSuccess(SudRuntime2GameRuntime runtime) {
                loadCore(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("createRuntime fail:" + error);
                LogUtils.file("createRuntime fail:" + error);
                delayCreateRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayCreateRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void loadCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudRuntime2InitManager.loadCore(new SudRuntime2InitManager.LoadCoreListener() {
            @Override
            public void onSuccess(SudRuntime2GameCoreHandle coreHandle) {
                loadGame(activity, gameId, gamePkgVersion, gameUrl);
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("loadCore fail:" + tech.sud.logger.LogUtils.getErrorInfo(error));
                LogUtils.file("loadCore fail:" + error);
                delayLoadCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLoadCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void loadGame(Activity activity, String gameId, String gamePkgVersion, String gameUrl) {
        SudRuntime2LoadGameParamModel loadGameParamModel = new SudRuntime2LoadGameParamModel();
        loadGameParamModel.activity = activity;
        loadGameParamModel.userId = getUserId();
        loadGameParamModel.gameId = gameId;
        loadGameParamModel.pkgVersion = gamePkgVersion;
        loadGameParamModel.pkgUrl = gameUrl;
        mISudRuntime2FSTAPP = SudRuntime2.loadPackage(loadGameParamModel, new ISudRuntime2FSMGame() {
            @Override
            public void onSuccess() {
                if (mPreloadListener != null) {
                    mPreloadListener.onSuccess();
                }
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("loadGamePkg fail(" + retCode + ")" + retMsg);
                LogUtils.file("loadGamePkg fail(" + retCode + ")" + retMsg);
                ToastUtils.showLong("loadGamePkg fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }

            /**
             * 游戏加载进度(loadMG)
             * @param stage 阶段：start=1,loading=2,end=3
             * @param retCode 错误码：0成功
             * @param progress 进度：[0, 100]
             */
            @Override
            public void onGameLoadingProgress(int stage, int retCode, int progress) {
            }
        });
    }

    public interface PreloadListener {
        void onSuccess();
    }

}
