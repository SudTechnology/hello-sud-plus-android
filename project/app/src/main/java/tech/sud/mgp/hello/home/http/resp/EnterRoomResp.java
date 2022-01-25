package tech.sud.mgp.hello.home.http.resp;

public class EnterRoomResp {
    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    private long roomId;
    private String roomName;

}
