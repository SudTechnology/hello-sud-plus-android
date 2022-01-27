package tech.sud.mgp.hello.home.http.repository;


import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.common.http.param.BaseUrlManager;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.hello.home.http.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.home.http.req.MatchBodyReq;
import tech.sud.mgp.hello.home.http.resp.GameListResp;
import tech.sud.mgp.hello.home.http.resp.RoomListResp;
import tech.sud.mgp.hello.home.model.MatchRoomModel;

public class HomeRepository {

    /**
     * 游戏列表
     */
    public static void gameList(LifecycleOwner owner, RxCallback<GameListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .gameList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 房间列表
     */
    public static void roomList(LifecycleOwner owner, RxCallback<RoomListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .roomList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 匹配游戏
     */
    public static void matchGame(Integer sceneType,Long gameId, LifecycleOwner owner, RxCallback<MatchRoomModel> callback) {
        MatchBodyReq req = new MatchBodyReq();
        req.gameId = gameId;
        req.sceneType = sceneType;
        HomeRequestMethodFactory.getMethod()
                .matchGame(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
