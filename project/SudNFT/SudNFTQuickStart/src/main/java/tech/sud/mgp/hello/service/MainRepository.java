package tech.sud.mgp.hello.service;

import androidx.lifecycle.LifecycleOwner;

import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;

public class MainRepository {

    private static final MainRequestMethod method = RetrofitManager.createMethod(MainRequestMethod.class);

    /**
     * 接入方客户端调用接入方服务端获取短期令牌code（getCode）
     * { 接入方服务端仓库：https://github.com/SudTechnology/hello-sud-java }
     * ------ 暂时不使用此方法，改为使用okhttp直接请求数据
     *
     * @param owner    生命周期对象
     * @param userId   用户id
     * @param appId    SudMGP appId
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String userId, String appId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.user_id = userId;
        req.app_id = appId;
        method.gameLogin(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
