package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.hello.rtc.protocol.MediaUser;

public class ZegoUserConverter {

    public static MediaUser converMediaUser(ZegoUser user) {
        return new MediaUser(user.userID, user.userName);
    }

    public static ZegoUser converZegoUser(MediaUser user) {
        return new ZegoUser(user.userID, user.userName);
    }

}
