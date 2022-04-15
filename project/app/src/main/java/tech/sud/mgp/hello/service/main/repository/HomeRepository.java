package tech.sud.mgp.hello.service.main.repository;


import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.common.model.AppData;
import tech.sud.mgp.hello.service.main.method.HomeRequestMethodFactory;
import tech.sud.mgp.hello.service.main.req.CreatRoomReq;
import tech.sud.mgp.hello.service.main.req.MatchBodyReq;
import tech.sud.mgp.hello.service.main.req.TicketConfirmJoinReq;
import tech.sud.mgp.hello.service.main.req.UserInfoReq;
import tech.sud.mgp.hello.service.main.resp.BaseConfigResp;
import tech.sud.mgp.hello.service.main.resp.CheckUpgradeResp;
import tech.sud.mgp.hello.service.main.resp.CreatRoomResp;
import tech.sud.mgp.hello.service.main.resp.GameListResp;
import tech.sud.mgp.hello.service.main.resp.GetAccountResp;
import tech.sud.mgp.hello.service.main.resp.RoomListResp;
import tech.sud.mgp.hello.service.main.resp.TicketConfirmJoinResp;
import tech.sud.mgp.hello.service.main.resp.UserInfoListResp;
import tech.sud.mgp.hello.ui.main.home.model.MatchRoomModel;

public class HomeRepository {

    /**
     * 房间列表
     */
    public static void roomList(LifecycleOwner owner, RxCallback<RoomListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .roomList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 匹配游戏
     *
     * @param gameLevel 门票场景游戏级别 从1开始
     */
    public static void matchGame(int sceneType, Long gameId, Integer gameLevel, LifecycleOwner owner, RxCallback<MatchRoomModel> callback) {
        MatchBodyReq req = new MatchBodyReq();
        req.gameId = gameId;
        req.sceneType = sceneType;
        req.rtcType = AppData.getInstance().getRtcType();
        req.gameLevel = gameLevel;
        HomeRequestMethodFactory.getMethod()
                .matchGame(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
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
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 游戏列表
     */
    public static void gameList(LifecycleOwner owner, RxCallback<GameListResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .gameList(BaseUrlManager.getInteractBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 基础配置
     */
    public static void getBaseConfig(LifecycleOwner owner, RxCallback<BaseConfigResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .getBaseConfig(BaseUrlManager.getBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 创建房间
     */
    public static void creatRoom(Integer sceneType, Integer gameLevel, LifecycleOwner owner, RxCallback<CreatRoomResp> callback) {
        CreatRoomReq req = new CreatRoomReq();
        req.rtcType = AppData.getInstance().getRtcType();
        req.sceneType = sceneType;
        req.gameLevel = gameLevel;
        HomeRequestMethodFactory.getMethod()
                .creatRoom(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 获取账户信息
     */
    public static void getAccount(LifecycleOwner owner, RxCallback<GetAccountResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .getAccount(BaseUrlManager.getBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 确认加入门票场景游戏
     */
    public static void ticketConfirmJoin(int sceneType, long roomId, long gameId, int gameLevel,
                                         LifecycleOwner owner, RxCallback<TicketConfirmJoinResp> callback) {
        TicketConfirmJoinReq req = new TicketConfirmJoinReq();
        req.sceneId = sceneType;
        req.roomId = roomId;
        req.gameId = gameId;
        req.gameLevel = gameLevel;
        HomeRequestMethodFactory.getMethod()
                .ticketConfirmJoin(BaseUrlManager.getInteractBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 检查更新
     */
    public static void checkUpgrade(LifecycleOwner owner, RxCallback<CheckUpgradeResp> callback) {
        HomeRequestMethodFactory.getMethod()
                .checkUpgrade(BaseUrlManager.getBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
