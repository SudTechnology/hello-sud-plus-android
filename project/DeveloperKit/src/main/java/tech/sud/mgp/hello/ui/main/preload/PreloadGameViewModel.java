package tech.sud.mgp.hello.ui.main.preload;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.DeveloperKitGameViewModel;

/**
 * 预加载游戏逻辑
 */
public class PreloadGameViewModel extends DeveloperKitGameViewModel {

    /** 获取游戏的安全操作区域 */
    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        gameViewInfoModel.view_game_rect.left = 0;
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 131) + BarUtils.getStatusBarHeight();
        gameViewInfoModel.view_game_rect.right = 0;
        gameViewInfoModel.view_game_rect.bottom = DensityUtils.dp2px(Utils.getApp(), 160);
    }

}
