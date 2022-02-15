package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.entity.ZegoRoomConfig;
import tech.sud.mgp.hello.rtc.protocol.MediaRoomConfig;

public class ZegoRoomConfigConverter {

    public static ZegoRoomConfig converZegoRoomConfig(MediaRoomConfig config) {
        if (config == null) return null;
        ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
        zegoRoomConfig.maxMemberCount = config.maxMemberCount;
        zegoRoomConfig.isUserStatusNotify = config.isUserStatusNotify;
        zegoRoomConfig.token = config.token;
        return zegoRoomConfig;
    }

}
