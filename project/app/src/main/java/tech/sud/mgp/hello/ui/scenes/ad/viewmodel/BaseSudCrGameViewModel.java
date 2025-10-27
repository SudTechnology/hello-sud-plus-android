package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.cr.core.ISudCrFSMGame;
import tech.sud.cr.core.ISudCrListenerInitSDK;
import tech.sud.cr.core.SudCr;
import tech.sud.cr.core.SudCrGameAudioSession;
import tech.sud.cr.core.SudCrGameCoreHandle;
import tech.sud.cr.core.SudCrGameHandle;
import tech.sud.cr.core.SudCrGameRuntime;
import tech.sud.cr.core.SudCrInitSDKParamModel;
import tech.sud.cr.core.SudCrLoadGameParamModel;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnvUtils;

public abstract class BaseSudCrGameViewModel {

    private FragmentActivity mActivity;
    public String CALC_TAG = "";
    private SudCrGameRuntime mRuntime;
    private SudCrGameCoreHandle mCoreHandle;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private String mUserId;
    private SudCrGameHandle mGameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;
    private boolean isGameStarted;
    private AudioManager _audioManager;
    private long startGameTimestamp;

    public MutableLiveData<String> gameMGCommonGameFinishLiveData = new MutableLiveData<>();
    public MutableLiveData<String> gameStartedLiveData = new MutableLiveData<>();

    /**
     * 启动游戏
     *
     * @param activity 页面
     * @param gameId   游戏id
     */
    public void startGame(FragmentActivity activity, String userId, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl)) {
            LogUtils.d("startGame gameId or gameUrl can not be empty");
            LogUtils.file("startGame gameId or gameUrl can not be empty");
            return;
        }
        if (gameId.equals(mGameId)) {
            LogUtils.d("重复的gameId加载");
            LogUtils.file("重复的gameId加载");
            return;
        }

        startGameTimestamp = System.currentTimeMillis();
        LogUtils.d(CALC_TAG + "开始加载游戏 gameId:" + gameId);
        if (_audioManager == null) {
            _audioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        mUserId = userId;
        mActivity = activity;
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        initCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
        onStart();
    }

    private void initCocosRuntime(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCrInitManager.initRuntime(activity, new SudCrInitManager.InitRuntimeListener() {
            @Override
            public void onSuccess(SudCrGameRuntime runtime) {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                mRuntime = runtime;
                initCocosCore(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("initCocosRuntime fail:" + error);
                LogUtils.file("initCocosRuntime fail:" + error);
                delayInitCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void login(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed() || TextUtils.isEmpty(mGameId)) {
            return;
        }
        // 请求登录code
        GameRepository.login(activity, getAppId(), new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (!gameId.equals(mGameId)) {
                    return;
                }
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    LogUtils.d("code为：" + t.getData().runtimeCode);
                    initSdk(activity, gameId, gameUrl, gamePkgVersion, t.getData().runtimeCode);
                } else {
                    delayLogin(activity, gameId, gameUrl, gamePkgVersion);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLogin(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void initSdk(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig == null || sudConfig.appId == null || sudConfig.appKey == null) {
            ToastUtils.showLong("SudConfig is empty");
            return;
        }
        EnvUtils.initMgpEnv();
        SudCrInitSDKParamModel initSDKParamModel = new SudCrInitSDKParamModel();
        initSDKParamModel.context = activity.getApplicationContext();
        initSDKParamModel.appId = sudConfig.appId;
        initSDKParamModel.appKey = sudConfig.appKey;
        initSDKParamModel.isTestEnv = APPConfig.GAME_IS_TEST_ENV;
        initSDKParamModel.userId = getUserId();
        initSDKParamModel.code = code;

        SudCr.initSDK(initSDKParamModel, new ISudCrListenerInitSDK() {
            @Override
            public void onSuccess() {
                loadGame(activity, gameId, gamePkgVersion, gameUrl);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("loadGamePkg fail(" + retCode + ")" + retMsg);
                LogUtils.file("loadGamePkg fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void loadGame(FragmentActivity activity, String gameId, String gamePkgVersion, String gameUrl) {
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        SudCrLoadGameParamModel loadGameParamModel = new SudCrLoadGameParamModel();
        loadGameParamModel.activity = activity;
        loadGameParamModel.userId = getUserId();
        loadGameParamModel.gameId = gameId;
        loadGameParamModel.pkgVersion = gamePkgVersion;
        loadGameParamModel.pkgUrl = gameUrl;
        SudCr.loadGame(loadGameParamModel, new ISudCrFSMGame() {
            @Override
            public void onSuccess() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                _isGameInstalled = true;
                _changeGameState(_expectGameState);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("loadGamePkg fail(" + retCode + ")" + retMsg);
                LogUtils.file("loadGamePkg fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayInitCocosRuntime(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                initCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void initCocosCore(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCrInitManager.initCocosCore(new SudCrInitManager.InitCoreListener() {
            @Override
            public void onSuccess(SudCrGameCoreHandle coreHandle) {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                mCoreHandle = coreHandle;
                login(activity, gameId, gameUrl, gamePkgVersion);
                createCocosGameInstance();
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("initCocos fail:" + error);
                LogUtils.file("initCocos fail:" + error);
                delayInitCocosCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void createCocosGameInstance() {
        // 创建游戏实例
        Bundle createOptions = new Bundle();
        createOptions.putString(SudCrGameHandle.KEY_GAME_USER_ID, mUserId);
        long start = System.currentTimeMillis();
        LogUtils.d(CALC_TAG + "创建GameHandle 开始 gameId:" + mGameId);
        mRuntime.createGameHandle(mActivity, createOptions, mCoreHandle, new SudCrGameRuntime.GameHandleCreateListener() {
            @Override
            public void onSuccess(SudCrGameHandle handle) {
                LogUtils.d(CALC_TAG + "创建GameHandle 成功 gameId：" + mGameId + " 耗时：" + (System.currentTimeMillis() - start));
                mGameHandle = handle;
                onAddGameView(mGameHandle.getGameView());
                mGameHandle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                initListener(handle);
                setMute(isMute);
                handle.getGameAudioSession().setGameQueryAudioOptionsListener(_audioListener);
                mGameHandle.setGameDrawFrameListener(new SudCrGameHandle.GameDrawFrameListener() {
                    @Override
                    public void onDrawFrame(long l) {
                        mGameHandle.setGameDrawFrameListener(null);
                        LogUtils.d(CALC_TAG + "第一帧来了 gameId：" + mGameId + " 从开始加载到第一帧的耗时：" + (System.currentTimeMillis() - startGameTimestamp));
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.d(CALC_TAG + "创建GameHandle 失败 gameId：" + mGameId + " 耗时：" + (System.currentTimeMillis() - start));

                LogUtils.e("createCocosGameInstance fail:" + error);
                LogUtils.file("createCocosGameInstance fail:" + error);
            }
        });
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // 不自动暂停音频
        }
    };

    private final SudCrGameAudioSession.GameQueryAudioOptionsListener _audioListener = new SudCrGameAudioSession.GameQueryAudioOptionsListener() {
        @Override
        public void onQueryAudioOptions(SudCrGameAudioSession.GameQueryAudioOptionsHandle gameQueryAudioOptionsHandle, Bundle bundle) {
            // bundle 中参数
            // bundle.getBoolean(CocosGameAudioSession.KEY_AUDIO_MIX_WITH_OTHER); 是否用扬声器播放，true 默认输出设备优先级：耳机 > 蓝牙 > 扬声器；false 用听筒播放
            // bundle.getBoolean(CocosGameAudioSession.KEY_AUDIO_SPEAKER_ON); 音频是否支持与其他音频混播（包含其他应用、其他游戏实例的音频）
            _audioManager.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            );
        }
    };

    private void initListener(SudCrGameHandle handle) {
        handle.setCustomCommandListener(new SudCrGameHandle.CustomCommandListener() {
            @Override
            public void onCallCustomCommand(SudCrGameHandle.CustomCommandHandle customCommandHandle, Bundle bundle) {
                LogUtils.d("onCallCustomCommand :" + bundle);
                LogUtils.file("onCallCustomCommand :" + bundle);
                onGameStateChange(bundle);
                customCommandHandle.success();
            }

            @Override
            public void onCallCustomCommandSync(SudCrGameHandle.CustomCommandHandle customCommandHandle, Bundle bundle) {
                LogUtils.d("onCallCustomCommandSync :" + bundle);
                LogUtils.file("onCallCustomCommandSync :" + bundle);
            }
        });
    }

    private void onGameStateChange(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String state = bundle.getString("0");
        String dataJson = bundle.getString("1");
        if ("mg_common_game_finish".equals(state)) {
            onGameMGCommonGameFinish(dataJson);
        }
    }

    private void onGameMGCommonGameFinish(String dataJson) {
        gameMGCommonGameFinishLiveData.setValue(dataJson);
    }

    private void delayInitCocosCore(FragmentActivity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                initCocosCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    public void onStart() {
        _changeGameState(SudCrGameHandle.GAME_STATE_RUNNING);
    }

    public void onResume() {
        _changeGameState(SudCrGameHandle.GAME_STATE_PLAYING);
    }

    public void onPause() {
        _changeGameState(SudCrGameHandle.GAME_STATE_RUNNING);
    }

    public void onStop() {
        _changeGameState(SudCrGameHandle.GAME_STATE_WAITING);
    }

    /** 销毁游戏 */
    public void destroyGame() {
        LogUtils.d(CALC_TAG + "调用destroy gameId:" + mGameId);
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        if (mGameHandle != null) {
            mGameHandle.destroy();
        }
        onRemoveGameView();
        mGameId = null;
        mGameUrl = null;
        mGamePkgVersion = null;
        mUserId = null;
        mGameHandle = null;
        _isGameStateChanging = false;
        _currentGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
        _expectGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
        _isGameInstalled = false;
        isMute = false;
        isGameStarted = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 将游戏View添加到页面中
     * Add the game view to the page.
     */
    protected abstract void onAddGameView(View gameView);

    /**
     * 将页面中的游戏View移除
     * Remove the game view from the page.
     */
    protected abstract void onRemoveGameView();

    private final SudCrGameHandle.GameStateChangeListener _gameStateListener = new SudCrGameHandle.GameStateChangeListener() {
        @Override
        public void preStateChange(int fromState, int state) {
        }

        @Override
        public void onStateChanged(int fromState, int state) {
            LogUtils.d(CALC_TAG + "状态变化 gameId:" + mGameId + " 状态为：" + getStringState(state));
            if (!isGameStarted && state == SudCrGameHandle.GAME_STATE_PLAYING) {
                isGameStarted = true;
                gameStartedLiveData.setValue(null);
            }
            LogUtils.i("onStateChanged:" + state);
            LogUtils.file("onStateChanged:" + state);
            _currentGameState = state;
            _isGameStateChanging = false;
            _changeGameState(_expectGameState);
        }

        private String getStringState(int state) {
            switch (state) {
                case SudCrGameHandle.GAME_STATE_UNAVAILABLE:
                    return "UNAVAILABLE";
                case SudCrGameHandle.GAME_STATE_WAITING:
                    return "WAITING";
                case SudCrGameHandle.GAME_STATE_RUNNING:
                    return "RUNNING";
                case SudCrGameHandle.GAME_STATE_PLAYING:
                    return "PLAYING";
                default:
                    return "UNKNOW:" + state;
            }
        }

        @Override
        public void onFailure(int fromState, int toState, Throwable error) {
            LogUtils.e("game state change failed:" + " from=" + fromState + " to=" + toState + " error=" + error.getMessage());
            LogUtils.file("game state change failed:" + " from=" + fromState + " to=" + toState + " error=" + error.getMessage());
        }
    };

    private void _changeGameState(int newState) {
        _expectGameState = newState;
        if (mGameHandle == null || !_isGameInstalled || _isGameStateChanging) {
            return;
        }
        LogUtils.i("_changeGameState success: _currentGameState " + _currentGameState + " to " + newState);
        LogUtils.file("_changeGameState success: _currentGameState " + _currentGameState + " to " + newState);
        switch (_currentGameState) {
            case SudCrGameHandle.GAME_STATE_UNAVAILABLE: {
                switch (newState) {
                    case SudCrGameHandle.GAME_STATE_UNAVAILABLE:
                        break;
                    case SudCrGameHandle.GAME_STATE_WAITING:
                    case SudCrGameHandle.GAME_STATE_RUNNING:
                    case SudCrGameHandle.GAME_STATE_PLAYING:
                        LogUtils.d(CALC_TAG + "调用SudCrGameHandle.create() gameId:" + mGameId);
                        _isGameStateChanging = true;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SudCrGameHandle.KEY_GAME_START_OPTIONS_ENABLE_THIRD_SCRIPT, true);
                        mGameHandle.setGameStartOptions(mGameId, bundle);
                        mGameHandle.create();
                        break;
                }
                break;
            }
            case SudCrGameHandle.GAME_STATE_WAITING: {
                switch (newState) {
                    case SudCrGameHandle.GAME_STATE_UNAVAILABLE:
                        _isGameStateChanging = true;
                        mGameHandle.destroy();
                        break;
                    case SudCrGameHandle.GAME_STATE_WAITING:
                        break;
                    case SudCrGameHandle.GAME_STATE_RUNNING:
                    case SudCrGameHandle.GAME_STATE_PLAYING:
                        LogUtils.d(CALC_TAG + "调用SudCrGameHandle.start() gameId:" + mGameId);
                        _isGameStateChanging = true;
                        mGameHandle.start(null);
                        break;
                }
                break;
            }
            case SudCrGameHandle.GAME_STATE_RUNNING: {
                switch (newState) {
                    case SudCrGameHandle.GAME_STATE_UNAVAILABLE:
                    case SudCrGameHandle.GAME_STATE_WAITING:
                        _isGameStateChanging = true;
                        mGameHandle.stop(null);
                        break;
                    case SudCrGameHandle.GAME_STATE_RUNNING:
                        break;
                    case SudCrGameHandle.GAME_STATE_PLAYING:
                        LogUtils.d(CALC_TAG + "调用SudCrGameHandle.play() gameId:" + mGameId);
                        _isGameStateChanging = true;
                        mGameHandle.play();
                        break;
                }
                break;
            }
            case SudCrGameHandle.GAME_STATE_PLAYING: {
                switch (newState) {
                    case SudCrGameHandle.GAME_STATE_UNAVAILABLE:
                    case SudCrGameHandle.GAME_STATE_WAITING:
                    case SudCrGameHandle.GAME_STATE_RUNNING:
                        LogUtils.d(CALC_TAG + "调用SudCrGameHandle.pause() gameId:" + mGameId);
                        _isGameStateChanging = true;
                        mGameHandle.pause();
                        break;
                    case SudCrGameHandle.GAME_STATE_PLAYING:
                        break;
                }
                break;
            }
            default: {
                LogUtils.e("_changeGameState fail: _currentGameState " + _currentGameState + " to " + newState);
            }
        }
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
        if (mGameHandle != null) {
            SudCrGameAudioSession gameAudioSession = mGameHandle.getGameAudioSession();
            if (gameAudioSession != null) {
                gameAudioSession.mute(isMute);
            }
        }
    }

    private String getAppId() {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
        if (sudConfig != null) {
            return sudConfig.appId;
        }
        return "";
    }

    private String getUserId() {
        return HSUserInfo.userId + "";
    }
}
