package tech.sud.mgp.hello.service.room.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.room.req.CrossAppCancelMatchReq;
import tech.sud.mgp.hello.service.room.req.CrossAppJoinTeamReq;
import tech.sud.mgp.hello.service.room.req.CrossAppQuitTeamReq;
import tech.sud.mgp.hello.service.room.req.CrossAppStartMatchReq;
import tech.sud.mgp.hello.service.room.req.CrossAppSwitchGameReq;
import tech.sud.mgp.hello.service.room.req.DanmakuListReq;
import tech.sud.mgp.hello.service.room.req.DeductionCoinReq;
import tech.sud.mgp.hello.service.room.req.DiscoAnchorListReq;
import tech.sud.mgp.hello.service.room.req.DiscoSwitchAnchorReq;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.GiftListReq;
import tech.sud.mgp.hello.service.room.req.LeaguePlayingReq;
import tech.sud.mgp.hello.service.room.req.QuizGamePlayerReq;
import tech.sud.mgp.hello.service.room.req.RobotListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderCreateReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderReceiveReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgainReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgreeReq;
import tech.sud.mgp.hello.service.room.req.RoomPkDurationReq;
import tech.sud.mgp.hello.service.room.req.RoomPkRemoveRivalReq;
import tech.sud.mgp.hello.service.room.req.RoomPkStartReq;
import tech.sud.mgp.hello.service.room.req.RoomPkSwitchReq;
import tech.sud.mgp.hello.service.room.req.SendDanmakuReq;
import tech.sud.mgp.hello.service.room.req.SendGiftReq;
import tech.sud.mgp.hello.service.room.resp.CrossAppStartMatchResp;
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.LeaguePlayingResp;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkStartResp;

/**
 * 网络请求方法和地址
 */
public interface AudioRequestMethod {

    /**
     * 进入房间
     */
    @POST(RequestUrl.ENTER_ROOM)
    Observable<BaseResponse<EnterRoomResp>> enterRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body EnterRoomReq req);

    /**
     * 查询麦位列表接口
     */
    @POST(RequestUrl.ROOM_MIC_LIST)
    Observable<BaseResponse<RoomMicListResp>> roomMicList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomMicListReq req);

    /**
     * 房间上下麦接口
     */
    @POST(RequestUrl.ROOM_MIC_SWITCH)
    Observable<BaseResponse<RoomMicSwitchResp>> roomMicSwitch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomMicSwitchReq req);

    /**
     * 退出房间
     */
    @POST(RequestUrl.EXIT_ROOM)
    Observable<BaseResponse<Object>> exitRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body ExitRoomReq req);

    /**
     * 用户点单
     */
    @POST(RequestUrl.ROOM_ORDER_CREATE)
    Observable<BaseResponse<RoomOrderCreateResp>> roomOrderCreate(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomOrderCreateReq req);

    /**
     * 主播接单
     */
    @POST(RequestUrl.ROOM_ORDER_RECEIVE)
    Observable<BaseResponse<Object>> roomOrderReceive(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomOrderReceiveReq req);

    /**
     * 设置PK时长
     */
    @POST(RequestUrl.ROOM_PK_DURATION)
    Observable<BaseResponse<Object>> roomPkDuration(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkDurationReq req);

    /**
     * 设置PK开关
     */
    @POST(RequestUrl.ROOM_PK_SWITCH)
    Observable<BaseResponse<Object>> roomPkSwitch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkSwitchReq req);

    /**
     * 开始PK
     */
    @POST(RequestUrl.ROOM_PK_START)
    Observable<BaseResponse<RoomPkStartResp>> roomPkStart(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkStartReq req);

    /**
     * 同意PK
     */
    @POST(RequestUrl.ROOM_PK_AGREE)
    Observable<BaseResponse<RoomPkAgreeResp>> roomPkAgree(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkAgreeReq req);

    /**
     * 移除pk对手
     */
    @POST(RequestUrl.ROOM_PK_REMOVE_RIVAL)
    Observable<BaseResponse<Object>> roomPkRemoveRival(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkRemoveRivalReq req);

    /**
     * pk再来一局
     */
    @POST(RequestUrl.ROOM_PK_AGAIN)
    Observable<BaseResponse<RoomPkAgainResp>> roomPkAgain(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkAgainReq req);

    /**
     * 查询竞猜场景游戏玩家列表（房间内）
     */
    @POST(RequestUrl.QUIZ_GAME_PLAYER)
    Observable<BaseResponse<QuizGamePlayerResp>> quizGamePlayer(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body QuizGamePlayerReq req);

    /**
     * 弹幕列表
     */
    @POST(RequestUrl.DANMAKU_LIST)
    Observable<BaseResponse<DanmakuListResp>> danmakuList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DanmakuListReq req);

    /**
     * 发送弹幕
     */
    @POST(RequestUrl.SEND_DANMAKU)
    Observable<BaseResponse<Object>> sendDanmaku(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SendDanmakuReq req);

    /**
     * 送礼
     */
    @POST(RequestUrl.SEND_GIFT)
    Observable<BaseResponse<Object>> sendGift(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SendGiftReq req);

    /**
     * 礼物列表
     */
    @POST(RequestUrl.GIFT_LIST)
    Observable<BaseResponse<GiftListResp>> giftList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GiftListReq req);

    /**
     * 机器人列表
     */
    @POST(RequestUrl.ROBOT_LIST)
    Observable<BaseResponse<RobotListResp>> robotList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RobotListReq req);

    /**
     * 扣费
     */
    @POST(RequestUrl.DEDUCTION_COIN)
    Observable<BaseResponse<Object>> deductionCoin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DeductionCoinReq req);

    /**
     * 蹦迪主播列表
     */
    @POST(RequestUrl.DISCO_ANCHOR_LIST)
    Observable<BaseResponse<DiscoAnchorListResp>> discoAnchorList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DiscoAnchorListReq req);

    /**
     * 上/下主播位
     */
    @POST(RequestUrl.DISCO_SWITCH_ANCHOR)
    Observable<BaseResponse<Object>> discoSwitchAnchor(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DiscoSwitchAnchorReq req);

    // region 联赛

    /**
     * 查询进入前三的房间
     */
    @POST(RequestUrl.LEAGUE_PLAYING)
    Observable<BaseResponse<LeaguePlayingResp>> leaguePlaying(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body LeaguePlayingReq req);
    // endregion 联赛

    // region 跨域

    /** 加入组队 */
    @POST(RequestUrl.CROSS_APP_JOIN_TEAM)
    Observable<BaseResponse<Object>> crossAppJoinTeam(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CrossAppJoinTeamReq req);

    /** 开启匹配 */
    @POST(RequestUrl.CROSS_APP_START_MATCH)
    Observable<BaseResponse<CrossAppStartMatchResp>> crossAppStartMatch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CrossAppStartMatchReq req);

    /** 取消匹配 */
    @POST(RequestUrl.CROSS_APP_CANCEL_MATCH)
    Observable<BaseResponse<Object>> crossAppCancelMatch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CrossAppCancelMatchReq req);

    /** 退出组队 */
    @POST(RequestUrl.CROSS_APP_QUIT_TEAM)
    Observable<BaseResponse<Object>> crossAppQuitTeam(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CrossAppQuitTeamReq req);

    /** 切换游戏 */
    @POST(RequestUrl.CROSS_APP_SWITCH_GAME)
    Observable<BaseResponse<Object>> crossAppSwitchGame(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CrossAppSwitchGameReq req);
    // endregion 跨域

}
