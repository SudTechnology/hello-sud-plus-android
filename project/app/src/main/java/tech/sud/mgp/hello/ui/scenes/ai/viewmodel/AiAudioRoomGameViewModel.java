package tech.sud.mgp.hello.ui.scenes.ai.viewmodel;

import androidx.lifecycle.MutableLiveData;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPMGState;
import tech.sud.mgp.core.ISudFSMStateHandle;
import tech.sud.mgp.hello.ui.scenes.base.viewmodel.AppGameViewModel;

public class AiAudioRoomGameViewModel extends AppGameViewModel {

    public final MutableLiveData<SudMGPMGState.MGHappyGoatChat> happyGoatChatLiveData = new MutableLiveData<>();

    @Override
    public void onGameMGHappyGoatChat(ISudFSMStateHandle handle, SudMGPMGState.MGHappyGoatChat model) {
        super.onGameMGHappyGoatChat(handle, model);
        happyGoatChatLiveData.setValue(model);
    }

}
