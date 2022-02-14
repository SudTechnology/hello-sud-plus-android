package tech.sud.mgp.hello.ui.game.middle.manager;

import android.view.View;
import android.view.ViewTreeObserver;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.JsonObject;

import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.game.http.repository.GameRepository;
import tech.sud.mgp.hello.ui.game.http.resp.GameLoginResp;
import tech.sud.mgp.hello.ui.game.middle.model.GameConfigModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameViewInfoModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameViewRectModel;
import tech.sud.mgp.hello.ui.game.middle.model.GameViewSizeModel;
import tech.sud.mgp.hello.ui.game.middle.state.MGStateResponse;

/**
 * 用于处理游戏SDK部分回调业务
 */
public class SudFSMMGManager {

    /**
     * 处理code过期
     */
    public void processOnExpireCode(FsmApp2MgManager fsmApp2MGManager, ISudFSMStateHandle handle) {
        // code过期，刷新code
        GameRepository.gameLogin(null, new RxCallback<GameLoginResp>() {
            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = t.getRetCode();
                if (t.getRetCode() == RetCode.SUCCESS && t.getData() != null) {
                    fsmApp2MGManager.updateCode(t.getData().code, null);
                    handle.success(GsonUtils.toJson(mgStateResponse));
                } else {
                    handle.failure(GsonUtils.toJson(mgStateResponse));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                MGStateResponse mgStateResponse = new MGStateResponse();
                mgStateResponse.ret_code = -1;
                handle.failure(GsonUtils.toJson(mgStateResponse));
            }
        });
    }

    /**
     * 处理游戏视图
     */
    public void processOnGetGameViewInfo(View gameView, ISudFSMStateHandle handle) {
        //拿到游戏View的宽高
        int gameViewWidth = gameView.getMeasuredWidth();
        int gameViewHeight = gameView.getMeasuredHeight();
        if (gameViewWidth > 0 && gameViewHeight > 0) {
            notifyGameViewInfo(handle, gameViewWidth, gameViewHeight);
            return;
        }

        //如果游戏View未加载完成，则监听加载完成时回调
        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = gameView.getMeasuredWidth();
                int height = gameView.getMeasuredHeight();
                notifyGameViewInfo(handle, width, height);
            }
        });
    }

    /**
     * 通知游戏，游戏视图信息
     */
    private void notifyGameViewInfo(ISudFSMStateHandle handle, int gameViewWidth, int gameViewHeight) {
        GameViewInfoModel gameViewInfoModel = new GameViewInfoModel();
        gameViewInfoModel.ret_code = 0;
        // 游戏View大小
        gameViewInfoModel.view_size = new GameViewSizeModel();
        gameViewInfoModel.view_size.width = gameViewWidth;
        gameViewInfoModel.view_size.height = gameViewHeight;

        // 游戏安全操作区域
        gameViewInfoModel.view_game_rect = new GameViewRectModel();
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 110);
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 160);

        // 给游戏侧进行返回
        handle.success(GsonUtils.toJson(gameViewInfoModel));
    }

    /**
     * 处理游戏配置
     */
    public void processOnGetGameCfg(ISudFSMStateHandle handle, String dataJson) {
        JsonObject jsonObject = new JsonObject();
        GameConfigModel gameConfigModel = new GameConfigModel();
        // 配置不展示大厅玩家展示位
        gameConfigModel.ui.lobby_players.hide = true;
        handle.success(jsonObject.toString());
    }

}
