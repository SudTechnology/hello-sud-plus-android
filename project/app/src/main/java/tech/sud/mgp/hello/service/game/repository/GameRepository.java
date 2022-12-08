package tech.sud.mgp.hello.service.game.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.service.game.method.GameRequestMethodFactory;
import tech.sud.mgp.hello.service.game.req.BringChipReq;
import tech.sud.mgp.hello.service.game.req.SwitchGameReq;
import tech.sud.mgp.hello.service.game.resp.GameLoginResp;

public class GameRepository {

    /**
     * 游戏登录
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, RxCallback<GameLoginResp> callback) {
        GameRequestMethodFactory.getMethod()
                .gameLogin(BaseUrlManager.getGameBaseUrl())
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

    /**
     * 带入筹码到游戏
     *
     * @param owner            生命周期对象
     * @param mgId             游戏id
     * @param roomId           roomId
     * @param roundId          订单关联的游戏局id
     * @param lastRoundScore   本人当前积分
     * @param incrementalScore 充值积分
     * @param totalScore       充值后总积分
     * @param callback         回调
     */
    public static void bringChip(LifecycleOwner owner, long mgId, String roomId, String roundId, long lastRoundScore,
                                 long incrementalScore, long totalScore, RxCallback<Object> callback) {
        BringChipReq req = new BringChipReq();
        req.mgId = mgId;
        req.roomId = roomId;
        req.roundId = roundId;
        req.lastRoundScore = lastRoundScore;
        req.incrementalScore = incrementalScore;
        req.totalScore = totalScore;
        GameRequestMethodFactory.getMethod()
                .bringChip(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
