package tech.sud.mgp.hello.ui.main.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.common.http.param.RequestUrl;
import tech.sud.mgp.hello.ui.main.http.req.MatchBodyReq;
import tech.sud.mgp.hello.ui.main.http.req.UserInfoReq;
import tech.sud.mgp.hello.ui.main.http.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.main.http.resp.GameListResp;
import tech.sud.mgp.hello.ui.main.http.resp.RoomListResp;
import tech.sud.mgp.hello.ui.main.http.resp.UserInfoListResp;
import tech.sud.mgp.hello.ui.main.model.MatchRoomModel;

/**
 * 网络请求方法和地址
 */
public interface HomeRequestMethod {

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
    
    /**
     * 游戏列表
     */
    @POST(RequestUrl.GAME_LIST)
    Observable<BaseResponse<GameListResp>> gameList(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

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
}
