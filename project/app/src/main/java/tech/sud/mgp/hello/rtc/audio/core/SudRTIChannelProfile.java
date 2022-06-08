package tech.sud.mgp.hello.rtc.audio.core;

public enum SudRTIChannelProfile {
    // 通信场景，所有用户都可以发布和接收音、视频流
    SUD_RTI_CHANNEL_PROFILE_COMMUNICATION(0),
    // 直播场景，有主播和用户两种角色，主播可以发布和接收音视频流，观众只能接收流
    SUD_RTI_CHANNEL_PROFILE_BROADCASTING(1);

    private int value;

    private SudRTIChannelProfile(int profile) {
        this.value = profile;
    }

    public int value() {
        return this.value;
    }
}
