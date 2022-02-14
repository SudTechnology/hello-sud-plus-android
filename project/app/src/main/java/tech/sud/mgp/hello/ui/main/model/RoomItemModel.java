package tech.sud.mgp.hello.ui.main.model;

public class RoomItemModel {

    /**
     * roomId : 1
     * roomName : 测试1
     * memberCount : 1
     * roomPic : http://xxxxx
     * sceneType : 1
     */

    private int roomId;
    private String roomName;
    private int memberCount;
    private String roomPic;
    private int sceneType;

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
}
