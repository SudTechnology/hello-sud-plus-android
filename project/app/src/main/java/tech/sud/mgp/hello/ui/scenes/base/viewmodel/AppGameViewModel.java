package tech.sud.mgp.hello.ui.scenes.base.viewmodel;

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

import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGDecorator;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGListener;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSTAPPDecorator;
import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.MGStateResponse;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.SudMGPWrapper.utils.GameCommonStateUtils;
import tech.sud.mgp.SudMGPWrapper.utils.ISudFSMStateHandleUtils;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.ISudListenerNotifyStateChange;
import tech.sud.mgp.core.SudMGP;
import tech.sud.mgp.hello.app.APPConfig;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.HSTextUtils;
import tech.sud.mgp.hello.common.utils.SystemUtils;
import tech.sud.mgp.hello.service.game.repository.GameRepository;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameLoadingProgressModel;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.rtc.audio.core.AudioPCMData;

/**
 * 游戏业务逻辑
 */
public class AppGameViewModel implements SudFSMMGListener {

    // region field
    public static long GAME_ID_NONE = 0; // 没有游戏

    private long gameRoomId; // 游戏房间id
    private long playingGameId; // 当前使用的游戏id
    public final SudFSTAPPDecorator sudFSTAPPDecorator = new SudFSTAPPDecorator(); // app调用sdk的封装类
    public final SudFSMMGDecorator sudFSMMGDecorator = new SudFSMMGDecorator(); // 用于处理游戏SDK部分回调业务

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调
    public final MutableLiveData<GameTextModel> gameMessageLiveData = new MutableLiveData<>(); // 游戏消息
    public final MutableLiveData<Object> updateMicLiveData = new MutableLiveData<>(); // 刷新麦位通知
    public final MutableLiveData<Object> autoUpMicLiveData = new MutableLiveData<>(); // 执行自动上麦的通知
    public final MutableLiveData<String> gameKeywordLiveData = new MutableLiveData<>(); // 游戏关键字
    public final MutableLiveData<Boolean> gameASRLiveData = new MutableLiveData<>(); // 游戏ASR开关
    public final MutableLiveData<Boolean> gameRTCPublishLiveData = new MutableLiveData<>(); // 游戏RTC推流开关
    public final MutableLiveData<Boolean> gameRTCPlayLiveData = new MutableLiveData<>(); // 游戏RTC拉流开关
    public final MutableLiveData<Object> playerInLiveData = new MutableLiveData<>(); // 有玩家加入或者离开了游戏
    public final MutableLiveData<Boolean> showFinishGameBtnLiveData = new MutableLiveData<>(); // 是否具备结束游戏的权力
    public final MutableLiveData<Boolean> micSpaceMaxLiveData = new MutableLiveData<>(); // 是否收缩麦位
    public final MutableLiveData<Object> autoJoinGameLiveData = new MutableLiveData<>(); // 触发自动加入游戏
    public final MutableLiveData<Integer> gameStateChangedLiveData = new MutableLiveData<>(); // 游戏状态变化
    public final MutableLiveData<Boolean> gameLoadingCompletedLiveData = new MutableLiveData<>(); // 游戏是否已加载完成
    public final MutableLiveData<Object> gameStartedLiveData = new MutableLiveData<>(); // onGameStarted回调
    public final MutableLiveData<Object> captainChangeLiveData = new MutableLiveData<>(); // 队长变化了
    public final MutableLiveData<GameLoadingProgressModel> gameLoadingProgressLiveData = new MutableLiveData<>(); // 游戏加载进度回调

    private boolean isRunning = true; // 业务是否还在运行
    public View gameView; // 游戏View
    private int selfMicIndex = -1; // 记录自己所在麦位
    public GameConfigModel gameConfigModel = new GameConfigModel(); // 游戏配置

    // endregion field

    /**
     * 外部调用切换游戏
     * gameId传0 等同于关闭游戏
     *
     * @param activity   游戏所在页面，用作于生命周期判断
     * @param gameRoomId 游戏房间id，房间隔离，同一房间才能一起游戏
     * @param gameId     游戏id，传入不同的游戏id，即可加载不同的游戏，传0等同于关闭游戏。
     */
    public void switchGame(FragmentActivity activity, long gameRoomId, long gameId) {
        checkFinishGame(gameId);
        // 因为finishGame需要一定时间发送给游戏服务，所以这里带了delay
        // 如果没有finishGame的需求，那可以不需要delay
        ThreadUtils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                if (playingGameId == gameId && AppGameViewModel.this.gameRoomId == gameRoomId) {
                    return;
                }
                destroyMG();
                AppGameViewModel.this.gameRoomId = gameRoomId;
                playingGameId = gameId;
                login(activity, gameId);
            }
        }, 100);
    }

    private void checkFinishGame(long newGameId) {
        // 自己是队长，并且游戏在进行中，收到关闭游戏的指令，则发送结束游戏的状态给游戏
        if (isCaptain(HSUserInfo.userId)) {
            int gameState = getGameState();
            if (gameState == SudMGPMGState.MGCommonGameState.LOADING
                    || gameState == SudMGPMGState.MGCommonGameState.PLAYING) {
                if (newGameId == GAME_ID_NONE) {
                    finishGame();
                }
            }
        }
    }

    /**
     * 第1步，获取短期令牌code，用于换取游戏Server访问APP Server的长期ssToken
     * 接入方客户端 调用 接入方服务端 login 获取 短期令牌code
     * 参考文档时序图：sud-mgp-doc(https://docs.sud.tech/zh-CN/app/Client/StartUp-Android.html)
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
                if (!isRunning || gameId != playingGameId) {
                    return;
                }
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
        SudMGP.initSDK(activity, sudConfig.appId, sudConfig.appKey, APPConfig.GAME_IS_TEST_ENV, new ISudListenerInitSDK() {
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
    private void loadGame(FragmentActivity activity, String code, long gameId) {
        if (activity.isDestroyed() || !isRunning || gameId != playingGameId) {
            return;
        }

        // 给装饰类设置回调
        sudFSMMGDecorator.setSudFSMMGListener(this);

        // 调用游戏sdk加载游戏
        ISudFSTAPP iSudFSTAPP = SudMGP.loadMG(activity, HSUserInfo.userId + "", gameRoomId + "", code, gameId, SystemUtils.getLanguageCode(activity), sudFSMMGDecorator);

        // 如果返回空，则代表参数问题或者非主线程
        if (iSudFSTAPP == null) {
            ToastUtils.showLong("loadMG params error");
            delayLoadGame(activity, gameId);
            return;
        }

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

    // 通知更新麦位
    private void notifyUpdateMic() {
        updateMicLiveData.setValue(null);
    }

    // region 生命周期相关
    public void onStart() {
    }

    public void onPause() {
        // 根据场景需要，playMG和pauseMG要配对
//        sudFSTAPPDecorator.pauseMG();
    }

    public void onResume() {
        // 根据场景需要，playMG和pauseMG要配对
//        sudFSTAPPDecorator.playMG();
    }

    public void onStop() {
    }

    public void onDestroy() {
        isRunning = false;
        destroyMG();
    }

    protected void destroyMG() {
        if (playingGameId > 0) {
            sudFSTAPPDecorator.destroyMG();
            sudFSMMGDecorator.destroyMG();
            playingGameId = 0;
            gameView = null;
            gameViewLiveData.setValue(null);
            gameRTCPublishLiveData.setValue(null);
            notifyUpdateMic();
            gameLoadingCompletedLiveData.setValue(false);
            notifyShowFinishGameBtn();
        }
    }

    /** 游戏是否已加载完成 */
    public boolean gameLoadingCompleted() {
        Boolean loadingCompleted = gameLoadingCompletedLiveData.getValue();
        return loadingCompleted != null && loadingCompleted;
    }

    // endregion 生命周期相关

    /** 获取当前玩着的游戏id */
    public long getPlayingGameId() {
        return playingGameId;
    }

    /** 获取当前游戏房id */
    public long getGameRoomId() {
        return gameRoomId;
    }

    /**
     * 处理code过期
     */
    public void processOnExpireCode(SudFSTAPPDecorator sudFSTAPPDecorator, ISudFSMStateHandle handle) {
        // code过期，刷新code
        GameRepository.login(null, new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = t.getRetCode();
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    sudFSTAPPDecorator.updateCode(t.getData().code, null);
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
     * 文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameViewInfo.html
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
        getGameRect(gameViewInfoModel);

        // 给游戏侧进行返回
        String json = GsonUtils.toJson(gameViewInfoModel);
        LogUtils.d("gameViewInfo:" + json);
        handle.success(json);
    }

    /** 获取游戏的安全操作区域 */
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 121) + BarUtils.getStatusBarHeight();
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 160);
    }

    /**
     * 处理游戏配置
     * 文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameCfg.html
     */
    public void processOnGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
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
        if (hasUser && isCaptain(model.userId)) {
            model.isCaptain = true;
        } else {
            model.isCaptain = false;
        }

        boolean isPlayingGame = sudFSMMGDecorator.playerIsPlaying(model.userId + ""); // 是否正在游戏中
        boolean isReady = sudFSMMGDecorator.playerIsReady(model.userId + ""); // 是否已准备
        boolean isIn = sudFSMMGDecorator.playerIsIn(model.userId + ""); // 是否已加入了游戏

        // 处理麦位是否显示游戏中图标
        if (hasUser && isPlayingGame) {
            model.isPlayingGame = true;
        } else {
            model.isPlayingGame = false;
        }

        // 是否已加入了游戏
        if (hasUser && isIn) {
            model.isIn = true;
        } else {
            model.isIn = false;
        }

        // 处理麦位中显示准备未准备的逻辑
        if (hasUser && !isPlayingGame) { // 麦位有人，并且当前没有在游戏中
            if (isReady) { // 已准备
                model.readyStatus = 1;
            } else {
                if (isIn) { // 加入了游戏，未准备
                    model.readyStatus = 2;
                } else { // 未加入游戏，不显示准备或未准备
                    model.readyStatus = 0;
                }
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
            autoJoinGame();
        }
        selfMicIndex = micIndex;
    }

    /** 自动加入游戏 */
    private void autoJoinGame() {
        autoJoinGameLiveData.postValue(null);
    }

    /** 加入游戏 */
    public void joinGame() {
        // 游戏闲置，并且自己没有加入游戏时，才发送
        if (sudFSMMGDecorator.getGameState() == SudMGPMGState.MGCommonGameState.IDLE && !isSelfInGame()) {
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(true, -1, true, 1);
        }
    }

    // 自己是否已加入了游戏
    public boolean isSelfInGame() {
        return playerIsIn(HSUserInfo.userId);
    }

    // 返回该玩家是否已加入了游戏
    public boolean playerIsIn(long userId) {
        return sudFSMMGDecorator.playerIsIn(userId + "");
    }

    /**
     * 退出游戏，执行逃跑逻辑
     */
    public void exitGame() {
        if (playerIsPlaying(HSUserInfo.userId)) {
            // 用户正在游戏中，先退出本局游戏，再退出游戏
            sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(false, null, null);
            sudFSTAPPDecorator.notifyAPPCommonSelfReady(false);
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(false, -1, true, 1);
        } else if (sudFSMMGDecorator.playerIsReady(HSUserInfo.userId + "")) {
            // 用户已加入并且已经准备，先取消准备，再退出游戏
            sudFSTAPPDecorator.notifyAPPCommonSelfReady(false);
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(false, -1, true, 1);
        } else if (isSelfInGame()) {
            // 用户已加入游戏 退出游戏
            sudFSTAPPDecorator.notifyAPPCommonSelfIn(false, -1, true, 1);
        }
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
        // 数字炸弹
        if (sudFSMMGDecorator.isHitBomb() && HSTextUtils.isInteger(msg)) {
            sudFSTAPPDecorator.notifyAPPCommonSelfTextHitState(false, null, msg, null, null, null);
            return;
        }
        String keyword = gameKeywordLiveData.getValue();
        if (keyword == null || keyword.isEmpty()) {
            return;
        }
        // 判断是否命中了关键字，这里是contains判断。接入方可根据自身业务使用equals或者其它自定义的条件。
        if (msg.contains(keyword)) {
            sudFSTAPPDecorator.notifyAPPCommonSelfTextHitState(true, keyword, msg, null, null, null);
            gameKeywordLiveData.setValue(null);
        }
    }

    // 获取当前游戏中的人数
    public int getPlayerInNumber() {
        return sudFSMMGDecorator.getPlayerInNumber();
    }

    // 对应状态有变化时，更新是否展示结束游戏按钮
    public void notifyShowFinishGameBtn() {
        // 队长以及游戏中才显示
        boolean isShow = playingGameId > 0
                && isCaptain(HSUserInfo.userId)
                && isGamePlaying();
        showFinishGameBtnLiveData.setValue(isShow);
    }

    // 游戏是否在进行中
    private boolean isGamePlaying() {
        return sudFSMMGDecorator.getGameState() == SudMGPMGState.MGCommonGameState.PLAYING;
    }

    // 该用户是否在游戏中
    public boolean playerIsPlaying(long userId) {
        return sudFSMMGDecorator.playerIsPlaying(userId + "");
    }

    /**
     * 队长结束游戏。提前结束游戏的条件:
     * 1.游戏已经加载完成（onGameStarted()回调之后），并且游戏正在进行中
     * 2.必须由游戏队长来调用才有效
     * 3.发送此状态之后，不能立即finish Activity或者destroyMG，建议delay(500ms)之后再执行销毁的逻辑。
     */
    public void finishGame() {
        sudFSTAPPDecorator.notifyAPPCommonSelfEnd();
    }

    /** 是否具备结束游戏的权限 */
    public boolean isOperateFinishGame() {
        Boolean value = showFinishGameBtnLiveData.getValue();
        return value != null && value;
    }

    // 返回该用户是否为游戏队长
    public boolean isCaptain(long userId) {
        String captainUserId = sudFSMMGDecorator.getCaptainUserId();
        return (userId + "").equals(captainUserId);
    }

    /**
     * 发送
     * 2. 准备状态
     * 用户（本人）准备/取消准备
     *
     * @param isReady true 准备，false 取消准备
     */
    public void notifyAPPCommonSelfReady(boolean isReady) {
        sudFSTAPPDecorator.notifyAPPCommonSelfReady(isReady);
    }

    /**
     * 发送
     * 3. 游戏状态
     *
     * @param isPlaying true时为队长开始游戏 false时为本人退出本局游戏
     */
    public void notifyAPPCommonSelfPlaying(boolean isPlaying, String reportGameInfoExtras, String reportGameInfoKey) {
        sudFSTAPPDecorator.notifyAPPCommonSelfPlaying(isPlaying, reportGameInfoExtras, reportGameInfoKey);
    }

    /**
     * 发送
     * 4. 队长状态
     * 用户是否为队长，队长在游戏中会有开始游戏的权利。
     *
     * @param curCaptainUID 必填，指定队长uid
     */
    public void notifyAPPCommonSelfCaptain(String curCaptainUID) {
        sudFSTAPPDecorator.notifyAPPCommonSelfCaptain(curCaptainUID);
    }

    /**
     * 发送
     * 5. 踢人
     * 用户（本人，队长）踢其他玩家；
     * 队长才能踢人；
     *
     * @param kickedUID 被踢用户uid
     */
    public void notifyAPPCommonSelfKick(String kickedUID) {
        sudFSTAPPDecorator.notifyAPPCommonSelfKick(kickedUID);
    }

    /**
     * 返回当前游戏的状态，数值参数{@link SudMGPMGState.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGDecorator.getGameState();
    }

    // 是否要拦截麦克风按钮的操作
    public boolean isInterceptSwitchMic(boolean micOpen) {
        if (micOpen) { // 意图要开麦时
            // 游戏正在进行中，并且游戏中发送过来的状态是不能开麦，则禁止开麦
            Boolean rtcPublish = gameRTCPublishLiveData.getValue();
            if (isGamePlaying() && rtcPublish != null && !rtcPublish) {
                return true;
            }
        }
        return false;
    }

    /**
     * APP状态通知给小游戏
     *
     * @param state    状态标识
     * @param dataJson 数据
     * @param listener 回调监听
     */
    public void notifyStateChange(String state, String dataJson, ISudListenerNotifyStateChange listener) {
        sudFSTAPPDecorator.notifyStateChange(state, dataJson, listener);
    }

    // region 游戏侧回调
    @Override
    public void onGameLog(String str) {
        SudFSMMGListener.super.onGameLog(str);
        LogUtils.d(str);
    }

    @Override
    public void onGameLoadingProgress(int stage, int retCode, int progress) {
        SudFSMMGListener.super.onGameLoadingProgress(stage, retCode, progress);
        gameLoadingProgressLiveData.setValue(new GameLoadingProgressModel(stage, retCode, progress));
    }

    @Override
    public void onGameStarted() {
        if (selfMicIndex >= 0) {
            autoJoinGame();
        }
        gameLoadingCompletedLiveData.setValue(true);
        gameStartedLiveData.setValue(true);
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
        String message = GameCommonStateUtils.parseMGCommonPublicMessage(model, SystemUtils.getLanguageCode(Utils.getApp()));
        if (!TextUtils.isEmpty(message)) {
            GameTextModel gameTextModel = new GameTextModel();
            gameTextModel.message = message;
            gameMessageLiveData.setValue(gameTextModel);
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 游戏状态
    @Override
    public void onGameMGCommonGameState(ISudFSMStateHandle handle, SudMGPMGState.MGCommonGameState model) {
        gameStateChangedLiveData.setValue(model.gameState);
        notifyShowFinishGameBtn();
        if (model.gameState == SudMGPMGState.MGCommonGameState.LOADING) { // 游戏开始，收缩麦位
            micSpaceMaxLiveData.setValue(true);
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 关键字
    @Override
    public void onGameMGCommonKeyWordToHit(ISudFSMStateHandle handle, SudMGPMGState.MGCommonKeyWordToHit model) {
        if (model != null) {
            gameKeywordLiveData.setValue(model.word);
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

    // 麦克风状态
    @Override
    public void onGameMGCommonSelfMicrophone(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfMicrophone model) {
        boolean isOn = model != null && model.isOn;
        gameRTCPublishLiveData.setValue(isOn);
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 耳机（听筒，扬声器）状态
    @Override
    public void onGameMGCommonSelfHeadphone(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfHeadphone model) {
        boolean isOn = model != null && model.isOn;
        gameRTCPlayLiveData.setValue(isOn);
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 队长状态
    @Override
    public void onPlayerMGCommonPlayerCaptain(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerCaptain model) {
        captainChangeLiveData.setValue(model);
        if (model != null) {
            notifyUpdateMic();
            notifyShowFinishGameBtn();
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 玩家准备状态
    @Override
    public void onPlayerMGCommonPlayerReady(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerReady model) {
        if (model != null) {
            notifyUpdateMic();
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 玩家加入状态
    @Override
    public void onPlayerMGCommonPlayerIn(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerIn model) {
        if (model != null) {
            if ((HSUserInfo.userId + "").equals(userId)) { // 属于自己的变动
                if (model.isIn) {
                    autoUpMicLiveData.setValue(null);
                }
            }
            notifyUpdateMic();
            playerInLiveData.setValue(null);
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }

    // 玩家游戏状态
    @Override
    public void onPlayerMGCommonPlayerPlaying(ISudFSMStateHandle handle, String userId, SudMGPMGState.MGCommonPlayerPlaying model) {
        if (model != null) {
            notifyUpdateMic();
        }
        ISudFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏侧回调

}
