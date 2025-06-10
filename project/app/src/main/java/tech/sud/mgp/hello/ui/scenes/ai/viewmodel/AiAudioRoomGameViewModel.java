package tech.sud.mgp.hello.ui.scenes.ai.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.gip.SudGIPWrapper.state.SudGIPMGState;
import tech.sud.gip.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

public class AiAudioRoomGameViewModel extends AppGameViewModel {

    public final MutableLiveData<SudGIPMGState.MGHappyGoatChat> happyGoatChatLiveData = new MutableLiveData<>();

    @Override
    public void onGameMGHappyGoatChat(ISudFSMStateHandle handle, SudGIPMGState.MGHappyGoatChat model) {
        super.onGameMGHappyGoatChat(handle, model);
        happyGoatChatLiveData.setValue(model);
    }

}
