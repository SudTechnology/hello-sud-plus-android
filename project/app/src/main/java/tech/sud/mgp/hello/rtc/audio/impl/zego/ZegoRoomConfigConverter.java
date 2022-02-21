package tech.sud.mgp.hello.rtc.audio.impl.zego;

import im.zego.zegoexpress.entity.ZegoRoomConfig;
import tech.sud.mgp.hello.rtc.audio.core.AudioRoomConfig;

public class ZegoRoomConfigConverter {

    public static ZegoRoomConfig converZegoRoomConfig(AudioRoomConfig config) {
        if (config == null) return null;
        ZegoRoomConfig zegoRoomConfig = new ZegoRoomConfig();
        zegoRoomConfig.maxMemberCount = config.maxMemberCount;
        zegoRoomConfig.isUserStatusNotify = config.isUserStatusNotify;
        zegoRoomConfig.token = config.token;
        return zegoRoomConfig;
    }

}
