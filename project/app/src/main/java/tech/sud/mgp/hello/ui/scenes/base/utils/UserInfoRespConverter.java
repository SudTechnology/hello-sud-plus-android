package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.gip.SudGIPWrapper.state.SudGIPAPPState;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

public class UserInfoRespConverter {

    public static UserInfoResp conver(SudGIPAPPState.AIPlayers model) {
        if (model == null) {
            return null;
        }
        UserInfoResp info = new UserInfoResp();
        try {
            info.userId = Long.parseLong(model.userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.avatar = model.avatar;
        info.nickname = model.name;
        info.gender = model.gender;
        info.isAi = true;
        info.level = model.level;
        return info;
    }

    public static UserInfoResp conver(AudioRoomMicModel model) {
        if (model == null) {
            return null;
        }
        UserInfoResp info = new UserInfoResp();
        info.userId = model.userId;
        info.avatar = model.avatar;
        info.nickname = model.nickName;
        info.gender = model.gender;
        info.isAi = model.isAi;
        info.level = model.level;
        return info;
    }

    public static UserInfoResp conver(SudGIPAPPState.ModelAIPlayers model) {
        if (model == null) {
            return null;
        }
        UserInfoResp info = new UserInfoResp();
        try {
            info.userId = Long.parseLong(model.userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        info.avatar = model.avatar;
        info.nickname = model.name;
        info.gender = model.gender;
        info.isAi = true;
        info.level = 3;
        return info;
    }
}
