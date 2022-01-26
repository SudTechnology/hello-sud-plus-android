package tech.sud.mgp.audio.middle.impl;

import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.audio.middle.MediaUser;

public class ZegoUserConverter {

    public static MediaUser converMediaUser(ZegoUser user) {
        return new MediaUser(user.userID, user.userName);
    }

    public static ZegoUser converZegoUser(MediaUser user) {
        return new ZegoUser(user.userID, user.userName);
    }

}
