package tech.sud.mgp.hello.service.main.repository;


import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtil;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.main.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.service.main.req.CreatRoomReq;
import tech.sud.mgp.hello.service.main.req.MatchBodyReq;
import tech.sud.mgp.hello.service.main.req.UserInfoReq;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoListResp;
import tech.sud.mgp.hello.ui.main.home.MatchRoomModel;

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
        req.rtcType = AppData.getInstance().getRtcType();
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

    /**
     * 创建房间
     */
    public static void creatRoom(Integer sceneType, LifecycleOwner owner, RxCallback<CreatRoomResp> callback) {
        CreatRoomReq req = new CreatRoomReq();
        req.rtcType = AppData.getInstance().getRtcType();
        req.sceneType = sceneType;
        HomeRequestMethodFactory.getMethod()
                .creatRoom(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}
