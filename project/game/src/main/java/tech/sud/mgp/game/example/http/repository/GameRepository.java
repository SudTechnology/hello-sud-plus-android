package tech.sud.mgp.game.example.http.repository;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.common.http.param.BaseUrlManager;
import tech.sud.mgp.common.http.rx.RxCallback;
import tech.sud.mgp.common.http.rx.RxUtil;
import tech.sud.mgp.game.example.http.method.GameRequestMethodFactory;
import tech.sud.mgp.game.example.http.resp.GameLoginResp;

public class GameRepository {

    /**
     * 游戏登录
     *
     * @param owner    生命周期对象
     * @param callback 回调
     */
    public static void gameLogin(LifecycleOwner owner, RxCallback<GameLoginResp> callback) {
        GameRequestMethodFactory.getMethod()
                .gameLogin(BaseUrlManager.getBaseUrl())
                .compose(RxUtil.schedulers(owner))
                .subscribe(callback);
    }

}