package tech.sud.mgp.hello.service.room.resp;

import java.util.List;

/**
 * 礼物列表 返回参数
 */
public class GiftListResp {

    public List<BackGiftModel> giftList; // 礼物列表

    public static class BackGiftModel {
        public long gameId; // 游戏id
        public long giftId; // 礼物id
        public String name; // 名称
        public int giftPrice; // 礼物价格(金币)
        public String giftUrl; // 礼物图片
        public String bigGiftUrl; // 礼物大图
        public String smallGiftUrl; // 礼物小图
        public String animationUrl; // 动效路径
    }

}
