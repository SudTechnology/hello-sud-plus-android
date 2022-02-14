package tech.sud.mgp.hello.ui.room.audio.middle.impl;

import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.entity.ZegoStream;
import tech.sud.mgp.hello.ui.room.audio.middle.MediaStream;

public class ZegoStreamConverter {

    public static MediaStream converMediaStream(ZegoStream stream) {
        MediaStream mediaStream = new MediaStream();
        mediaStream.user = stream.user;
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
