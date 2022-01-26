package tech.sud.mgp.audio.middle.impl;

import im.zego.zegoexpress.entity.ZegoRoomConfig;
import tech.sud.mgp.audio.middle.MediaRoomConfig;

public class ZegoRoomConfigConverter {

    public static ZegoRoomConfig converZegoRoomConfig(MediaRoomConfig confg) {
        ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
        zegoRoomConfig.maxMemberCount = confg.maxMemberCount;
        zegoRoomConfig.isUserStatusNotify = confg.isUserStatusNotify;
        zegoRoomConfig.token = confg.token;
        return zegoRoomConfig;
    }

}
