package tech.sud.mgp.hello.service.login.method;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.IBaseUrl;
import tech.sud.mgp.hello.common.http.param.RequestUrl;
import tech.sud.mgp.hello.service.login.req.LoginRequestBody;
import tech.sud.mgp.hello.service.login.resp.LoginResponse;

/**
 * 网络请求方法和地址
 */
public interface LoginRequestMethod {

    /**
     * 登录
     */
    @POST(RequestUrl.LOGIN)
    Observable<BaseResponse<LoginResponse>> login(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body LoginRequestBody body);

}
