package tech.sud.mgp.hello.service.game.repository;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.service.game.method.GameRequestMethodFactory;
import tech.sud.mgp.hello.service.game.req.GameLoginReq;
import tech.sud.mgp.hello.service.game.req.SwitchGameReq;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;
import tech.sud.mgp.hello.service.main.config.SudConfig;

public class GameRepository {

    /**
     * 游戏登录
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String userId, String appId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.user_id = userId;
        req.app_id = appId;
        GameRequestMethodFactory.getMethod()
                .gameLogin(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * sudAppId列表
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void sudAppList(LifecycleOwner owner, RxCallback<List<SudConfig>> callback) {
        GameRequestMethodFactory.getMethod()
                .sudAppList(BaseUrlManager.getGameBaseUrl())
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    /**
     * 游戏切换
     *
     * @param owner    生命周期对象
     * @param roomId   房间id
     * @param gameId   游戏id
     * @param callback 回调
     */
    public static void switchGame(LifecycleOwner owner, long roomId, long gameId, RxCallback<Object> callback) {
        SwitchGameReq switchGameReq = new SwitchGameReq();
        switchGameReq.roomId = roomId;
        switchGameReq.gameId = gameId;
        GameRequestMethodFactory.getMethod()
                .switchGame(BaseUrlManager.getInteractBaseUrl(), switchGameReq)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
