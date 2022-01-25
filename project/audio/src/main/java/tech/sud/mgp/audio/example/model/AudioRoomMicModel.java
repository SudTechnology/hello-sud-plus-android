package tech.sud.mgp.audio.example.model;

public class AudioRoomMicModel {
    public long userId;
    public String nickName;
    public String avatar;

    public Boolean isCaptain; // 是否是队长，空值不显示
    public int readyStatus; // 0没有状态，1已准备，2未准备
}
