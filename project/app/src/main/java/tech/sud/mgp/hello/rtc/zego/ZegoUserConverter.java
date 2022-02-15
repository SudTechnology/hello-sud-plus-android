package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.hello.rtc.protocol.MediaUser;

public class ZegoUserConverter {

    public static MediaUser converMediaUser(ZegoUser user) {
        if (user == null) return null;
        return new MediaUser(user.userID, user.userName);
    }

    public static ZegoUser converZegoUser(MediaUser user) {
        if (user == null) return null;
        return new ZegoUser(user.userID, user.userName);
    }

}
