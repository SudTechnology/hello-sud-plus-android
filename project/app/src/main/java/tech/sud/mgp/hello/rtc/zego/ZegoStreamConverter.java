package tech.sud.mgp.hello.rtc.zego;

import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.entity.ZegoStream;
import tech.sud.mgp.hello.rtc.protocol.MediaStream;

public class ZegoStreamConverter {

    public static MediaStream converMediaStream(ZegoStream stream) {
        if (stream == null) return null;
        MediaStream mediaStream = new MediaStream();
        mediaStream.user = ZegoUserConverter.converMediaUser(stream.user);
        mediaStream.streamID = stream.streamID;
        mediaStream.extraInfo = stream.extraInfo;
        return mediaStream;
    }

    public static List<MediaStream> converMediaStreamList(List<ZegoStream> zegoStreamList) {
        if (zegoStreamList == null || zegoStreamList.size() == 0) {
            return null;
        }
        List<MediaStream> list = new ArrayList<>();
        for (int i = 0; i < zegoStreamList.size(); i++) {
            list.add(converMediaStream(zegoStreamList.get(i)));
        }
        return list;
    }

}