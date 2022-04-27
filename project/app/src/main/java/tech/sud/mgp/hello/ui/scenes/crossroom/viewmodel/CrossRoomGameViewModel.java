package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.hello.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;

/**
 * 跨房pk的游戏业务
 */
public class CrossRoomGameViewModel extends GameViewModel {

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        super.getGameRect(gameViewInfoModel);
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 167) + BarUtils.getStatusBarHeight();
    }

}
