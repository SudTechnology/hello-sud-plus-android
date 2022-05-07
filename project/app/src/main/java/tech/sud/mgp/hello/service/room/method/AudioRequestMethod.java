package tech.sud.mgp.hello.service.room.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.room.req.EnterRoomReq;
import tech.sud.mgp.hello.service.room.req.ExitRoomReq;
import tech.sud.mgp.hello.service.room.req.RoomMicListReq;
import tech.sud.mgp.hello.service.room.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderCreateReq;
import tech.sud.mgp.hello.service.room.req.RoomOrderReceiveReq;
import tech.sud.mgp.hello.service.room.req.RoomPkAgreeReq;
import tech.sud.mgp.hello.service.room.req.RoomPkDurationReq;
import tech.sud.mgp.hello.service.room.req.RoomPkRemoveRivalReq;
import tech.sud.mgp.hello.service.room.req.RoomPkStartReq;
import tech.sud.mgp.hello.service.room.req.RoomPkSwitchReq;
import tech.sud.mgp.hello.service.room.response.EnterRoomResp;
import tech.sud.mgp.hello.service.room.response.RoomMicListResp;
import tech.sud.mgp.hello.service.room.response.RoomMicSwitchResp;
import tech.sud.mgp.hello.service.room.response.RoomOrderCreateResp;
import tech.sud.mgp.hello.service.room.response.RoomPkAgreeResp;
import tech.sud.mgp.hello.service.room.response.RoomPkStartResp;

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

}
