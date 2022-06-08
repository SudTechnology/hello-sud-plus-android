package tech.sud.mgp.hello.rtc.audio.model;

import android.view.View;

public class AudioJoinRoomModel {

    public String userID;

    public String userName;

    public String roomID;

    public String roomName;

    public String token;

    public long timestamp;

    public String appId;

    // 接收视频时，传入视频画面View
    public View localView;
}
