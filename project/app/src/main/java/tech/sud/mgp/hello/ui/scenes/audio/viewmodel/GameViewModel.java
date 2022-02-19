package tech.sud.mgp.hello.ui.scenes.audio.viewmodel;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import java.util.HashMap;

import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.SudMGPWrapper.decorator.SudFSMMGDecorator;
import tech.sud.mgp.hello.SudMGPWrapper.decorator.SudFSMMGListener;
import tech.sud.mgp.hello.SudMGPWrapper.decorator.SudFSTAPPDecorator;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.hello.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.SudMGPWrapper.state.MGStateResponse;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.SudMGPWrapper.utils.GameCommonStateUtils;
import tech.sud.mgp.hello.SudMGPWrapper.utils.ISudFSMStateHandleUtils;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.HSTextUtils;
import tech.sud.mgp.hello.rtc.audio.core.AudioPCMData;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.ui.scenes.audio.model.AudioRoomMicModel;

/**
 * 游戏业务逻辑
 */
public class GameViewModel {

    private long roomId; // 房间id
    private long playingGameId; // 当前使用的游戏id
    private final SudFSTAPPDecorator sudFSTAPPDecorator = new SudFSTAPPDecorator(); // app调用sdk的封装类
    private final SudFSMMGDecorator sudFSMMGDecorator = new SudFSMMGDecorator(); // 用于处理游戏SDK部分回调业务

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调
    public final MutableLiveData<String> gameMessageLiveData = new MutableLiveData<>(); // 游戏消息
    public final MutableLiveData<Object> updateMicLiveData = new MutableLiveData<>(); // 刷新麦位通知
    public final MutableLiveData<Object> autoUpMicLiveData = new MutableLiveData<>(); // 执行自动上麦的通知
    public final MutableLiveData<String> gameKeywordLiveData = new MutableLiveData<>(); // 游戏关键字
    public final MutableLiveData<Boolean> gameASRLiveData = new MutableLiveData<>(); // 游戏ASR开关
    public final MutableLiveData<Boolean> showFinishGameBtnLiveData = new MutableLiveData<>(); // 是否具备结束游戏的权力

    private View gameView; // 游戏View
    private long captainUserId; // 记录当前队长的用户id
    private SudMGPMGState.MGCommonGameState mgCommonGameStateModel; // 游戏状态
    private boolean isSelfInGame; // 标识自己是否已加入了游戏
    private int selfMicIndex = -1; // 记录自己所在麦位
    private boolean isHitBomb = false; // 是否数字炸弹

    /**
     * 记录该玩家最新的游戏状态
     * 1，准备状态 {@link  SudMGPMGState.MGCommonPlayerReady}
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
        login(activity, gameId);
    }

    /**
     * 第1步，获取短期令牌code，用于换取游戏Server访问APP Server的长期ssToken
     * 接入方客户端 调用 接入方服务端 login 获取 短期令牌code
     * 参考文档时序图：sud-mgp-doc(https://github.com/SudTechnology/sud-mgp-doc)
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    private void login(FragmentActivity activity, long gameId) {
        if (activity.isDestroyed() || gameId <= 0) {
            return;
        }
        // 请求登录code
        GameRepository.login(activity, new RxCallback<GameLoginResp>() {
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
     * 第2步，初始化SudMGP sdk
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     * @param code     令牌
     */
    private void initSdk(FragmentActivity activity, long gameId, String code) {
        SudConfig sudConfig = AppData.getInstance().getSudConfig();
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
                ToastUtils.showShort("initSDK onFailure:" + errMsg + "(" + errCode + ")");
                delayLoadGame(activity, gameId);
            }
        });
    }

    /**
     * 第3步，加载游戏
     * APP和游戏的相互调用
     * ISudFSTAPP：APP调用游戏的接口
     * ISudFSMMG：游戏调APP的响应回调
     *
     * @param activity 游戏所在页面
     * @param code     登录令牌
     * @param gameId   游戏id
     */
    private void loadGame(Activity activity, String code, long gameId) {
        // 给装饰类设置回调
        sudFSMMGDecorator.setSudFSMMGListener(sudFSMMGListener);

        // 调用游戏sdk加载游戏
        ISudFSTAPP iSudFSTAPP = SudMGP.loadMG(activity, HSUserInfo.userId + "", roomId + "", code, gameId, "zh-CN", sudFSMMGDecorator);

        // APP调用游戏接口的装饰类设置
        sudFSTAPPDecorator.setISudFSTAPP(iSudFSTAPP);

        // 获取游戏视图，将其抛回Activity进行展示
        // Activity调用：gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
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
                login(activity, gameId);
            }
        }, 5000);
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

    /**
     * 游戏侧的回调
     */
    private final SudFSMMGListener sudFSMMGListener = new SudFSMMGListener() {
        @Override
        public void onGameLog(String str) {
            super.onGameLog(str);
            LogUtils.d(str);
        }

        @Override
        public void onGameStarted() {
        }

        @Override
        public void onGameDestroyed() {
        }

        @Override
        public void onExpireCode(ISudFSMStateHandle handle, String dataJson) {
            processOnExpireCode(sudFSTAPPDecorator, handle);
        }

        @Override
        public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {
            processOnGetGameViewInfo(gameView, handle);
        }

        @Override
        public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
            processOnGetGameCfg(handle, dataJson);
        }

        // 公屏消息
        @Override
        public void onGameMGCommonPublicMessage(ISudFSMStateHandle handle, SudMGPMGState.MGCommonPublicMessage model) {
            gameMessageLiveData.setValue(GameCommonStateUtils.parseMGCommonPublicMessage(model));
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 游戏状态
        @Override
        public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
            mgCommonGameStateModel = model;
            notifyShowFinishGameBtn();
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 关键字
        @Override
        public void onGameMGCommonKeyWordToHit(ISudFSMStateHandle handle, SudMGPMGState.MGCommonKeyWordToHit model) {
            if (model != null) {
                String word = model.word;
                isHitBomb = model.wordType.equals("number");
                gameKeywordLiveData.setValue(word);
            }
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // ASR开关
        @Override
        public void onGameMGCommonGameASR(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameASR model) {
            boolean isOpen = model != null && model.isOpen;
            gameASRLiveData.setValue(isOpen);
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 队长状态
        @Override
        public void onPlayerMGCommonPlayerCaptain(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerCaptain model) {
            if (model != null) {
                if (model.isCaptain) { // 该用户成为了队长
                    captainChange(userId);
                } else {
                    if ((captainUserId + "").equals(userId)) { // 当前队长变为了非队长
                        captainChange(null);
                    }
                }
                notifyShowFinishGameBtn();
            }
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 玩家准备状态
        @Override
        public void onPlayerMGCommonPlayerReady(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerReady model) {
            if (model != null) {
                putPlayerState(userId, model);
                notifyUpdateMic();
            }
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 玩家加入状态
        @Override
        public void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerIn model) {
            if (model != null) {
                if ((HSUserInfo.userId + "").equals(userId)) { // 属于自己的变动
                    isSelfInGame = model.isIn;
                    if (model.isIn) {
                        autoUpMicLiveData.setValue(null);
                    }
                }
                if (model.isIn) {
                    putPlayerState(userId, model);
                } else {
                    removePlayerState(userId);
                }
                notifyUpdateMic();
            }
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }

        // 玩家游戏状态
        @Override
        public void onPlayerMGCommonPlayerPlaying(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerPlaying model) {
            if (model != null) {
                if (model.isPlaying) {
                    putPlayerState(userId, model);
                } else {
                    removePlayerState(userId);
                }
                notifyUpdateMic();
            }
            ISudFSMStateHandleUtils.handleSuccess(handle);
        }
    };

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


    // region 生命周期相关
    public void onStart() {
        sudFSTAPPDecorator.startMG();
    }

    public void onPause() {
        sudFSTAPPDecorator.pauseMG();
    }

    public void onResume() {
        sudFSTAPPDecorator.playMG();
    }

    public void onStop() {
        sudFSTAPPDecorator.stopMG();
    }

    public void destroyMG() {
        if (playingGameId > 0) {
            sudFSTAPPDecorator.destroyMG();
            playingGameId = 0;
            gameView = null;
            gameViewLiveData.setValue(null);
            captainUserId = 0;
            mgCommonGameStateModel = null;
            playerStates.clear();
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
     * 返回当前游戏的状态，数值参数{@link SudMGPMGState.MGCommonGameState}
     */
    public int getGameState() {
        if (mgCommonGameStateModel != null) {
            return mgCommonGameStateModel.gameState;
        }
        return SudMGPMGState.MGCommonGameState.IDLE;
    }

    /**
     * 处理code过期
     */
    public void processOnExpireCode(SudFSTAPPDecorator sudFSTAPPManager, ISudFSMStateHandle handle) {
        // code过期，刷新code
        GameRepository.login(null, new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = t.getRetCode();
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    sudFSTAPPManager.updateCode(t.getData().code, null);
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
     * 处理游戏视图信息(游戏安全区)
     * 文档：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/API/ISudFSMMG/onGetGameViewInfo.md
     */
    public void processOnGetGameViewInfo(View gameView, ISudFSMStateHandle handle) {
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
     */
    private void notifyGameViewInfo(ISudFSMStateHandle handle, int gameViewWidth, int gameViewHeight) {
        GameViewInfoModel gameViewInfoModel = new GameViewInfoModel();
        gameViewInfoModel.ret_code = 0;
        // 游戏View大小
        gameViewInfoModel.view_size.width = gameViewWidth;
        gameViewInfoModel.view_size.height = gameViewHeight;

        // 游戏安全操作区域
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 110) + BarUtils.getStatusBarHeight();
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 160);

        // 给游戏侧进行返回
        handle.success(GsonUtils.toJson(gameViewInfoModel));
    }

    /**
     * 处理游戏配置
     * 文档：https://github.com/SudTechnology/sud-mgp-doc/blob/main/Client/API/ISudFSMMG/onGetGameCfg.md
     */
    public void processOnGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
        GameConfigModel gameConfigModel = new GameConfigModel();
        // 配置不展示大厅玩家展示位
        gameConfigModel.ui.lobby_players.hide = true;
        handle.success(GsonUtils.toJson(gameConfigModel));
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
            if (playerState instanceof SudMGPMGState.MGCommonPlayerReady) { // 准备状态
                SudMGPMGState.MGCommonPlayerReady readyState = (SudMGPMGState.MGCommonPlayerReady) playerState;
                if (readyState.isReady) { // 已准备
                    model.readyStatus = 1;
                } else { // 未准备
                    model.readyStatus = 2;
                }
            } else if (playerState instanceof SudMGPMGState.MGCommonPlayerIn) { // 加入状态
                SudMGPMGState.MGCommonPlayerIn playerInState = (SudMGPMGState.MGCommonPlayerIn) playerState;
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

        // 是否正在游戏中
        boolean isPlayingGame = false;
        if (hasUser) {
            Object playerState = getPlayerState(model.userId + "");
            if (playerState instanceof SudMGPMGState.MGCommonPlayerPlaying) {
                SudMGPMGState.MGCommonPlayerPlaying playerPlayingState = (SudMGPMGState.MGCommonPlayerPlaying) playerState;
                isPlayingGame = playerPlayingState.isPlaying;
            }
        }
        model.isPlayingGame = isPlayingGame;
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
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(true, -1, true, 1);
        }
    }

    /**
     * 下麦后退出游戏
     */
    public void exitGame() {
        if (playerIsPlaying(HSUserInfo.userId)) {
            sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(false, "");
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(false, -1, true, 1);
        }
    }

    // 返回游戏是否在等待加入的状态
    private boolean isGameIdle() {
        if (playingGameId > 0 && mgCommonGameStateModel != null) {
            return mgCommonGameStateModel.gameState == SudMGPMGState.MGCommonGameState.IDLE;
        }
        return false;
    }

    /**
     * 音频流数据
     */
    public void onCapturedAudioData(AudioPCMData audioPCMData) {
        sudFSTAPPDecorator.pushAudio(audioPCMData.data, audioPCMData.dataLength);
    }

    /**
     * 用户发送了公屏消息
     */
    public void sendMsgCompleted(String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        //数字炸弹
        if (isHitBomb && HSTextUtils.isInteger(msg)) {
            sudFSTAPPDecorator.notifyAPPCommonSelfTextHitState(false, null, msg, null, null, null);
            return;
        }
        String keyword = gameKeywordLiveData.getValue();
        if (keyword == null || keyword.isEmpty()) {
            return;
        }
        if (msg.contains(keyword)) { //命中
            sudFSTAPPDecorator.notifyAPPCommonSelfTextHitState(true, keyword, msg, null, null, null);
            gameKeywordLiveData.setValue(null);
        }
    }

    // 对应状态有变化时，更新是否展示结束游戏按钮
    public void notifyShowFinishGameBtn() {
        // 队长以及游戏中才显示
        boolean isShow = playingGameId > 0
                && captainUserId == HSUserInfo.userId
                && mgCommonGameStateModel != null && mgCommonGameStateModel.gameState == SudMGPMGState.MGCommonGameState.PLAYING;
        showFinishGameBtnLiveData.setValue(isShow);
    }


    // 该用户是否在游戏中
    public boolean playerIsPlaying(long userId) {
        Object playerState = getPlayerState(userId + "");
        if (playerState instanceof SudMGPMGState.MGCommonPlayerPlaying) {
            SudMGPMGState.MGCommonPlayerPlaying playerPlayingState = (SudMGPMGState.MGCommonPlayerPlaying) playerState;
            return playerPlayingState.isPlaying;
        }
        return false;
    }

    public void finishGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfEnd();
    }
}
