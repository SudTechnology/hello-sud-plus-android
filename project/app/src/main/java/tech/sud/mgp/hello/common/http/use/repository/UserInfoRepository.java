package tech.sud.mgp.hello.common.http.use.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.use.resp.UserInfoListResp;
import tech.sud.mgp.hello.common.http.use.resp.UserInfoResp;

public class UserInfoRepository {

    public static void getUserInfoList(LifecycleOwner owner, List<Long> userIds, UserInfoResultListener listener) {
        if (listener == null) return;
        CommonRepository.getUserInfoList(owner, userIds, new RxCallback<UserInfoListResp>() {
            @Override
            public void onNext(BaseResponse<UserInfoListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    UserInfoListResp userInfoListResp = t.getData();
                    if (userInfoListResp != null) {
                        listener.userInfoList(userInfoListResp.userInfoList);
                        return;
                    }
                }
                listener.userInfoList(null);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.userInfoList(null);
            }
        });
    }

    public interface UserInfoResultListener {
        void userInfoList(List<UserInfoResp> userInfos);
    }

}
