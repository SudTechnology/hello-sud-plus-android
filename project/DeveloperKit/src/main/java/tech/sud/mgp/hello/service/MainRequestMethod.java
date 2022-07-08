package tech.sud.mgp.hello.service;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;

/**
 * 网络请求方法和地址
 */
public interface MainRequestMethod {

    /** 游戏登录URL */
    String GAME_LOGIN = "login/v3";

    /**
     * 游戏登录
     */
    @POST(GAME_LOGIN)
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GameLoginReq req);

}
