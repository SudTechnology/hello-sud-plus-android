package tech.sud.mgp.hello.home.http.repository;


import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.common.http.param.BaseUrlManager;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.hello.home.http.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;

public class HomeRepository {

    public static void gameList(LifecycleOwner owner, RxCallback<GameListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .gameList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    public static void roomList(LifecycleOwner owner, RxCallback<RoomListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .roomList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
