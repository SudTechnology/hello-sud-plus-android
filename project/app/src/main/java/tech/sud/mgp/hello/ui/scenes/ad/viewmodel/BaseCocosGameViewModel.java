package tech.sud.mgp.hello.ui.scenes.ad.viewmodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.LogUtils;
import com.cocos.game.CocosGameAudioSession;
import com.cocos.game.CocosGameCoreHandle;
import com.cocos.game.CocosGameHandleV2;
import com.cocos.game.CocosGameRuntimeV2;

public abstract class BaseCocosGameViewModel {

    private Activity mActivity;

    private CocosGameRuntimeV2 mRuntime;
    private CocosGameCoreHandle mCoreHandle;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isLoadingGame;
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private String mUserId;
    private CocosGameHandleV2 mGameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = CocosGameHandleV2.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = CocosGameHandleV2.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;

    public MutableLiveData<String> gameMGCommonGameFinishLiveData = new MutableLiveData<>();

    /**
     * 启动游戏
     *
     * @param activity 页面
     * @param gameId   游戏id
     */
    public void startGame(Activity activity, String userId, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl)) {
            LogUtils.d("startGame gameId or gameUrl can not be empty");
            LogUtils.file("startGame gameId or gameUrl can not be empty");
            return;
        }
        mUserId = userId;
        isLoadingGame = true;
        mActivity = activity;
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        initCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
    }

    private void initCocosRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCocosInitManager.initCocosRuntime(activity, new SudCocosInitManager.InitRuntimeListener() {
            @Override
            public void onSuccess(CocosGameRuntimeV2 runtime) {
                if (!isLoadingGame) {
                    return;
                }
                mRuntime = runtime;
                initCocosCore(activity, gameId, gameUrl, gamePkgVersion);
                downloadAndInstallGamePkg(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("initCocosRuntime fail:" + error);
                LogUtils.file("initCocosRuntime fail:" + error);
                delayInitCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void downloadAndInstallGamePkg(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCocosGamePkgManager.downloadAndInstallGamePkg(mRuntime, gameId, gameUrl, gamePkgVersion, new GamePkgListener() {
            @Override
            public void onSuccess(String gameId, String gamePkgVersion) {
                if (!isLoadingGame) {
                    return;
                }
                _isGameInstalled = true;
                _changeGameState(_expectGameState);
            }

            @Override
            public void onFailure(String gameId, String gamePkgVersion, Throwable error) {
                LogUtils.e("downloadAndInstallGamePkg fail:" + error);
                LogUtils.file("downloadAndInstallGamePkg fail:" + error);
            }
        });
    }

    private void delayInitCocosRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoadingGame) {
                    return;
                }
                initCocosRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void initCocosCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        SudCocosInitManager.initCocosCore(new SudCocosInitManager.InitCoreListener() {
            @Override
            public void onSuccess(CocosGameCoreHandle coreHandle) {
                if (!isLoadingGame) {
                    return;
                }
                mCoreHandle = coreHandle;
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
        createOptions.putString(CocosGameHandleV2.KEY_GAME_USER_ID, mUserId);
        mRuntime.createGameHandle(mActivity, createOptions, mCoreHandle, new CocosGameRuntimeV2.GameHandleCreateListener() {
            @Override
            public void onSuccess(CocosGameHandleV2 handle) {
                mGameHandle = handle;
                onAddGameView(mGameHandle.getGameView());
                mGameHandle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                initListener(handle);
                setMute(isMute);
            }

            @Override
            public void onFailure(Throwable error) {
                LogUtils.e("createCocosGameInstance fail:" + error);
                LogUtils.file("createCocosGameInstance fail:" + error);
            }
        });
    }

    private void initListener(CocosGameHandleV2 handle) {
        handle.setCustomCommandListener(new CocosGameHandleV2.CustomCommandListener() {
            @Override
            public void onCallCustomCommand(CocosGameHandleV2.CustomCommandHandle customCommandHandle, Bundle bundle) {
                LogUtils.d("onCallCustomCommand :" + bundle);
                LogUtils.file("onCallCustomCommand :" + bundle);
                onGameStateChange(bundle);
                customCommandHandle.success();
            }

            @Override
            public void onCallCustomCommandSync(CocosGameHandleV2.CustomCommandHandle customCommandHandle, Bundle bundle) {
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
                if (!isLoadingGame) {
                    return;
                }
                initCocosCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    public void onStart() {
        _changeGameState(CocosGameHandleV2.GAME_STATE_RUNNING);
    }

    public void onResume() {
        _changeGameState(CocosGameHandleV2.GAME_STATE_PLAYING);
    }

    public void onPause() {
        _changeGameState(CocosGameHandleV2.GAME_STATE_RUNNING);
    }

    public void onStop() {
        _changeGameState(CocosGameHandleV2.GAME_STATE_WAITING);
    }

    /** 销毁游戏 */
    public void destroyGame() {
        isLoadingGame = false;
        if (mGameHandle != null) {
            mGameHandle.destroy();
        }
        onRemoveGameView();
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

    private final CocosGameHandleV2.GameStateChangeListener _gameStateListener = new CocosGameHandleV2.GameStateChangeListener() {
        @Override
        public void preStateChange(int fromState, int state) {
        }

        @Override
        public void onStateChanged(int fromState, int state) {
            LogUtils.i("onStateChanged:" + state);
            LogUtils.file("onStateChanged:" + state);
            _currentGameState = state;
            _isGameStateChanging = false;
            _changeGameState(_expectGameState);
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
            case CocosGameHandleV2.GAME_STATE_UNAVAILABLE: {
                switch (newState) {
                    case CocosGameHandleV2.GAME_STATE_UNAVAILABLE:
                        break;
                    case CocosGameHandleV2.GAME_STATE_WAITING:
                    case CocosGameHandleV2.GAME_STATE_RUNNING:
                    case CocosGameHandleV2.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.setGameStartOptions(mGameId, null);
                        mGameHandle.create();
                        break;
                }
                break;
            }
            case CocosGameHandleV2.GAME_STATE_WAITING: {
                switch (newState) {
                    case CocosGameHandleV2.GAME_STATE_UNAVAILABLE:
                        _isGameStateChanging = true;
                        mGameHandle.destroy();
                        break;
                    case CocosGameHandleV2.GAME_STATE_WAITING:
                        break;
                    case CocosGameHandleV2.GAME_STATE_RUNNING:
                    case CocosGameHandleV2.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.start(null);
                        break;
                }
                break;
            }
            case CocosGameHandleV2.GAME_STATE_RUNNING: {
                switch (newState) {
                    case CocosGameHandleV2.GAME_STATE_UNAVAILABLE:
                    case CocosGameHandleV2.GAME_STATE_WAITING:
                        _isGameStateChanging = true;
                        mGameHandle.stop(null);
                        break;
                    case CocosGameHandleV2.GAME_STATE_RUNNING:
                        break;
                    case CocosGameHandleV2.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.play();
                        break;
                }
                break;
            }
            case CocosGameHandleV2.GAME_STATE_PLAYING: {
                switch (newState) {
                    case CocosGameHandleV2.GAME_STATE_UNAVAILABLE:
                    case CocosGameHandleV2.GAME_STATE_WAITING:
                    case CocosGameHandleV2.GAME_STATE_RUNNING:
                        _isGameStateChanging = true;
                        mGameHandle.pause();
                        break;
                    case CocosGameHandleV2.GAME_STATE_PLAYING:
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
            CocosGameAudioSession gameAudioSession = mGameHandle.getGameAudioSession();
            if (gameAudioSession != null) {
                gameAudioSession.mute(isMute);
            }
        }
    }

}
