package tech.sud.mgp.hello.service.room.req;

/**
 * 发送弹幕 请求参数
 */
public class SendDanmakuReq {
    public long roomId; // 房间id
    public String content; // 弹幕内容（具体值跟游戏类型相关）
}
