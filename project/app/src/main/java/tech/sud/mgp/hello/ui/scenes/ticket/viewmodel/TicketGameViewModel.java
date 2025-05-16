package tech.sud.mgp.hello.ui.scenes.ticket.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

/**
 * 门票场景的游戏业务
 */
public class TicketGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickStartBtnLiveData = new MutableLiveData<>();
    public MutableLiveData<Object> clickReadyBtnLiveData = new MutableLiveData<>();

    // 开始按钮点击事件
    @Override
    public void onGameMGCommonSelfClickStartBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickStartBtn model) {
        super.onGameMGCommonSelfClickStartBtn(handle, model);
        clickStartBtnLiveData.setValue(null);
    }

    // 准备按钮点击事件
    @Override
    public void onGameMGCommonSelfClickReadyBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickReadyBtn model) {
        super.onGameMGCommonSelfClickReadyBtn(handle, model);
        clickReadyBtnLiveData.setValue(null);
    }

    // 结算界面再来一局按钮点击状态
    @Override
    public void onGameMGCommonSelfClickGameSettleAgainBtn(ISudFSMStateHandle handle, SudGIPMGState.MGCommonSelfClickGameSettleAgainBtn model) {
        super.onGameMGCommonSelfClickGameSettleAgainBtn(handle, model);
        clickReadyBtnLiveData.setValue(null);
    }
}
