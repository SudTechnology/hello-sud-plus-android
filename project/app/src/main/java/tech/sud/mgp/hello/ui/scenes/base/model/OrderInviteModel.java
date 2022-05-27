package tech.sud.mgp.hello.ui.scenes.base.model;

import java.util.List;
/**
 * 点单邀请数据
 * agreeState  同意状态
 * orderId  订单id
 * gameId   游戏id
 * gameName 游戏名字
 * userID   邀请者的userID
 * nickname 邀请者的nickname
 * toUsers  被邀请的主播id列表
 * */
public class OrderInviteModel {
    public int agreeState;//1同意2拒绝
    public String sendUserId;//邀请者id
    public String sendUserName;//邀请者昵称
    public long orderId; // 订单id
    public long gameId; //游戏id
    public String gameName; //游戏名字
    public List<String> toUsers; // 下单邀请的主播id列表
}
