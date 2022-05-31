package tech.sud.mgp.hello.ui.scenes.common.cmd.model;

import tech.sud.mgp.hello.SudMGPWrapper.utils.SudJsonUtils;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 信令消息的其类
 */
public class RoomCmdBaseModel {
    public int cmd;
    public UserInfo sendUser;

    public RoomCmdBaseModel(int cmd, UserInfo sendUser) {
        this.cmd = cmd;
        this.sendUser = sendUser;
    }

    public String toJson() {
        return SudJsonUtils.toJson(this);
    }

}
