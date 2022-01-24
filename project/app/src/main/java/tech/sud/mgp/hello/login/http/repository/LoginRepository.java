package tech.sud.mgp.hello.login.http.repository;


import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.hello.login.http.method.LoginRequestMethodFactory;
import tech.sud.mgp.hello.login.http.req.LoginRequestBody;
import tech.sud.mgp.hello.login.http.resp.LoginResponse;

public class LoginRepository {

    public static void login(LifecycleOwner owner, RxCallback<LoginResponse> callback) {
        // TODO: 2022/1/21 调用示例
        LoginRequestBody loginRequestBody = new LoginRequestBody();
        LoginRequestMethodFactory.getMethod()
                .login(loginRequestBody)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
        
        //下面是返回示例
//        new RxCallback<LoginResponse>() {
//            @Override
//            public void onNext(BaseResponse<LoginResponse> t) {
//                super.onNext(t);
//                if (t.getRetCode() == RetCode.SUCCESS) {
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//                // 网络请求异常
//            }
//        }
    }

}
