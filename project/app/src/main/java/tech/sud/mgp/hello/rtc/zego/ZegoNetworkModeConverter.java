package tech.sud.mgp.hello.rtc.zego;

import im.zego.zegoexpress.constants.ZegoNetworkMode;
import tech.sud.mgp.hello.rtc.protocol.MediaAudioEngineNetworkStateType;

public class ZegoNetworkModeConverter {

    public static MediaAudioEngineNetworkStateType converMediaNetwork(ZegoNetworkMode mode) {
        switch (mode) {
            case OFFLINE:
                return MediaAudioEngineNetworkStateType.OFFLINE;
            case UNKNOWN:
                return MediaAudioEngineNetworkStateType.UNKNOWN;
            case ETHERNET:
                return MediaAudioEngineNetworkStateType.ETHERNET;
            case WIFI:
                return MediaAudioEngineNetworkStateType.WIFI;
            case MODE_2G:
                return MediaAudioEngineNetworkStateType.MODE_2G;
            case MODE_3G:
                return MediaAudioEngineNetworkStateType.MODE_3G;
            case MODE_4G:
                return MediaAudioEngineNetworkStateType.MODE_4G;
            case MODE_5G:
                return MediaAudioEngineNetworkStateType.MODE_5G;
        }
        return null;
    }

}
