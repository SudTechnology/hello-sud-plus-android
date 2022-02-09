package tech.sud.mgp.game.example.viewmodel;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RetCode;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.use.model.SudConfig;
import tech.sud.mgp.common.model.AppConfig;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.common.utils.DensityUtils;
import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.game.example.http.repository.GameRepository;
import tech.sud.mgp.game.example.http.resp.GameLoginResp;
import tech.sud.mgp.game.middle.manager.FsmApp2MgManager;
import tech.sud.mgp.game.middle.model.GameViewInfoModel;
import tech.sud.mgp.game.middle.model.GameViewRectModel;
import tech.sud.mgp.game.middle.model.GameViewSizeModel;
import tech.sud.mgp.game.middle.state.MGStateResponse;

/**
 * 游戏业务逻辑
 */
public class GameViewModel {

    private long roomId; // 房间id
    private long playingGameId; // 当前使用的游戏id
    private final FsmApp2MgManager fsmApp2MGManager = new FsmApp2MgManager(); // app调用sdk的封装类

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调
    public final MutableLiveData<Object> gameStartLiveData = new MutableLiveData<>(); // 游戏开始时的回调
    private View gameView; // 游戏View
    private ISudFSTAPP iSudFSTAPP;

    /**
     * 设置当前房间id
     *
     * @param roomId
     */
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    /**
     * 外部调用切换游戏
     *
     * @param activity
     * @param gameId
     */
    public void switchGame(AppCompatActivity activity, long gameId) {
        if (playingGameId == gameId) {
            return;
        }
        destroyMG();
        playingGameId = gameId;
        gameLogin(activity, gameId);
    }

    /**
     * 1，游戏登录，也就是获取code
     *
     * @param activity
     * @param gameId   游戏id
     */
    private void gameLogin(AppCompatActivity activity, long gameId) {
        if (activity.isDestroyed() || gameId <= 0) {
            return;
        }
        // 请求登录code
        GameRepository.gameLogin(activity, new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    initSdk(activity, gameId, t.getData().code);
                } else {
                    delayLoadGame(activity, gameId);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                delayLoadGame(activity, gameId);
            }
        });
    }

    /**
     * 2，游戏登录后，初始化sdk
     *
     * @param activity
     * @param gameId   游戏id
     * @param code     令牌
     */
    private void initSdk(AppCompatActivity activity, long gameId, String code) {
        SudConfig sudConfig = AppConfig.getInstance().getSudConfig();
        if (sudConfig == null || sudConfig.appId == null || sudConfig.appKey == null) {
            ToastUtils.showLong("SudConfig is empty");
            return;
        }
        // 初始化sdk
        SudMGP.initSDK(activity, sudConfig.appId, sudConfig.appKey, true, new ISudListenerInitSDK() {
            @Override
            public void onSuccess() {
                loadGame(activity, code, gameId);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                delayLoadGame(activity, gameId);
            }
        });
    }

    /**
     * 3，sdk初始化成功后，加载游戏
     *
     * @param activity
     * @param code     登录令牌
     * @param gameId   游戏id
     */
    private void loadGame(AppCompatActivity activity, String code, long gameId) {
        iSudFSTAPP = SudMGP.loadMG(activity, HSUserInfo.userId + "", roomId + "", code, gameId, "zh-CN", iSudFSMMG);
        fsmApp2MGManager.setISudFSTAPP(iSudFSTAPP);
        gameView = iSudFSTAPP.getGameView();
        gameViewLiveData.setValue(gameView);
    }

    /**
     * 游戏加载失败的时候，延迟一会再重新加载
     *
     * @param activity
     * @param gameId   游戏id
     */
    private void delayLoadGame(AppCompatActivity activity, long gameId) {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                gameLogin(activity, gameId);
            }
        }, 3000);
    }

    private final ISudFSMMG iSudFSMMG = new ISudFSMMG() {
        @Override
        public void onGameLog(String s) {

        }

        @Override
        public void onGameStarted() {
            gameStartLiveData.setValue(null);
        }

        @Override
        public void onGameDestroyed() {

        }

        @Override
        public void onExpireCode(ISudFSMStateHandle handle, String dataJson) {
            processOnExpireCode(handle);
        }

        @Override
        public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {
            processOnGetGameViewInfo(handle);
        }

        @Override
        public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
            handle.success("{}");
        }

        @Override
        public void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson) {
        }

        @Override
        public void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson) {
        }
    };

    /**
     * 处理code过期
     */
    private void processOnExpireCode(ISudFSMStateHandle handle) {
        // code过期，刷新code
        GameRepository.gameLogin(null, new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = t.getRetCode();
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    fsmApp2MGManager.updateCode(t.getData().code, null);
                    handle.success(GsonUtils.toJson(mgStateResponse));
                } else {
                    handle.failure(GsonUtils.toJson(mgStateResponse));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = -1;
                handle.failure(GsonUtils.toJson(mgStateResponse));
            }
        });
    }

    /**
     * 处理游戏视图
     */
    private void processOnGetGameViewInfo(ISudFSMStateHandle handle) {
        //拿到游戏View的宽高
        int gameViewWidth = gameView.getMeasuredWidth();
        int gameViewHeight = gameView.getMeasuredHeight();
        if (gameViewWidth > 0 && gameViewHeight > 0) {
            notifyGameViewInfo(handle, gameViewWidth, gameViewHeight);
            return;
        }

        //如果游戏View未加载完成，则监听加载完成时回调
        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = gameView.getMeasuredWidth();
                int height = gameView.getMeasuredHeight();
                notifyGameViewInfo(handle, width, height);
            }
        });
    }

    /**
     * 通知游戏，游戏视图信息
     *
     * @param handle
     * @param gameViewWidth
     * @param gameViewHeight
     */
    private void notifyGameViewInfo(ISudFSMStateHandle handle, int gameViewWidth, int gameViewHeight) {
        GameViewInfoModel gameViewInfoModel = new GameViewInfoModel();
        // 游戏View大小
        gameViewInfoModel.view_size = new GameViewSizeModel();
        gameViewInfoModel.view_size.width = gameViewWidth;
        gameViewInfoModel.view_size.height = gameViewHeight;

        // 游戏安全操作区域
        gameViewInfoModel.view_game_rect = new GameViewRectModel();
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 110);
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 160);

        LogUtils.d("notifyGameViewInfo:" + GsonUtils.toJson(gameViewInfoModel));
        handle.success(GsonUtils.toJson(gameViewInfoModel));
    }

    // region 生命周期相关
    public void onStart() {
        fsmApp2MGManager.onStart();
    }

    public void onPause() {
        fsmApp2MGManager.onPause();
    }

    public void onResume() {
        fsmApp2MGManager.onResume();
    }

    public void onStop() {
        fsmApp2MGManager.onStop();
    }

    public void destroyMG() {
        if (playingGameId > 0) {
            fsmApp2MGManager.destroyMG();
            playingGameId = 0;
            gameView = null;
            gameViewLiveData.setValue(null);
        }
    }
    // endregion 生命周期相关

}
