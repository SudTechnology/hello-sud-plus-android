package tech.sud.mgp.hello.rtc.audio.impl.zego;

import im.zego.zegoexpress.entity.ZegoUser;
import tech.sud.mgp.hello.rtc.audio.core.AudioUser;

public class ZegoUserConverter {

    public static AudioUser converMediaUser(ZegoUser user) {
        if (user == null) return null;
        return new AudioUser(user.userID, user.userName);
    }

    public static ZegoUser converZegoUser(AudioUser user) {
        if (user == null) return null;
        return new ZegoUser(user.userID, user.userName);
    }

}
