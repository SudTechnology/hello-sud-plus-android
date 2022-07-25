package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.mgp.SudMGPWrapper.state.SudMGPAPPState;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;

public class UserInfoRespConverter {

    public static UserInfoResp conver(SudMGPAPPState.AIPlayers model) {
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
        return info;
    }

}
