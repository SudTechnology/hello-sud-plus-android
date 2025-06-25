package tech.sud.mgp.hello.service.main.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.main.req.AuthMatchRoomReq;
import tech.sud.mgp.hello.service.main.req.AuthRoomListReq;
import tech.sud.mgp.hello.service.main.req.CreatRoomReq;
import tech.sud.mgp.hello.service.main.req.GameListReq;
import tech.sud.mgp.hello.service.main.req.MatchBodyReq;
import tech.sud.mgp.hello.service.main.req.QuizBetReq;
import tech.sud.mgp.hello.service.main.req.RoomListReq;
import tech.sud.mgp.hello.service.main.req.TicketConfirmJoinReq;
import tech.sud.mgp.hello.service.main.req.UserInfoReq;
import tech.sud.mgp.hello.service.main.resp.AuthMatchRoomResp;
import tech.sud.mgp.hello.service.main.resp.AuthRoomListResp;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.service.main.resp.CheckUpgradeResp;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.CrossAppGameListResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.main.resp.GetAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.GetBannerResp;
import tech.sud.mgp.hello.service.main.resp.QuizGameListResp;
import tech.sud.mgp.hello.service.main.resp.RandomAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.service.main.resp.SaveAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.TicketConfirmJoinResp;
import tech.sud.mgp.hello.service.main.resp.UpdateAiCloneResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoListResp;
import tech.sud.mgp.hello.service.room.req.AddCoinReq;
import tech.sud.mgp.hello.service.room.req.GetAiCloneReq;
import tech.sud.mgp.hello.service.room.req.RandomAiCloneReq;
import tech.sud.mgp.hello.service.room.req.SaveAiCloneReq;
import tech.sud.mgp.hello.service.room.req.UpdateAiCloneReq;
import tech.sud.mgp.hello.service.room.req.WearNftReq;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;

/**
 * 网络请求方法和地址
 */
public interface HomeRequestMethod {

    /**
     * 房间列表
     */
    @POST(RequestUrl.ROOM_LIST)
    Observable<BaseResponse<RoomListResp>> roomList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomListReq body);

    /**
     * 匹配游戏
     */
    @POST(RequestUrl.ROOM_MATCH)
    Observable<BaseResponse<MatchRoomModel>> matchGame(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body MatchBodyReq body);

    /**
     * 游戏列表
     */
    @POST(RequestUrl.GAME_LIST)
    Observable<BaseResponse<GameListResp>> gameList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 游戏列表V2
     */
    @POST(RequestUrl.GAME_LIST_V2)
    Observable<BaseResponse<GameListResp>> gameListV2(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GameListReq body);

    /**
     * 批量查询用户信息
     */
    @POST(RequestUrl.USER_INFO_LIST)
    Observable<BaseResponse<UserInfoListResp>> getUserInfoList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body UserInfoReq body);

    /**
     * 查询基础配置
     */
    @POST(RequestUrl.GET_BASE_CONFIG)
    Observable<BaseResponse<BaseConfigResp>> getBaseConfig(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 创建房间
     */
    @POST(RequestUrl.CREAT_ROOM)
    Observable<BaseResponse<CreatRoomResp>> creatRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CreatRoomReq body);

    /**
     * 查询用户账户
     */
    @POST(RequestUrl.GET_ACCOUNT)
    Observable<BaseResponse<GetAccountResp>> getAccount(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 确认加入门票游戏
     */
    @POST(RequestUrl.TICKET_CONFIRM_JOIN)
    Observable<BaseResponse<TicketConfirmJoinResp>> ticketConfirmJoin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body TicketConfirmJoinReq body);

    /**
     * 检查更新
     */
    @POST(RequestUrl.CHECK_UPGRADE)
    Observable<BaseResponse<CheckUpgradeResp>> checkUpgrade(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 下注
     */
    @POST(RequestUrl.QUIZ_BET)
    Observable<BaseResponse<Object>> quizBet(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body QuizBetReq body);

    /**
     * 查询竞猜游戏列表
     */
    @POST(RequestUrl.QUIZ_GAME_LIST)
    Observable<BaseResponse<QuizGameListResp>> quizGameList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 授权房间列表
     */
    @POST(RequestUrl.AUTH_ROOM_LIST)
    Observable<BaseResponse<AuthRoomListResp>> authRoomList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body AuthRoomListReq body);

    /**
     * 跨域匹配房间
     */
    @POST(RequestUrl.AUTH_MATCH_ROOM)
    Observable<BaseResponse<AuthMatchRoomResp>> authMatchRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body AuthMatchRoomReq body);

    /**
     * 穿戴NFT
     */
    @POST(RequestUrl.WEAR_NFT)
    Observable<BaseResponse<Object>> wearNFT(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body WearNftReq req);

    /**
     * 跨域游戏列表
     */
    @POST(RequestUrl.CROSS_APP_GAME_LIST)
    Observable<BaseResponse<CrossAppGameListResp>> crossAppGameList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 获取首页banner信息
     */
    @POST(RequestUrl.GET_BANNER)
    Observable<BaseResponse<GetBannerResp>> getBanner(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 加金币
     */
    @POST(RequestUrl.ADD_COIN)
    Observable<BaseResponse<Object>> addCoin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body AddCoinReq req);

    /**
     * 保存AI（分身）
     */
    @POST(RequestUrl.SAVE_AI_CLONE)
    Observable<BaseResponse<SaveAiCloneResp>> saveAiClone(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SaveAiCloneReq req);

    /**
     * 查询AI（分身）
     */
    @POST(RequestUrl.GET_AI_CLONE)
    Observable<BaseResponse<GetAiCloneResp>> getAiClone(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GetAiCloneReq req);

    /**
     * 修改AI状态（分身）
     */
    @POST(RequestUrl.UPDATE_AI_CLONE)
    Observable<BaseResponse<UpdateAiCloneResp>> updateAiClone(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body UpdateAiCloneReq req);

    /**
     * 随机AI（分身）
     */
    @POST(RequestUrl.RANDOM_AI_CLONE)
    Observable<BaseResponse<RandomAiCloneResp>> randomAiClone(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RandomAiCloneReq req);

}
