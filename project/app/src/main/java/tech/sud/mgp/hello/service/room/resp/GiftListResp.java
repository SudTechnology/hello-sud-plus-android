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
        public Details details; // 详情
    }

    public static class Details {
        public String backgroundUrl; // 卡片背景
        public String description; // 描述
        public String title; // 标题
        public int cardType; // 1：角色卡 2：功能卡
        public String textColor; // 文字颜色
        public String content; // 弹幕内容
    }

}
