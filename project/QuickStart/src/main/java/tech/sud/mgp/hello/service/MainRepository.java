package tech.sud.mgp.hello.service;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.ui.main.GameModel;

public class MainRepository {

    private static final MainRequestMethod method = RetrofitManager.createMethod(MainRequestMethod.class);

    /** 获取游戏列表 */
    public static List<GameModel> getGameList() {
        ArrayList<GameModel> list = new ArrayList<>();
        list.add(buildGameModel(1461227817776713818L, "碰碰我最强", R.drawable.ppwzq, R.drawable.ic_ppwzq));
        list.add(buildGameModel(1461228379255603251L, "飞镖达人", R.drawable.fbdr, R.drawable.ic_fbdr));
        list.add(buildGameModel(1461228410184400899L, "你画我猜", R.drawable.nhwc, R.drawable.ic_nhwc));
        list.add(buildGameModel(1461297734886621238L, "五子棋", R.drawable.wzq, R.drawable.ic_wzq));
        list.add(buildGameModel(1461297789198663710L, "黑白棋", R.drawable.hbq, R.drawable.ic_hbq));
        list.add(buildGameModel(1468090257126719572L, "短道速滑", R.drawable.ddsh, R.drawable.ic_ddsh));
        list.add(buildGameModel(1468091457989509190L, "数字炸弹", R.drawable.szzd, R.drawable.ic_szzd));
        list.add(buildGameModel(1468180338417074177L, "飞行棋", R.drawable.fxq, R.drawable.ic_fxq));
        list.add(buildGameModel(1468434401847222273L, "扫雷", R.drawable.sl, R.drawable.ic_sl));
        list.add(buildGameModel(1468434504892882946L, "你说我猜", R.drawable.nswc, R.drawable.ic_nswc));
        list.add(buildGameModel(1468434637562912769L, "数字转轮", R.drawable.szzl, R.drawable.ic_szzl));
        list.add(buildGameModel(1468434723902660610L, "石头剪刀布", R.drawable.stjdb, R.drawable.ic_stjdb));
        list.add(buildGameModel(1472142478505271298L, "TeenPatti", R.drawable.teen_patti, R.drawable.ic_teen_patti));
        list.add(buildGameModel(1472142559912517633L, "UMO", R.drawable.umo, R.drawable.ic_umo));
        list.add(buildGameModel(1472142640866779138L, "排雷兵", R.drawable.plb, R.drawable.ic_plb));
        list.add(buildGameModel(1472142695162044417L, "台湾麻将", R.drawable.twmj, R.drawable.ic_twmj));
        list.add(buildGameModel(1472142747708284929L, "狼人杀", R.drawable.lrs, R.drawable.ic_lrs));
        list.add(buildGameModel(1490944230389182466L, "友尽闯关", R.drawable.picopark, R.drawable.ic_picopark));
        list.add(buildGameModel(1490944604005199873L, "大话骰", R.drawable.dice, R.drawable.ic_dice));
        list.add(buildGameModel(1494212349664686081L, "德州扑克", R.drawable.dz, R.drawable.ic_dz));
        return list;
    }

    /** 构建GameModel */
    public static GameModel buildGameModel(long gameId, String gameName, int homeGamePic, int gamePic) {
        GameModel model = new GameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.homeGamePic = homeGamePic;
        model.gamePic = gamePic;
        return model;
    }

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
