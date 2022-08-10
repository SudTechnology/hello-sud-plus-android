package tech.sud.mgp.hello.ui.scenes.base.utils;

import tech.sud.mgp.hello.common.model.Gender;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.scenes.base.model.AudioRoomMicModel;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

public class UserInfoConverter {

    public static UserInfo conver(UserInfoResp model) {
        if (model == null) {
            return null;
        }
        UserInfo info = new UserInfo();
        info.userID = model.userId + "";
        info.name = model.nickname;
        info.icon = model.avatar;
        info.sex = Gender.MALE.equals(model.gender) ? 1 : 2;
        return info;
    }

    public static UserInfo conver(AudioRoomMicModel model) {
        if (model == null) {
            return null;
        }
        UserInfo info = new UserInfo();
        info.userID = model.userId + "";
        info.name = model.nickName;
        info.icon = model.avatar;
        info.sex = Gender.MALE.equals(model.gender) ? 1 : 2;
        return info;
    }

}
