package tech.sud.mgp.common.http.use.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.common.http.param.BaseUrlManager;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.common.http.use.method.CommonRequestMethodFactory;
import tech.sud.mgp.common.http.use.req.UserInfoReq;
import tech.sud.mgp.common.http.use.resp.UserInfoListResp;

public class CommonRepository {

    /**
     * 查询用户信息
     *
     * @param userIds userId数据集
     */
    public static void getUserInfoList(LifecycleOwner owner, List<Long> userIds, RxCallback<UserInfoListResp> callback) {
        UserInfoReq req = new UserInfoReq();
        req.userIds = userIds;
        CommonRequestMethodFactory.getMethod()
                .getUserInfoList(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
