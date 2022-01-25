package tech.sud.mgp.hello.home.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.RequestUrl;
import tech.sud.mgp.hello.home.http.req.EnterRoomReq;
import tech.sud.mgp.hello.home.http.resp.EnterRoomResp;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;

/**
 * 网络请求方法和地址
 */
public interface HomeRequestMethod {

    /**
     * 游戏列表
     */
    @POST(RequestUrl.GAMELIST)
    Observable<BaseResponse<GameListResp>> gameList();

    /**
     * 房间列表
     */
    @POST(RequestUrl.ROOMLIST)
    Observable<BaseResponse<RoomListResp>> roomList();

    /**
     * 进入房间
     */
    @POST(RequestUrl.ENTERROOM)
    Observable<BaseResponse<EnterRoomResp>> enterRoom(@Body EnterRoomReq req);

}
