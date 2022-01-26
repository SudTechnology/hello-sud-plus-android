package tech.sud.mgp.audio.example.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tech.sud.mgp.audio.example.http.req.EnterRoomReq;
import tech.sud.mgp.audio.example.http.req.RoomMicListReq;
import tech.sud.mgp.audio.example.http.response.EnterRoomResp;
import tech.sud.mgp.audio.example.http.response.RoomMicListResp;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RequestUrl;

/**
 * 网络请求方法和地址
 */
public interface AudioRequestMethod {

    /**
     * 进入房间
     */
    @POST(RequestUrl.ENTER_ROOM)
    Observable<BaseResponse<EnterRoomResp>> enterRoom(@Body EnterRoomReq req);

    /**
     * 查询麦位列表接口
     */
    @POST(RequestUrl.ROOM_MIC_LIST)
    Observable<BaseResponse<RoomMicListResp>> roomMicList(@Body RoomMicListReq req);

}
