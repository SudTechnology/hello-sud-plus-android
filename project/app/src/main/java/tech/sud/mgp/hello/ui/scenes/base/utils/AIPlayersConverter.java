package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

public class AIPlayersConverter {

    public static SudMGPAPPState.AIPlayers conver(AudioRoomMicModel model) {
        if (model == null) {
            return null;
        }
        SudMGPAPPState.AIPlayers aiPlayers = new SudMGPAPPState.AIPlayers();
        aiPlayers.userId = model.userId + "";
        aiPlayers.name = model.nickName;
        aiPlayers.avatar = model.avatar;
        aiPlayers.gender = model.gender;
        return aiPlayers;
    }

}
