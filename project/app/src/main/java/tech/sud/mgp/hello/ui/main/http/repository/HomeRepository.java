package tech.sud.mgp.hello.ui.main.http.repository;


import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtil;
import tech.sud.mgp.hello.ui.main.http.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.ui.main.http.req.MatchBodyReq;
import tech.sud.mgp.hello.ui.main.http.req.UserInfoReq;
import tech.sud.mgp.hello.ui.main.http.resp.BaseConfigResp;
import tech.sud.mgp.hello.ui.main.http.resp.GameListResp;
import tech.sud.mgp.hello.ui.main.http.resp.RoomListResp;
import tech.sud.mgp.hello.ui.main.http.resp.UserInfoListResp;
import tech.sud.mgp.hello.ui.main.model.MatchRoomModel;

public class HomeRepository {

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
    public static void matchGame(Integer sceneType, Long gameId, LifecycleOwner owner, RxCallback<MatchRoomModel> callback) {
        MatchBodyReq req = new MatchBodyReq();
        req.gameId = gameId;
        req.sceneType = sceneType;
        HomeRequestMethodFactory.getMethod()
                .matchGame(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 查询用户信息
     *
     * @param userIds userId数据集
     */
    public static void getUserInfoList(LifecycleOwner owner, List<Long> userIds, RxCallback<UserInfoListResp> callback) {
        UserInfoReq req = new UserInfoReq();
        req.userIds = userIds;
        HomeRequestMethodFactory.getMethod()
                .getUserInfoList(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

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
     * 基础配置
     */
    public static void getBaseConfig(LifecycleOwner owner, RxCallback<BaseConfigResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .getBaseConfig(BaseUrlManager.getBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}