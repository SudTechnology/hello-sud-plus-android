package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.gip.SudGIPWrapper.model.GameViewInfoModel;
import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 跨房pk的游戏业务
 */
public class CrossRoomGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>();

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        super.getGameRect(gameViewInfoModel);
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 157) + BarUtils.getStatusBarHeight();
    }

    // 开始按钮点击事件
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

}
