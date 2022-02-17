//package tech.sud.mgp.hello.rtc.audio.impl.zego;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import im.zego.zegoexpress.entity.ZegoStream;
//import tech.sud.mgp.hello.rtc.audio.core.AudioStream;
//
//public class ZegoStreamConverter {
//
//    public static AudioStream converMediaStream(ZegoStream stream) {
//        if (stream == null) return null;
//        AudioStream audioStream = new AudioStream();
//        audioStream.user = ZegoUserConverter.converMediaUser(stream.user);
//        audioStream.streamID = stream.streamID;
//        audioStream.extraInfo = stream.extraInfo;
//        return audioStream;
//    }
//
//    public static List<AudioStream> converMediaStreamList(List<ZegoStream> zegoStreamList) {
//        if (zegoStreamList == null || zegoStreamList.size() == 0) {
//            return null;
//        }
//        List<AudioStream> list = new ArrayList<>();
//        for (int i = 0; i < zegoStreamList.size(); i++) {
//            list.add(converMediaStream(zegoStreamList.get(i)));
//        }
//        return list;
//    }
//
//}
