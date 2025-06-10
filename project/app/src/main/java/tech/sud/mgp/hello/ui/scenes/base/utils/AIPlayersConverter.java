package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.mgp.hello.common.model.Gender;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

public class AIPlayersConverter {

    public static SudGIPAPPState.AIPlayers conver(AudioRoomMicModel model) {
        if (model == null) {
            return null;
        }
        SudGIPAPPState.AIPlayers aiPlayers = new SudGIPAPPState.AIPlayers();
        aiPlayers.userId = model.userId + "";
        aiPlayers.name = model.nickName;
        aiPlayers.avatar = model.avatar;
        aiPlayers.gender = model.gender;
        aiPlayers.level = model.level;
        return aiPlayers;
    }

    public static SudGIPAPPState.AIPlayers conver(UserInfo model) {
        if (model == null) {
            return null;
        }
        SudGIPAPPState.AIPlayers aiPlayers = new SudGIPAPPState.AIPlayers();
        aiPlayers.userId = model.userID;
        aiPlayers.name = model.name;
        aiPlayers.avatar = model.icon;
        aiPlayers.gender = model.sex == 1 ? Gender.MALE : Gender.FEMALE;
        aiPlayers.level = model.level;
        return aiPlayers;
    }

}
