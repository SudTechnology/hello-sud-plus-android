package tech.sud.mgp.hello.ui.scenes.crossapp.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

public class CrossAppGameViewModel extends AppGameViewModel {

    public MutableLiveData<Object> clickGameSettleCloseBtnLiveData = new MutableLiveData<>();

    @Override
    public void onGameMGCommonSelfClickGameSettleCloseBtn(ISudFSMStateHandle handle, SudMGPMGState.MGCommonSelfClickGameSettleCloseBtn model) {
        super.onGameMGCommonSelfClickGameSettleCloseBtn(handle, model);
        clickGameSettleCloseBtnLiveData.setValue(null);
    }

}
