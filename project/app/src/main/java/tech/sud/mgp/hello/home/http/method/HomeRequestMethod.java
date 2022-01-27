package tech.sud.mgp.hello.home.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.IBaseUrl;
import tech.sud.mgp.common.http.param.RequestUrl;
import tech.sud.mgp.hello.home.http.req.MatchBodyReq;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;
import tech.sud.mgp.hello.home.model.MatchRoomModel;
import tech.sud.mgp.hello.login.http.req.LoginRequestBody;

/**
 * 网络请求方法和地址
 */
public interface HomeRequestMethod {

    /**
     * 游戏列表
     */
    @POST(RequestUrl.GAME_LIST)
    Observable<BaseResponse<GameListResp>> gameList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 房间列表
     */
    @POST(RequestUrl.ROOM_LIST)
    Observable<BaseResponse<RoomListResp>> roomList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

    /**
     * 匹配游戏
     */
    @POST(RequestUrl.ROOM_MATCH)
    Observable<BaseResponse<MatchRoomModel>> matchGame(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body MatchBodyReq body);

}
