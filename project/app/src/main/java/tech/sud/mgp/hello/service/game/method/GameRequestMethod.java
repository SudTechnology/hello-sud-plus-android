package tech.sud.mgp.hello.service.game.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.game.req.BringChipReq;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordReq;
import tech.sud.mgp.hello.service.game.req.RocketFireRecordSummeryReq;
import tech.sud.mgp.hello.service.game.req.RocketFireReq;
import tech.sud.mgp.hello.service.game.req.RocketPageReq;
import tech.sud.mgp.hello.service.game.req.RocketSetDefaultSeatReq;
import tech.sud.mgp.hello.service.game.req.RocketUnlockComponentReq;
import tech.sud.mgp.hello.service.game.req.SwitchGameReq;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
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
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

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

    /**
     * 查询商城组件列表
     */
    @POST(RequestUrl.ROCKET_MALL_COMPONENT_LIST)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketConfig>> rocketMallComponentList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 解锁组件
     */
    @POST(RequestUrl.ROCKET_UNLOCK_COMPONENT)
    Observable<BaseResponse<Object>> rocketUnlockComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketUnlockComponentReq body);

    /**
     * 购买组件
     */
    @POST(RequestUrl.ROCKET_BUY_COMPONENT)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketBuyComponent.Data>> rocketBuyComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudMGPMGState.MGCustomRocketBuyComponent body);

    /**
     * 购买组件记录
     */
    @POST(RequestUrl.ROCKET_BUY_COMPONENT_RECORD)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketOrderRecordList>> rocketBuyComponentRecord(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketPageReq body);

    /**
     * 查询装配间组件列表
     */
    @POST(RequestUrl.ROCKET_COMPONENT_LIST)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketComponentList>> rocketComponentList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 保存火箭模型
     */
    @POST(RequestUrl.ROCKET_SAVE_MODEL)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketCreateModel.Data>> rocketCreateModel(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudMGPMGState.MGCustomRocketCreateModel body);

    /**
     * 保存火箭模型
     */
    @POST(RequestUrl.ROCKET_SAVE_MODEL)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketReplaceComponent.Data>> rocketReplaceComponent(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudMGPMGState.MGCustomRocketReplaceComponent body);

    /**
     * 查询火箭模型列表
     */
    @POST(RequestUrl.ROCKET_MODEL_LIST)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketModelList>> rocketModelList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 发射火箭
     */
    @POST(RequestUrl.ROCKET_FIRE)
    Observable<BaseResponse<RocketFireResp>> rocketFire(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireReq body);

    /**
     * 发射火箭记录摘要
     */
    @POST(RequestUrl.ROCKET_FIRE_RECORD_SUMMERY)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketRoomRecordList>> rocketFireRecordSummery(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireRecordSummeryReq body);

    /**
     * 发射火箭记录
     */
    @POST(RequestUrl.ROCKET_FIRE_RECORD)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketUserRecordList>> rocketFireRecord(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketFireRecordReq body);

    /**
     * 获取发射价格
     */
    @POST(RequestUrl.ROCKET_FIRE_PRICE)
    Observable<BaseResponse<RocketFirePriceResp>> rocketFirePrice(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudMGPMGState.MGCustomRocketDynamicFirePrice body);

    /**
     * 设置火箭默认位置
     */
    @POST(RequestUrl.ROCKET_SET_DEFAULT_SEAT)
    Observable<BaseResponse<Object>> rocketSetDefaultSeat(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RocketSetDefaultSeatReq body);

    /**
     * 校验签名合规性
     */
    @POST(RequestUrl.ROCKET_VERIFY_SIGN)
    Observable<BaseResponse<SudMGPAPPState.AppCustomRocketVerifySign.Data>> rocketVerifySign(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SudMGPMGState.MGCustomRocketVerifySign body);

}
