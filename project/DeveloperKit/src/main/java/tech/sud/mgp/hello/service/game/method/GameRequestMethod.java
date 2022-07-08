package tech.sud.mgp.hello.service.game.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.service.base.RequestUrl;
import tech.sud.mgp.hello.service.game.req.SwitchGameReq;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;

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

}
