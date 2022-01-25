package tech.sud.mgp.hello.home.http.repository;


import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.hello.home.http.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.home.http.req.EnterRoomReq;
import tech.sud.mgp.hello.home.http.resp.EnterRoomResp;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;

public class HomeRepository {

    public static void gameList(LifecycleOwner owner, RxCallback<GameListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .gameList()
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    public static void roomList(LifecycleOwner owner, RxCallback<RoomListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .roomList()
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    public static void enterRoom(LifecycleOwner owner, long roomId, RxCallback<EnterRoomResp> callback) {
        EnterRoomReq req = new EnterRoomReq();
        req.setRoomId(roomId);
        HomeRequestMethodFactory.getMethod()
                .enterRoom(req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }
}
