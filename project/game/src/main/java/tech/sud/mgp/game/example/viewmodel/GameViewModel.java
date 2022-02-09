package tech.sud.mgp.game.example.viewmodel;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RetCode;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.use.model.SudConfig;
import tech.sud.mgp.common.model.AppConfig;
import tech.sud.mgp.common.model.HSUserInfo;
import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.game.example.http.repository.GameRepository;
import tech.sud.mgp.game.example.http.resp.GameLoginResp;
import tech.sud.mgp.game.middle.state.manager.FsmApp2MgManager;

/**
 * 游戏业务逻辑
 */
public class GameViewModel {

    private long roomId;
    private final FsmApp2MgManager fsmapp2MGManager = new FsmApp2MgManager();

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调
    public final MutableLiveData<Object> gameStartLiveData = new MutableLiveData<>(); // 游戏开始时的回调

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void loadGame(AppCompatActivity activity, long gameId) {
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

    private void delayLoadGame(AppCompatActivity activity, long gameId) {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                loadGame(activity, gameId);
            }
        }, 3000);
    }

    private void initSdk(AppCompatActivity activity, long gameId, String code) {
        SudConfig sudConfig = AppConfig.getInstance().getSudConfig();
        if (sudConfig == null || sudConfig.appId == null || sudConfig.appKey == null) {
            ToastUtils.showLong("SudConfig is empty");
            return;
        }
        sudConfig.appId = "1486637108889305089";
        sudConfig.appKey = "wVC9gUtJNIDzAqOjIVdIHqU3MY6zF6SR";
        // 初始化sdk
        SudMGP.initSDK(activity, sudConfig.appId, sudConfig.appKey, true, new ISudListenerInitSDK() {
            @Override
            public void onSuccess() {
                // 加载游戏
                ISudFSTAPP iSudFSTAPP = SudMGP.loadMG(activity, HSUserInfo.userId + "", roomId + "", code, gameId, "zh-cn", iSudFSMMG);
                fsmapp2MGManager.setISudFSTAPP(iSudFSTAPP);
                gameViewLiveData.setValue(iSudFSTAPP.getGameView());
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                delayLoadGame(activity, gameId);
            }
        });
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

        }

        @Override
        public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {

        }

        @Override
        public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {

        }

        @Override
        public void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson) {

        }

        @Override
        public void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson) {

        }
    };


}
