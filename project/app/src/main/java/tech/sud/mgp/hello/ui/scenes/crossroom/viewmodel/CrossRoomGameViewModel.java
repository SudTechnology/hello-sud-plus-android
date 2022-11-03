package tech.sud.mgp.hello.ui.scenes.crossroom.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;

import tech.sud.mgp.SudMGPWrapper.model.GameViewInfoModel;
import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppRocketGameViewModel;

/**
 * 跨房pk的游戏业务
 */
public class CrossRoomGameViewModel extends AppRocketGameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>();

    @Override
    protected void getGameRect(GameViewInfoModel gameViewInfoModel) {
        super.getGameRect(gameViewInfoModel);
        gameViewInfoModel.view_game_rect.top = DensityUtils.dp2px(Utils.getApp(), 157) + BarUtils.getStatusBarHeight();
    }

    // 开始按钮点击事件
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

}
