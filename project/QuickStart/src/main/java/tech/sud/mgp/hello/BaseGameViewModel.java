package tech.sud.mgp.hello;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGDecorator;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSMMGListener;
import tech.sud.mgp.SudMGPWrapper.decorator.SudFSTAPPDecorator;
import tech.sud.mgp.SudMGPWrapper.model.GameConfigModel;
import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.MGStateResponse;
import tech.sud.mgp.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.core.ISudFSTAPP;
import tech.sud.mgp.core.ISudListenerInitSDK;
import tech.sud.mgp.core.SudMGP;

/**
 * 游戏业务逻辑抽象类
 * 1.定自义ViewModel继承此类，实现对应方法。(注意：onAddGameView()与onRemoveGameView()与页面有交互)
 * 2.外部调用switchGame()方法启动游戏
 * 3.页面销毁时调用onDestroy()
 */
public abstract class BaseGameViewModel implements SudFSMMGListener {

    private String gameRoomId; // 游戏房间id
    private long playingGameId; // 当前使用的游戏id
    public final SudFSTAPPDecorator sudFSTAPPDecorator = new SudFSTAPPDecorator(); // app调用sdk的封装类
    private final SudFSMMGDecorator sudFSMMGDecorator = new SudFSMMGDecorator(); // 用于处理游戏SDK部分回调业务

    private boolean isRunning = true; // 业务是否还在运行
    public View gameView; // 游戏View
    public GameConfigModel gameConfigModel = new GameConfigModel(); // 游戏配置
    protected final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 外部调用切换游戏，传不同的gameId即可加载不同的游戏
     * gameId传0 等同于关闭游戏
     *
     * @param activity   游戏所在页面，用作于生命周期判断
     * @param gameRoomId 游戏房间id，房间隔离，同一房间才能一起游戏
     * @param gameId     游戏id，传入不同的游戏id，即可加载不同的游戏，传0等同于关闭游戏
     */
    public void switchGame(Activity activity, String gameRoomId, long gameId) {
        if (TextUtils.isEmpty(gameRoomId)) {
            Toast.makeText(activity, "gameRoomId can not be empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isRunning) {
            return;
        }
        if (playingGameId == gameId && gameRoomId.equals(this.gameRoomId)) {
            return;
        }
        destroyMG();
        this.gameRoomId = gameRoomId;
        playingGameId = gameId;
        login(activity, gameId);
    }

    /**
     * 第1步，获取短期令牌code，用于换取游戏Server访问APP Server的长期ssToken
     * 接入方客户端 调用 接入方服务端 login 获取 短期令牌code
     * 参考文档时序图：sud-mgp-doc(https://docs.sud.tech/zh-CN/app/Client/StartUp-Android.html)
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    private void login(Activity activity, long gameId) {
        if (activity.isDestroyed() || gameId <= 0) {
            return;
        }
        // 请求登录code
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!isRunning || gameId != playingGameId) {
                    return;
                }
                initSdk(activity, gameId, code);
            }

            @Override
            public void onFailed() {
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
    private void initSdk(Activity activity, long gameId, String code) {
        String appId = getAppId();
        String appKey = getAppKey();
        // 初始化sdk
        SudMGP.initSDK(activity, appId, appKey, isTestEnv(), new ISudListenerInitSDK() {
            @Override
            public void onSuccess() {
                loadGame(activity, code, gameId);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                // TODO: 2022/6/13 下面toast可以根据业务需要决定是否保留
                if (isTestEnv()) {
                    Toast.makeText(activity, "initSDK onFailure:" + errMsg + "(" + errCode + ")", Toast.LENGTH_LONG).show();
                }

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
        if (activity.isDestroyed() || !isRunning || gameId != playingGameId) {
            return;
        }

        // 给装饰类设置回调
        sudFSMMGDecorator.setSudFSMMGListener(this);

        // 调用游戏sdk加载游戏
        ISudFSTAPP iSudFSTAPP = SudMGP.loadMG(activity, getUserId(), gameRoomId, code, gameId, getLanguageCode(), sudFSMMGDecorator);

        // 如果返回空，则代表参数问题或者非主线程
        if (iSudFSTAPP == null) {
            Toast.makeText(activity, "loadMG params error", Toast.LENGTH_LONG).show();
            delayLoadGame(activity, gameId);
            return;
        }

        // APP调用游戏接口的装饰类设置
        sudFSTAPPDecorator.setISudFSTAPP(iSudFSTAPP);

        // 获取游戏视图，将其抛回Activity进行展示
        // Activity调用：gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        gameView = iSudFSTAPP.getGameView();
        onAddGameView(gameView);
    }

    /**
     * 游戏加载失败的时候，延迟一会再重新加载
     *
     * @param activity 游戏所在页面
     * @param gameId   游戏id
     */
    private void delayLoadGame(Activity activity, long gameId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId);
            }
        }, 5000);
    }

    // region 生命周期相关

    /** 页面销毁的时候调用 */
    public void onDestroy() {
        isRunning = false;
        destroyMG();
    }
    // endregion 生命周期相关


    /** 销毁游戏 */
    private void destroyMG() {
        if (playingGameId > 0) {
            sudFSTAPPDecorator.destroyMG();
            sudFSMMGDecorator.destroyMG();
            playingGameId = 0;
            gameView = null;
            onRemoveGameView();
        }
    }

    /** 获取当前游戏房id */
    public String getGameRoomId() {
        return gameRoomId;
    }

    // region 子类需要实现的方法

    /**
     * 向接入方服务器获取code
     */
    protected abstract void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener);

    /**
     * 设置当前用户id(接入方定义)
     *
     * @return 返回用户id
     */
    protected abstract String getUserId();

    /**
     * 设置游戏所用的appId
     *
     * @return 返回游戏服务appId
     */
    protected abstract String getAppId();

    /**
     * 设置游戏所用的appKey
     *
     * @return 返回游戏服务appKey
     */
    protected abstract String getAppKey();

    /**
     * 设置游戏的语言代码
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/Languages/
     *
     * @return 返回语言代码
     */
    protected abstract String getLanguageCode();

    /**
     * 设置游戏的安全操作区域
     *
     * @param gameViewInfoModel 游戏视图大小
     */
    protected abstract void getGameRect(GameViewInfoModel gameViewInfoModel);

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    protected abstract boolean isTestEnv();

    /**
     * 将游戏View添加到页面中
     *
     * @param gameView
     */
    protected abstract void onAddGameView(View gameView);

    /**
     * 将页面中的游戏View移除
     */
    protected abstract void onRemoveGameView();

    // endregion 子类需要实现的方法

    // region 游戏侧回调

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameLog(String str) {
        SudFSMMGListener.super.onGameLog(str);
    }

    /**
     * 游戏开始
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameStarted() {
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
    }

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    @Override
    public void onExpireCode(ISudFSMStateHandle handle, String dataJson) {
        processOnExpireCode(sudFSTAPPDecorator, handle);
    }

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   handle
     * @param dataJson {}
     */
    @Override
    public void onGetGameViewInfo(ISudFSMStateHandle handle, String dataJson) {
        processOnGetGameViewInfo(gameView, handle);
    }

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   handle
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    @Override
    public void onGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
        processOnGetGameCfg(handle, dataJson);
    }
    // endregion 游戏侧回调


    /** 处理code过期 */
    public void processOnExpireCode(SudFSTAPPDecorator sudFSTAPPDecorator, ISudFSMStateHandle handle) {
        // code过期，刷新code
        getCode(null, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!isRunning) return;
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = MGStateResponse.SUCCESS;
                sudFSTAPPDecorator.updateCode(code, null);
                handle.success(SudJsonUtils.toJson(mgStateResponse));
            }

            @Override
            public void onFailed() {
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = -1;
                handle.failure(SudJsonUtils.toJson(mgStateResponse));
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

    /** 通知游戏，游戏视图信息 */
    private void notifyGameViewInfo(ISudFSMStateHandle handle, int gameViewWidth, int gameViewHeight) {
        GameViewInfoModel gameViewInfoModel = new GameViewInfoModel();
        gameViewInfoModel.ret_code = 0;
        // 游戏View大小
        gameViewInfoModel.view_size.width = gameViewWidth;
        gameViewInfoModel.view_size.height = gameViewHeight;

        // 游戏安全操作区域
        getGameRect(gameViewInfoModel);

        // 给游戏侧进行返回
        String json = SudJsonUtils.toJson(gameViewInfoModel);
        // 如果设置安全区有疑问，可将下面的日志打印出来，分析json数据
        // Log.d("SudBaseGameViewModel", "notifyGameViewInfo:" + json);
        handle.success(json);
    }

    /**
     * 处理游戏配置
     * 文档：https://docs.sud.tech/zh-CN/app/Client/API/ISudFSMMG/onGetGameCfg.html
     */
    public void processOnGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
        handle.success(SudJsonUtils.toJson(gameConfigModel));
    }

    /** 游戏login(getCode)监听 */
    public interface GameGetCodeListener {
        /** 成功 */
        void onSuccess(String code);

        /** 失败 */
        void onFailed();
    }

}
