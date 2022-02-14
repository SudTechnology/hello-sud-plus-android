package tech.sud.mgp.hello.ui.room.audio.example.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.ui.room.audio.example.http.req.EnterRoomReq;
import tech.sud.mgp.hello.ui.room.audio.example.http.req.ExitRoomReq;
import tech.sud.mgp.hello.ui.room.audio.example.http.req.RoomMicListReq;
import tech.sud.mgp.hello.ui.room.audio.example.http.req.RoomMicSwitchReq;
import tech.sud.mgp.hello.ui.room.audio.example.http.response.EnterRoomResp;
import tech.sud.mgp.hello.ui.room.audio.example.http.response.RoomMicListResp;
import tech.sud.mgp.hello.ui.room.audio.example.http.response.RoomMicSwitchResp;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.common.http.param.RequestUrl;

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

}
