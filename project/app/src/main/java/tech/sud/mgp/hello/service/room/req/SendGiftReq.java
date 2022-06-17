package tech.sud.mgp.hello.service.room.req;

/**
 * 送礼 请求参数
 */
public class SendGiftReq {
    public long roomId; // 房间id
    public String giftId; // 礼物id
    public int amount; // 总数量
}
