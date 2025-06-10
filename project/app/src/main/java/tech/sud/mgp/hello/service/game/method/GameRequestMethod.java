package tech.sud.mgp.hello.service.game.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
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

/**
 * 网络请求方法和地址
 */
public interface GameRequestMethod {

    /**
     * 游戏登录
     */
    @POST(RequestUrl.GAME_LOGIN)
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GameLoginReq body);

    /**
     * 游戏切换
     */
    @POST(RequestUrl.SWITCH_GAME)
    Observable<BaseResponse<Object>> switchGame(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SwitchGameReq body);

    /**
     * 带入筹码到游戏
     */
    @POST(RequestUrl.GAME_BRING_CHIP)
    Observable<BaseResponse<Object>> bringChip(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body BringChipReq body);

    // region 火箭

    /**
     * 查询商城组件列表
     */
    @POST(RequestUrl.ROCKET_MALL_COMPONENT_LIST)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketConfig>> rocketMallComponentList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 解锁组件
     */
    @POST(RequestUrl.ROCKET_UNLOCK_COMPONENT)
    Observable<BaseResponse<Object>> rocketUnlockComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketUnlockComponentReq body);

    /**
     * 购买组件
     */
    @POST(RequestUrl.ROCKET_BUY_COMPONENT)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketBuyComponent.Data>> rocketBuyComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketBuyComponent body);

    /**
     * 购买组件记录
     */
    @POST(RequestUrl.ROCKET_BUY_COMPONENT_RECORD)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketOrderRecordList>> rocketBuyComponentRecord(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketPageReq body);

    /**
     * 查询装配间组件列表
     */
    @POST(RequestUrl.ROCKET_COMPONENT_LIST)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketComponentList>> rocketComponentList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 保存火箭模型
     */
    @POST(RequestUrl.ROCKET_SAVE_MODEL)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketCreateModel.Data>> rocketCreateModel(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketCreateModel body);

    /**
     * 保存火箭模型
     */
    @POST(RequestUrl.ROCKET_SAVE_MODEL)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketReplaceComponent.Data>> rocketReplaceComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketReplaceComponent body);

    /**
     * 查询火箭模型列表
     */
    @POST(RequestUrl.ROCKET_MODEL_LIST)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketModelList>> rocketModelList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 发射火箭
     */
    @POST(RequestUrl.ROCKET_FIRE)
    Observable<BaseResponse<RocketFireResp>> rocketFire(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireReq body);

    /**
     * 发射火箭记录摘要
     */
    @POST(RequestUrl.ROCKET_FIRE_RECORD_SUMMERY)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketRoomRecordList>> rocketFireRecordSummery(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireRecordSummeryReq body);

    /**
     * 发射火箭记录
     */
    @POST(RequestUrl.ROCKET_FIRE_RECORD)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketUserRecordList>> rocketFireRecord(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireRecordReq body);

    /**
     * 获取发射价格
     */
    @POST(RequestUrl.ROCKET_FIRE_PRICE)
    Observable<BaseResponse<RocketFirePriceResp>> rocketFirePrice(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketDynamicFirePrice body);

    /**
     * 设置火箭默认位置
     */
    @POST(RequestUrl.ROCKET_SET_DEFAULT_MODEL)
    Observable<BaseResponse<Object>> rocketSetDefaultModel(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketSetDefaultSeatReq body);

    /**
     * 校验签名合规性
     */
    @POST(RequestUrl.ROCKET_VERIFY_SIGN)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketVerifySign.Data>> rocketVerifySign(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketVerifySign body);

    /**
     * 保存颜色或签名
     */
    @POST(RequestUrl.ROCKET_SAVE_SIGN_COLOR)
    Observable<BaseResponse<SudGIPAPPState.AppCustomRocketSaveSignColor.Data>> rocketSaveSignColor(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGCustomRocketSaveSignColor body);
    // endregion 火箭

    // region 棒球

    /** 查询排行榜 */
    @POST(RequestUrl.BASEBALL_RANKING)
    Observable<BaseResponse<SudGIPAPPState.AppBaseballRanking>> baseballRanking(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGBaseballRanking body);

    /** 查询我的排行榜 */
    @POST(RequestUrl.BASEBALL_MY_RANKING)
    Observable<BaseResponse<SudGIPAPPState.AppBaseballMyRanking>> baseballMyRanking(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGBaseballMyRanking body);

    /** 查询排在自己前后的玩家数据 */
    @POST(RequestUrl.BASEBALL_RANGE_INFO)
    Observable<BaseResponse<SudGIPAPPState.AppBaseballRangeInfo>> baseballRangeInfo(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGBaseballRangeInfo body);

    /** 打棒球 */
    @POST(RequestUrl.BASEBALL_PLAY)
    Observable<BaseResponse<Object>> baseballPlay(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body BaseballPlayReq body);

    /** 棒球文本配置 */
    @POST(RequestUrl.BASEBALL_TEXT_CONFIG)
    Observable<BaseResponse<SudGIPAPPState.AppBaseballTextConfig>> baseballTextConfig(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudGIPMGState.MGBaseballTextConfig body);
    // endregion 棒球

    /** 创建订单 */
    @POST(RequestUrl.CREATE_ORDER)
    Observable<BaseResponse<Object>> createOrder(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CreateOrderReq body);

    /** 查询玩家持有的道具 */
    @POST(RequestUrl.GAME_PLAYER_PROPS)
    Observable<BaseResponse<GamePlayerPropsResp>> gamePlayerProps(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GamePlayerPropsReq body);

}
