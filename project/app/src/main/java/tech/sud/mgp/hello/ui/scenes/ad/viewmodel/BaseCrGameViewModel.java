package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.LogUtils;

import tech.sud.cr.core.ISudCrFSMGame;
import tech.sud.cr.core.ISudCrListenerInitSDK;
import tech.sud.cr.core.SudCr;
import tech.sud.cr.core.SudCrGameAudioSession;
import tech.sud.cr.core.SudCrGameCoreHandle;
import tech.sud.cr.core.SudCrGameHandle;
import tech.sud.cr.core.SudCrGameRuntime;
import tech.sud.cr.core.SudCrInitSDKParamModel;
import tech.sud.cr.core.SudCrLoadGameParamModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameLoadingProgressModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnvUtils;

public abstract class BaseCrGameViewModel {

    private Activity mActivity;
    public String CALC_TAG = "";
    private SudCrGameRuntime mRuntime;
    private SudCrGameCoreHandle mCoreHandle;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private SudCrGameHandle mGameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;
    private boolean isGameStarted;
    private AudioManager _audioManager;
    private long startGameTimestamp;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    public MutableLiveData<String> gameMGCommonGameFinishLiveData = new MutableLiveData<>();
    public MutableLiveData<String> gameStartedLiveData = new MutableLiveData<>();
    public final MutableLiveData<GameLoadingProgressModel> gameLoadingProgressLiveData = new MutableLiveData<>(); // 游戏加载进度回调

    /**
     * 启动游戏
     *
     * @param activity 页面
     * @param gameId   游戏id
     */
    public void startGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl)) {
            LogUtils.d("startGame gameId or gameUrl can not be empty");
            LogUtils.file("startGame gameId or gameUrl can not be empty");
            return;
        }
        if (!TextUtils.isEmpty(mGameId)) {
            LogUtils.d("已经存在加载中的游戏:" + mGameId);
            LogUtils.file("已经存在加载中的游戏:" + mGameId);
            return;
        }

        startGameTimestamp = System.currentTimeMillis();
        LogUtils.d(CALC_TAG + "开始加载游戏 gameId:" + gameId);
        if (_audioManager == null) {
            _audioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        mActivity = activity;
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        initCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
        onResume();
    }

    private void initCocosRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCrInitManager.createRuntime(activity, new SudCrInitManager.CreateRuntimeListener() {
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

    private void login(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed() || TextUtils.isEmpty(mGameId)) {
            return;
        }
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!gameId.equals(mGameId)) {
                    return;
                }
                initSdk(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailed() {
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLogin(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
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

    private void initSdk(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        EnvUtils.initMgpEnv();
        SudCrInitSDKParamModel initSDKParamModel = new SudCrInitSDKParamModel();
        initSDKParamModel.context = activity.getApplicationContext();
        initSDKParamModel.appId = getAppId();
        initSDKParamModel.appKey = getAppKey();
        initSDKParamModel.isTestEnv = isTestEnv();
        initSDKParamModel.userId = getUserId();
        initSDKParamModel.code = code;

        SudCr.initSDK(initSDKParamModel, new ISudCrListenerInitSDK() {
            @Override
            public void onSuccess() {
                loadGame(activity, gameId, gamePkgVersion, gameUrl);
                createCocosGameInstance();
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LogUtils.e("loadGamePkg fail(" + retCode + ")" + retMsg);
                LogUtils.file("loadGamePkg fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void loadGame(Activity activity, String gameId, String gamePkgVersion, String gameUrl) {
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

            /**
             * 游戏加载进度(loadMG)
             * @param stage 阶段：start=1,loading=2,end=3
             * @param retCode 错误码：0成功
             * @param progress 进度：[0, 100]
             */
            @Override
            public void onGameLoadingProgress(int stage, int retCode, int progress) {
                gameLoadingProgressLiveData.setValue(new GameLoadingProgressModel(stage, retCode, progress));
            }
        });
    }

    private void delayInitCocosRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
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

    private void initCocosCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCrInitManager.loadCore(new SudCrInitManager.LoadCoreListener() {
            @Override
            public void onSuccess(SudCrGameCoreHandle coreHandle) {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                mCoreHandle = coreHandle;
                login(activity, gameId, gameUrl, gamePkgVersion);
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
        if (mGameHandle != null) {
            return;
        }
        // 创建游戏实例
        Bundle createOptions = new Bundle();
        createOptions.putString(SudCrGameHandle.KEY_GAME_USER_ID, getUserId());
        long start = System.currentTimeMillis();
        LogUtils.d(CALC_TAG + "创建GameHandle 开始 gameId:" + mGameId);
        mRuntime.createGameHandle(mActivity, createOptions, mCoreHandle, new SudCrGameRuntime.GameHandleCreateListener() {
            @Override
            public void onSuccess(SudCrGameHandle handle) {
                LogUtils.d(CALC_TAG + "创建GameHandle 成功 gameId：" + mGameId + " 耗时：" + (System.currentTimeMillis() - start));
                mGameHandle = handle;
                onAddGameView(handle.getGameView());
                handle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                initListener(handle);
                setMute(isMute);
                handle.getGameAudioSession().setGameQueryAudioOptionsListener(_audioListener);
                handle.setGameDrawFrameListener(new SudCrGameHandle.GameDrawFrameListener() {
                    @Override
                    public void onDrawFrame(long l) {
                        handle.setGameDrawFrameListener(null);
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

    private final SudCrGameAudioSession.GameQueryAudioOptionsListener _audioListener = new SudCrGameAudioSession.GameQueryAudioOptionsListener() {
        @Override
        public void onQueryAudioOptions(SudCrGameAudioSession.GameQueryAudioOptionsHandle gameQueryAudioOptionsHandle, Bundle bundle) {
            // bundle 中参数
            // bundle.getBoolean(CocosGameAudioSession.KEY_AUDIO_MIX_WITH_OTHER); 是否用扬声器播放，true 默认输出设备优先级：耳机 > 蓝牙 > 扬声器；false 用听筒播放
            // bundle.getBoolean(CocosGameAudioSession.KEY_AUDIO_SPEAKER_ON); 音频是否支持与其他音频混播（包含其他应用、其他游戏实例的音频）
            if (afChangeListener == null) {
                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        // 不自动暂停音频
                    }
                };
                _audioManager.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                );
            }
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

    private void delayInitCocosCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
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
        mGameHandle = null;
        _isGameStateChanging = false;
        _currentGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
        _expectGameState = SudCrGameHandle.GAME_STATE_UNAVAILABLE;
        _isGameInstalled = false;
        isMute = false;
        isGameStarted = false;
        mHandler.removeCallbacksAndMessages(null);
        if (afChangeListener != null) {
            _audioManager.abandonAudioFocus(afChangeListener);
            afChangeListener = null;
        }
    }

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

    /**
     * 向接入方服务器获取code
     * Get the code from the integration party server.
     */
    protected abstract void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener);

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the integration party).
     *
     * @return 返回用户id
     * Returns the user ID.
     */
    protected abstract String getUserId();

    /**
     * 设置游戏所用的appId
     * Set the appId used by the game.
     *
     * @return 返回游戏服务appId
     * Returns the game service appId.
     */
    protected abstract String getAppId();

    /**
     * 设置游戏所用的appKey
     * Set the appKey used by the game.
     *
     * @return 返回游戏服务appKey
     * Returns the game service appKey.
     */
    protected abstract String getAppKey();

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

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     * <p>
     * true for the testing environment when loading the game.
     * false for the production environment when loading the game.
     */
    protected abstract boolean isTestEnv();

    /**
     * 游戏login(getCode)监听
     * Game login (getCode) listener
     */
    public interface GameGetCodeListener {
        void onSuccess(String code);

        void onFailed();
    }

}
