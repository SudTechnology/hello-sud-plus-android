package tech.sud.mgp.hello.service.main.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.service.main.resp.UserInfoListResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;

public class UserInfoRepository {

    public static void getUserInfoList(LifecycleOwner owner, List<Long> userIds, UserInfoResultListener listener) {
        if (listener == null) return;
        HomeRepository.getUserInfoList(owner, userIds, new RxCallback<UserInfoListResp>() {
            @Override
            public void onNext(BaseResponse<UserInfoListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    UserInfoListResp userInfoListResp = t.getData();
                    if (userInfoListResp != null) {
                        fixUserInfo(userInfoListResp.userInfoList);
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

    /**
     * 对数据做处理
     * 1，后端返回的nft头像，将直接赋值到avatar字段当中，避免多处修改
     *
     * @param list 用户列表数据
     */
    private static void fixUserInfo(List<UserInfoResp> list) {
        if (list == null) {
            return;
        }
        for (UserInfoResp userInfoResp : list) {
            userInfoResp.avatar = userInfoResp.getUseAvatar();
        }
    }

    public interface UserInfoResultListener {
        void userInfoList(List<UserInfoResp> userInfos);
    }

}
