package tech.sud.mgp.hello.ui.game;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.SystemUtils;
import tech.sud.mgp.hello.common.utils.UserUtils;
import tech.sud.mgp.hello.service.GameLoginResp;
import tech.sud.mgp.hello.service.MainRepository;

/**
 * 游戏业务逻辑
 * ---接入方开发者，将下面的方法填空成自己的
 * ---外部调用switchGame()方法启动游戏
 */
public class QuickStartGameViewModel extends BaseGameViewModel {

    /** Sud平台申请的appId */
    public static String SudMGP_APP_ID = "1461564080052506636";
    /** Sud平台申请的appKey */
    public static String SudMGP_APP_KEY = "03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc";
    /** true 加载游戏时为测试环境 false 加载游戏时为生产环境 */
    public static final boolean GAME_IS_TEST_ENV = true;

    /** 使用的UserId,这里为随机生成演示，请使用业务真实userId */
    public static String userId = UserUtils.genUserID();

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>(); // 游戏View回调

    /** 向接入方服务器获取code */
    @Override
    protected void getCode(FragmentActivity activity, String userId, String appId, GameGetCodeListener listener) {
        MainRepository.login(activity, getUserId(), getAppId(), new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (t.getRet_code() == RetCode.SUCCESS && t.getData() != null) {
                    listener.onSuccess(t.getData().code);
                } else {
                    listener.onFailed();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onFailed();
            }
        });
    }

    /** 设置当前用户id(接入方定义) */
    @Override
    protected String getUserId() {
        return userId;
    }

    /** 设置Sud平台申请的appId */
    @Override
    protected String getAppId() {
        return SudMGP_APP_ID;
    }

    /** 设置Sud平台申请的appKey */
    @Override
    protected String getAppKey() {
        return SudMGP_APP_KEY;
    }

    /** 设置游戏的语言代码 */
    @Override
    protected String getLanguageCode() {
        return SystemUtils.getLanguageCode(Utils.getApp());
    }

    /** 设置游戏的安全操作区域 */
    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        // 相对于view_size（左、上、右、下）边框偏移（单位像素）
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 54) + BarUtils.getStatusBarHeight();
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 54);
    }

    /**
     * true 加载游戏时为测试环境
     * false 加载游戏时为生产环境
     */
    @Override
    protected boolean isTestEnv() {
        return GAME_IS_TEST_ENV;
    }

    /** 将游戏View添加到页面中 */
    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    /** 将页面中的游戏View移除 */
    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

}
