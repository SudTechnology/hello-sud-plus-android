package tech.sud.mgp.hello.service.room.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.room.req.DanmakuListReq;
import tech.sud.mgp.hello.service.room.req.DeductionCoinReq;
import tech.sud.mgp.hello.service.room.req.DiscoAnchorListReq;
import tech.sud.mgp.hello.service.room.req.DiscoSwitchAnchorReq;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.GiftListReq;
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
import tech.sud.mgp.hello.service.room.resp.DanmakuListResp;
import tech.sud.mgp.hello.service.room.resp.DiscoAnchorListResp;
import tech.sud.mgp.hello.service.room.resp.EnterRoomResp;
import tech.sud.mgp.hello.service.room.resp.GiftListResp;
import tech.sud.mgp.hello.service.room.resp.QuizGamePlayerResp;
import tech.sud.mgp.hello.service.room.resp.RobotListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicListResp;
import tech.sud.mgp.hello.service.room.resp.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgainResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.resp.RoomPkStartResp;

/**
 * ???????????????????????????
 */
public interface AudioRequestMethod {

    /**
     * ????????????
     */
    @POST(RequestUrl.ENTER_ROOM)
    Observable<BaseResponse<EnterRoomResp>> enterRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body EnterRoomReq req);

    /**
     * ????????????????????????
     */
    @POST(RequestUrl.ROOM_MIC_LIST)
    Observable<BaseResponse<RoomMicListResp>> roomMicList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomMicListReq req);

    /**
     * ?????????????????????
     */
    @POST(RequestUrl.ROOM_MIC_SWITCH)
    Observable<BaseResponse<RoomMicSwitchResp>> roomMicSwitch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomMicSwitchReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.EXIT_ROOM)
    Observable<BaseResponse<Object>> exitRoom(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body ExitRoomReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.ROOM_ORDER_CREATE)
    Observable<BaseResponse<RoomOrderCreateResp>> roomOrderCreate(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomOrderCreateReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.ROOM_ORDER_RECEIVE)
    Observable<BaseResponse<Object>> roomOrderReceive(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomOrderReceiveReq req);

    /**
     * ??????PK??????
     */
    @POST(RequestUrl.ROOM_PK_DURATION)
    Observable<BaseResponse<Object>> roomPkDuration(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkDurationReq req);

    /**
     * ??????PK??????
     */
    @POST(RequestUrl.ROOM_PK_SWITCH)
    Observable<BaseResponse<Object>> roomPkSwitch(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkSwitchReq req);

    /**
     * ??????PK
     */
    @POST(RequestUrl.ROOM_PK_START)
    Observable<BaseResponse<RoomPkStartResp>> roomPkStart(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkStartReq req);

    /**
     * ??????PK
     */
    @POST(RequestUrl.ROOM_PK_AGREE)
    Observable<BaseResponse<RoomPkAgreeResp>> roomPkAgree(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkAgreeReq req);

    /**
     * ??????pk??????
     */
    @POST(RequestUrl.ROOM_PK_REMOVE_RIVAL)
    Observable<BaseResponse<Object>> roomPkRemoveRival(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkRemoveRivalReq req);

    /**
     * pk????????????
     */
    @POST(RequestUrl.ROOM_PK_AGAIN)
    Observable<BaseResponse<RoomPkAgainResp>> roomPkAgain(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RoomPkAgainReq req);

    /**
     * ???????????????????????????????????????????????????
     */
    @POST(RequestUrl.QUIZ_GAME_PLAYER)
    Observable<BaseResponse<QuizGamePlayerResp>> quizGamePlayer(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body QuizGamePlayerReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.DANMAKU_LIST)
    Observable<BaseResponse<DanmakuListResp>> danmakuList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DanmakuListReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.SEND_DANMAKU)
    Observable<BaseResponse<Object>> sendDanmaku(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SendDanmakuReq req);

    /**
     * ??????
     */
    @POST(RequestUrl.SEND_GIFT)
    Observable<BaseResponse<Object>> sendGift(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body SendGiftReq req);

    /**
     * ????????????
     */
    @POST(RequestUrl.GIFT_LIST)
    Observable<BaseResponse<GiftListResp>> giftList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GiftListReq req);

    /**
     * ???????????????
     */
    @POST(RequestUrl.ROBOT_LIST)
    Observable<BaseResponse<RobotListResp>> robotList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RobotListReq req);

    /**
     * ??????
     */
    @POST(RequestUrl.DEDUCTION_COIN)
    Observable<BaseResponse<Object>> deductionCoin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DeductionCoinReq req);

    /**
     * ??????????????????
     */
    @POST(RequestUrl.DISCO_ANCHOR_LIST)
    Observable<BaseResponse<DiscoAnchorListResp>> discoAnchorList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DiscoAnchorListReq req);

    /**
     * ???/????????????
     */
    @POST(RequestUrl.DISCO_SWITCH_ANCHOR)
    Observable<BaseResponse<Object>> discoSwitchAnchor(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body DiscoSwitchAnchorReq req);

}
