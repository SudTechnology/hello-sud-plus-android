package tech.sud.mgp.hello.service.main.resp;

import java.util.List;

import tech.sud.mgp.hello.ui.main.home.model.RoomItemModel;

/**
 * 房间列表返回数据
 */
public class RoomListResp {
    private List<RoomItemModel> roomInfoList;

    public List<RoomItemModel> getRoomInfoList() {
        return roomInfoList;
    }

    public void setRoomInfoList(List<RoomItemModel> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }
}
