package tech.sud.mgp.game.example.http.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.common.http.param.BaseResponse;
import tech.sud.mgp.common.http.param.IBaseUrl;
import tech.sud.mgp.common.http.param.RequestUrl;
import tech.sud.mgp.game.example.http.resp.GameLoginResp;

/**
 * 网络请求方法和地址
 */
public interface GameRequestMethod {

    /**
     * 游戏登录
     */
    @POST(RequestUrl.GAME_LOGIN)
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl);

}