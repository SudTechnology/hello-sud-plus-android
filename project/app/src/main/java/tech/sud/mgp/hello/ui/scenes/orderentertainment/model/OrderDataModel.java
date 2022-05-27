package tech.sud.mgp.hello.ui.scenes.orderentertainment.model;

import java.util.List;

import tech.sud.mgp.hello.service.room.resp.RoomOrderCreateResp;
import tech.sud.mgp.hello.ui.scenes.base.model.UserInfo;

/**
 * 本地组装的点单数据
 */
public class OrderDataModel {
    public long roomId; // 房间id
    public List<UserInfo> userList; // 邀请的主播userid
    public OrderGameModel game; // 点单的游戏
    public RoomOrderCreateResp resp; // 后台返回的订单数据
}
