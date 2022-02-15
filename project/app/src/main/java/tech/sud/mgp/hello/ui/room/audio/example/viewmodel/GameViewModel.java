package tech.sud.mgp.hello.ui.room.audio.example.viewmodel;

import static tech.sud.mgp.hello.ui.game.middle.state.SudMGPMGState.MG_COMMON_GAME_ASR;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.HashMap;

import tech.sud.mgp.core.ISudFSMMG;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppConfig;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.rtc.protocol.AudioData;
import tech.sud.mgp.hello.ui.game.http.repository.GameRepository;
import tech.sud.mgp.hello.ui.game.http.resp.GameLoginResp;
import tech.sud.mgp.hello.ui.game.middle.manager.FsmApp2MgManager;
import tech.sud.mgp.hello.ui.game.middle.manager.SudFSMMGManager;
import tech.sud.mgp.hello.ui.game.middle.model.GameMessageModel;
import tech.sud.mgp.hello.ui.game.middle.state.MGStateResponse;
import tech.sud.mgp.hello.ui.game.middle.state.SudMGPMGState;
import tech.sud.mgp.hello.ui.game.middle.state.mg.common.CommonGameState;
import tech.sud.mgp.hello.ui.game.middle.state.mg.player.PlayerCaptainState;
import tech.sud.mgp.hello.ui.game.middle.state.mg.player.PlayerInState;
import tech.sud.mgp.hello.ui.game.middle.state.mg.player.PlayerReadyState;
import tech.sud.mgp.hello.ui.game.middle.utils.GameCommonStateUtils;
import tech.sud.mgp.hello.ui.main.http.model.SudConfig;
import tech.sud.mgp.hello.ui.room.audio.example.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.room.audio.example.utils.HSJsonUtils;

/**
 * 游戏业务逻辑
 */
public class GameViewModel {

    private long roomId; // 房间id
    private long playingGameId; // 当前使用的游戏id
    private final FsmApp2MgManager fsmApp2MGManager = new FsmApp2MgManager(); // app调用sdk的封装类
    private final SudFSMMGManager sudFSMMGManager = new SudFSMMGManager(); // 用于处理游戏SDK部分回调业务

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调
    public final MutableLiveData<Object> gameStartLiveData = new MutableLiveData<>(); // 游戏开始时的回调
    public final MutableLiveData<GameMessageModel> gameMessageLiveData = new MutableLiveData<>(); // 游戏消息
    public final MutableLiveData<Object> updateMicLiveData = new MutableLiveData<>(); // 刷新麦位通知
    public final MutableLiveData<Object> autoUpMicLiveData = new MutableLiveData<>(); // 执行自动上麦的通知
    public final MutableLiveData<String> gameKeywordLiveData = new MutableLiveData<>();//游戏关键字
    public final MutableLiveData<Boolean> gameASRLiveData = new MutableLiveData<>();//游戏ASR开关

    private View gameView; // 游戏View
    private long captainUserId; // 记录当前队长的用户id
    private CommonGameState commonGameState; // 游戏状态
    private boolean isSelfInGame; // 标识自己是否已加入了游戏
    private int selfMicIndex = -1; // 记录自己所在麦位

    /**
     * 记录该玩家最新的游戏状态
     * 1，准备状态 {@link PlayerReadyState}
     */
    private final HashMap<String, Object> playerStates = new HashMap<>();

    /**
     * 外部调用切换游戏
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    public void switchGame(FragmentActivity activity, long gameId) {
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
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    private void gameLogin(FragmentActivity activity, long gameId) {
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
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     * @param code     令牌
     */
    private void initSdk(FragmentActivity activity, long gameId, String code) {
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
     * @param activity 游戏所在页面
     * @param code     登录令牌
     * @param gameId   游戏id
     */
    private void loadGame(Activity activity, String code, long gameId) {
        ISudFSTAPP iSudFSTAPP = SudMGP.loadMG(activity, HSUserInfo.userId + "", roomId + "", code, gameId, "zh-CN", iSudFSMMG);
        fsmApp2MGManager.setISudFSTAPP(iSudFSTAPP);
        gameView = iSudFSTAPP.getGameView();
        gameViewLiveData.setValue(gameView);
    }

    /**
     * 游戏加载失败的时候，延迟一会再重新加载
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    private void delayLoadGame(FragmentActivity activity, long gameId) {
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                gameLogin(activity, gameId);
            }
        }, 3000);
    }

    // 覆盖更新玩家状态
    private void putPlayerState(String userId, Object state) {
        playerStates.put(userId, state);
    }

    // 移除玩家状态
    private void removePlayerState(String userId) {
        playerStates.remove(userId);
    }

    // 获取该玩家状态
    private Object getPlayerState(String userId) {
        return playerStates.get(userId);
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
            sudFSMMGManager.processOnExpireCode(fsmApp2MGManager, handle);
        }

        @Override
        public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {
            sudFSMMGManager.processOnGetGameViewInfo(gameView, handle);
        }

        @Override
        public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
            sudFSMMGManager.processOnGetGameCfg(handle, dataJson);
        }

        @Override
        public void onGameStateChange(ISudFSMStateHandle handle, String state, String dataJson) {
            MGStateResponse response = new MGStateResponse();
            response.ret_code = 0;
            handle.success(GsonUtils.toJson(response));
            parseCommonState(state, dataJson);
        }

        @Override
        public void onPlayerStateChange(ISudFSMStateHandle handle, String userId, String state, String dataJson) {
            MGStateResponse response = new MGStateResponse();
            response.ret_code = 0;
            handle.success(GsonUtils.toJson(response));
            parsePlayerState(userId, state, dataJson);
            LogUtils.d("onPlayerStateChange:" + state + "---:" + userId + "---:" + dataJson);
        }
    };

    // 解析游戏侧发送的玩家状态
    private void parsePlayerState(String userId, String state, String dataJson) {
        switch (state) {
            case SudMGPMGState.MG_COMMON_PLAYER_CAPTAIN: // 队长状态
                PlayerCaptainState playerCaptainState = HSJsonUtils.fromJson(dataJson, PlayerCaptainState.class);
                if (playerCaptainState != null && playerCaptainState.retCode == 0) {
                    if (playerCaptainState.isCaptain) { // 该用户成为了队长
                        captainChange(userId);
                    } else {
                        if ((captainUserId + "").equals(userId)) { // 当前队长变为了非队长
                            captainChange(null);
                        }
                    }
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_READY: // 准备状态
                PlayerReadyState playerReadyState = HSJsonUtils.fromJson(dataJson, PlayerReadyState.class);
                if (playerReadyState != null && playerReadyState.retCode == 0) {
                    putPlayerState(userId, playerReadyState);
                    notifyUpdateMic();
                }
                break;
            case SudMGPMGState.MG_COMMON_PLAYER_IN: // 加入状态
                PlayerInState playerInState = HSJsonUtils.fromJson(dataJson, PlayerInState.class);
                if (playerInState != null) {
                    if ((HSUserInfo.userId + "").equals(userId)) { // 属于自己的变动
                        isSelfInGame = playerInState.isIn;
                        if (playerInState.isIn) {
                            autoUpMicLiveData.setValue(null);
                        }
                    }
                    if (playerInState.isIn) {
                        putPlayerState(userId, playerInState);
                    } else {
                        removePlayerState(userId);
                        notifyUpdateMic();
                    }
                }
                break;
        }
    }

    // 通知更新麦位
    private void notifyUpdateMic() {
        updateMicLiveData.setValue(null);
    }

    /**
     * 队长变化了
     *
     * @param userId 队长用户id
     */
    private void captainChange(String userId) {
        try {
            if (TextUtils.isEmpty(userId)) {
                captainUserId = 0;
            } else {
                captainUserId = Long.parseLong(userId);
            }
            notifyUpdateMic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析游戏侧发送的通用状态
     */
    private void parseCommonState(String state, String dataJson) {
        switch (state) {
            case SudMGPMGState.MG_COMMON_PUBLIC_MESSAGE: // 公屏消息
                GameMessageModel model = GameCommonStateUtils.parseMsgState(dataJson);
                gameMessageLiveData.setValue(model);
                break;
            case SudMGPMGState.MG_COMMON_GAME_STATE: // 游戏状态
                commonGameState = HSJsonUtils.fromJson(dataJson, CommonGameState.class);
                break;
            case SudMGPMGState.MG_COMMON_KEY_WORD_TO_HIT: //关键字
                String word = GameCommonStateUtils.parseKeywordState(dataJson);
                gameKeywordLiveData.setValue(word);
                break;
            case SudMGPMGState.MG_COMMON_GAME_ASR://开关
                boolean isOpen = GameCommonStateUtils.parseASRState(dataJson);
                gameASRLiveData.setValue(isOpen);
                break;
        }
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
            captainUserId = 0;
            commonGameState = null;
        }
    }
    // endregion 生命周期相关

    /**
     * 设置当前房间id
     */
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    /**
     * 返回当前游戏的状态，数值参数{@link CommonGameState}
     */
    public int getGameState() {
        if (commonGameState != null) {
            return commonGameState.gameState;
        }
        return CommonGameState.IDLE;
    }

    /**
     * 对麦位数据模型进行赋值游戏相关的状态
     *
     * @param model 麦位数据模型
     */
    public void wrapMicModel(AudioRoomMicModel model) {
        boolean hasUser = model.userId > 0;
        // 是否是游戏中的队长
        if (hasUser && model.userId == captainUserId) {
            model.isCaptain = true;
        } else {
            model.isCaptain = false;
        }

        // 是否已准备
        if (hasUser) {
            Object playerState = getPlayerState(model.userId + "");
            if (playerState instanceof PlayerReadyState) { // 准备状态
                PlayerReadyState readyState = (PlayerReadyState) playerState;
                if (readyState.isReady) { // 已准备
                    model.readyStatus = 1;
                } else { // 未准备
                    model.readyStatus = 2;
                }
            } else if (playerState instanceof PlayerInState) { // 加入状态
                PlayerInState playerInState = (PlayerInState) playerState;
                if (playerInState.isIn) { // 加入了，但是没有准备
                    model.readyStatus = 2;
                } else { // 未加入，不显示准备状态
                    model.readyStatus = 0;
                }
            } else {
                model.readyStatus = 0;
            }
        } else {
            model.readyStatus = 0;
        }

    }

    /**
     * 当前自己所在的麦位
     *
     * @param micIndex 麦位索引
     */
    public void selfMicIndex(int micIndex) {
        if (micIndex >= 0 && selfMicIndex != micIndex) { // 可选项。这里展示的逻辑是：上麦自动加入游戏。根据需求选择是否要执行此逻辑。
            joinGame(micIndex);
        }
        selfMicIndex = micIndex;
    }

    /**
     * 加入游戏
     */
    private void joinGame(int micIndex) {
        // 游戏闲置，并且自己没有加入游戏时，才发送
        if (isGameIdle() && !isSelfInGame) {
            fsmApp2MGManager.sendCommonSelfInState(true, -1, true, 1);
        }
    }

    // 返回游戏是否在等待加入的状态
    private boolean isGameIdle() {
        if (playingGameId > 0 && commonGameState != null) {
            return commonGameState.gameState == CommonGameState.IDLE;
        }
        return false;
    }

    /**
     * 音频流数据
     */
    public void onCapturedAudioData(AudioData audioData) {
        fsmApp2MGManager.onAudioPush(audioData);
    }

    /**
     * 用户发送了公屏消息
     */
    public void sendMsgCompleted(String msg) {
        String keyword = gameKeywordLiveData.getValue();
        if (msg.isEmpty() || keyword == null | keyword.isEmpty()) {
            return;
        }
        if (msg.contains(keyword) && msg.length() == keyword.length()) { //命中
            fsmApp2MGManager.sendCommonSelfTextHitState(true, keyword, msg);
            gameKeywordLiveData.setValue(null);
        }
    }
}
