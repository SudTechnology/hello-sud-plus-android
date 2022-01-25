package tech.sud.mgp.hello.home.http.resp;

import java.util.List;

import tech.sud.mgp.hello.home.model.RoomItemModel;

public class RoomListResp {
    private List<RoomItemModel> roomInfoList;

    public List<RoomItemModel> getRoomInfoList() {
        return roomInfoList;
    }

    public void setRoomInfoList(List<RoomItemModel> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }
}
