package tech.sud.mgp.hello.ui.main.home.model;

/**
 * 房间数据
 */
public class RoomItemModel {
    private int roomId; // 房间Id
    private String roomNumber; // 房间号
    private String roomName; // 房间名称
    private int memberCount; // 房间在线人数
    private String roomPic; // 房间封面
    private int sceneType; // 游戏适配场景
    private String rtcType; // rtcType类型
    private String sceneTag; // 场景标签名称
    private String gameLevelDesc; // 游戏级别描述

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getRoomPic() {
        return roomPic;
    }

    public void setRoomPic(String roomPic) {
        this.roomPic = roomPic;
    }

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public String getRtcType() {
        return rtcType;
    }

    public void setRtcType(String rtcType) {
        this.rtcType = rtcType;
    }

    public String getSceneTag() {
        return sceneTag;
    }

    public void setSceneTag(String sceneTag) {
        this.sceneTag = sceneTag;
    }

    public String getGameLevelDesc() {
        return gameLevelDesc;
    }

    public void setGameLevelDesc(String gameLevelDesc) {
        this.gameLevelDesc = gameLevelDesc;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
