package tech.sud.mgp.hello.service.room.req;

import java.util.List;

/**
 * 送礼 请求参数
 */
public class SendGiftReq {
    public long roomId; // 房间id
    public long giftId; // 礼物id
    public int amount; // 总数量
    public int giftConfigType; // 礼物配置方式（1：客户端，2：服务端）
    public int giftPrice; // 礼物价格(金币)
    public List<String> receiverList; // 收礼人列表
}
