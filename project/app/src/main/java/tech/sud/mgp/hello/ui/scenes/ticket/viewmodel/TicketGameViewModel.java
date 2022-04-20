package tech.sud.mgp.hello.ui.scenes.ticket.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.GameViewModel;

/**
 * 门票场景的游戏业务
 */
public class TicketGameViewModel extends GameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>();
    public MutableLiveData<Object> clickReadyBtnLiveData = new MutableLiveData<>();

    // 开始按钮点击事件
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

    // 准备按钮点击事件
    @Override
    public void onGameMGCommonSelfClickReadyBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickReadyBtn model) {
        super.onGameMGCommonSelfClickReadyBtn(handle, model);
        clickReadyBtnLiveData.setValue(null);
    }

    // 结算界面再来一局按钮点击状态
    @Override
    public void onGameMGCommonSelfClickGameSettleAgainBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGameSettleAgainBtn model) {
        super.onGameMGCommonSelfClickGameSettleAgainBtn(handle, model);
        clickReadyBtnLiveData.setValue(null);
    }
}
