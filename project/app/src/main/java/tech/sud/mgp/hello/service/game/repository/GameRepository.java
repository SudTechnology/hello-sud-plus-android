package tech.sud.mgp.hello.service.game.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.service.game.method.GameRequestMethodFactory;
import tech.sud.mgp.hello.service.game.req.BaseballPlayReq;
import tech.sud.mgp.hello.service.game.req.BringChipReq;
import tech.sud.mgp.hello.service.game.req.CreateOrderReq;
import tech.sud.mgp.hello.service.game.req.GameLoginReq;
import tech.sud.mgp.hello.service.game.req.GamePlayerPropsReq;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordReq;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordSummeryReq;
import tech.sud.mgp.hello.service.game.req.RocketFireReq;
import tech.sud.mgp.hello.service.game.req.RocketPageReq;
import tech.sud.mgp.hello.service.game.req.RocketSetDefaultSeatReq;
import tech.sud.mgp.hello.service.game.req.RocketUnlockComponentReq;
import tech.sud.mgp.hello.service.game.req.SwitchGameReq;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.game.resp.GamePlayerPropsResp;
import tech.sud.mgp.hello.service.game.resp.RocketFirePriceResp;
import tech.sud.mgp.hello.service.game.resp.RocketFireResp;

public class GameRepository {

    /**
     * 游戏登录
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String appId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.appId = appId;
        GameRequestMethodFactory.getMethod()
                .gameLogin(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 游戏切换
     *
     * @param owner    生命周期对象
     * @param roomId   房间id
     * @param gameId   游戏id
     * @param callback 回调
     */
    public static void switchGame(LifecycleOwner owner, long roomId, long gameId, RxCallback<Object> callback) {
        SwitchGameReq switchGameReq = new SwitchGameReq();
        switchGameReq.roomId = roomId;
        switchGameReq.gameId = gameId;
        GameRequestMethodFactory.getMethod()
                .switchGame(BaseUrlManager.getInteractBaseUrl(), switchGameReq)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 带入筹码到游戏
     *
     * @param owner            生命周期对象
     * @param mgId             游戏id
     * @param roomId           roomId
     * @param roundId          订单关联的游戏局id
     * @param lastRoundScore   本人当前积分
     * @param incrementalScore 充值积分
     * @param totalScore       充值后总积分
     * @param callback         回调
     */
    public static void bringChip(LifecycleOwner owner, long mgId, String roomId, String roundId, long lastRoundScore,
                                 long incrementalScore, long totalScore, RxCallback<Object> callback) {
        BringChipReq req = new BringChipReq();
        req.mgId = mgId;
        req.roomId = roomId;
        req.roundId = roundId;
        req.lastRoundScore = lastRoundScore;
        req.incrementalScore = incrementalScore;
        req.totalScore = totalScore;
        GameRequestMethodFactory.getMethod()
                .bringChip(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    // region 火箭

    /**
     * 查询商城组件列表
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void rocketMallComponentList(LifecycleOwner owner, RxCallback<SudGIPAPPState.AppCustomRocketConfig> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketMallComponentList(BaseUrlManager.getGameBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 解锁组件
     *
     * @param owner       生命周期对象
     * @param componentId 组件Id
     * @param isLock      是否锁住
     * @param callback    回调
     */
    public static void rocketUnlockComponent(LifecycleOwner owner, String componentId, int isLock, RxCallback<Object> callback) {
        RocketUnlockComponentReq req = new RocketUnlockComponentReq();
        req.componentId = componentId;
        req.isLock = isLock;
        GameRequestMethodFactory.getMethod()
                .rocketUnlockComponent(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 购买组件
     *
     * @param owner    生命周期对象
     * @param req      购买的组件列表
     * @param callback 回调
     */
    public static void rocketBuyComponent(LifecycleOwner owner, SudGIPMGState.MGCustomRocketBuyComponent req, RxCallback<SudGIPAPPState.AppCustomRocketBuyComponent.Data> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketBuyComponent(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 购买组件记录
     *
     * @param owner     生命周期对象
     * @param pageIndex 页码
     * @param pageSize  每页多少条数据
     * @param callback  回调
     */
    public static void rocketBuyComponentRecord(LifecycleOwner owner, int pageIndex, int pageSize, RxCallback<SudGIPAPPState.AppCustomRocketOrderRecordList> callback) {
        RocketPageReq req = new RocketPageReq();
        req.pageIndex = pageIndex;
        req.pageSize = pageSize;
        GameRequestMethodFactory.getMethod()
                .rocketBuyComponentRecord(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 查询装配间组件列表
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void rocketComponentList(LifecycleOwner owner, RxCallback<SudGIPAPPState.AppCustomRocketComponentList> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketComponentList(BaseUrlManager.getGameBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 保存火箭模型
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void rocketCreateModel(LifecycleOwner owner, SudGIPMGState.MGCustomRocketCreateModel req, RxCallback<SudGIPAPPState.AppCustomRocketCreateModel.Data> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketCreateModel(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 保存火箭模型
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void rocketReplaceComponent(LifecycleOwner owner, SudGIPMGState.MGCustomRocketReplaceComponent req, RxCallback<SudGIPAPPState.AppCustomRocketReplaceComponent.Data> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketReplaceComponent(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 查询火箭模型列表
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void rocketModelList(LifecycleOwner owner, RxCallback<SudGIPAPPState.AppCustomRocketModelList> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketModelList(BaseUrlManager.getGameBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 发射
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketFire(LifecycleOwner owner, RocketFireReq req, RxCallback<RocketFireResp> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketFire(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 发射火箭记录摘要
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketFireRecordSummery(LifecycleOwner owner, RocketFireRecordSummeryReq req, RxCallback<SudGIPAPPState.AppCustomRocketRoomRecordList> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketFireRecordSummery(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 发射火箭记录
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketFireRecord(LifecycleOwner owner, RocketFireRecordReq req, RxCallback<SudGIPAPPState.AppCustomRocketUserRecordList> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketFireRecord(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 获取发射价格
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketFirePrice(LifecycleOwner owner, SudGIPMGState.MGCustomRocketDynamicFirePrice req, RxCallback<RocketFirePriceResp> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketFirePrice(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 设置火箭默认位置
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketSetDefaultModel(LifecycleOwner owner, RocketSetDefaultSeatReq req, RxCallback<Object> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketSetDefaultModel(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 火箭 校验签名合规性
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketVerifySign(LifecycleOwner owner, SudGIPMGState.MGCustomRocketVerifySign req, RxCallback<SudGIPAPPState.AppCustomRocketVerifySign.Data> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketVerifySign(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 保存颜色或签名
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void rocketSaveSignColor(LifecycleOwner owner, SudGIPMGState.MGCustomRocketSaveSignColor req, RxCallback<SudGIPAPPState.AppCustomRocketSaveSignColor.Data> callback) {
        GameRequestMethodFactory.getMethod()
                .rocketSaveSignColor(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }
    // endregion 火箭

    // region 棒球

    /**
     * 查询排行榜
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void baseballRanking(LifecycleOwner owner, SudGIPMGState.MGBaseballRanking req, RxCallback<SudGIPAPPState.AppBaseballRanking> callback) {
        GameRequestMethodFactory.getMethod()
                .baseballRanking(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 查询我的排行榜
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void baseballMyRanking(LifecycleOwner owner, SudGIPMGState.MGBaseballMyRanking req, RxCallback<SudGIPAPPState.AppBaseballMyRanking> callback) {
        GameRequestMethodFactory.getMethod()
                .baseballMyRanking(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 查询排在自己前后的玩家数据
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void baseballRangeInfo(LifecycleOwner owner, SudGIPMGState.MGBaseballRangeInfo req, RxCallback<SudGIPAPPState.AppBaseballRangeInfo> callback) {
        GameRequestMethodFactory.getMethod()
                .baseballRangeInfo(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 打棒球
     *
     * @param owner    生命周期对象
     * @param req      请求参数
     * @param callback 回调
     */
    public static void baseballPlay(LifecycleOwner owner, BaseballPlayReq req, RxCallback<Object> callback) {
        GameRequestMethodFactory.getMethod()
                .baseballPlay(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 棒球文本配置
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void baseballTextConfig(LifecycleOwner owner, SudGIPMGState.MGBaseballTextConfig req, RxCallback<SudGIPAPPState.AppBaseballTextConfig> callback) {
        GameRequestMethodFactory.getMethod()
                .baseballTextConfig(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }
    // endregion 棒球

    /**
     * 创建订单
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void createOrder(LifecycleOwner owner, CreateOrderReq req, RxCallback<Object> callback) {
        GameRequestMethodFactory.getMethod()
                .createOrder(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 查询玩家持有的道具
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void gamePlayerProps(LifecycleOwner owner, GamePlayerPropsReq req, RxCallback<GamePlayerPropsResp> callback) {
        GameRequestMethodFactory.getMethod()
                .gamePlayerProps(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
