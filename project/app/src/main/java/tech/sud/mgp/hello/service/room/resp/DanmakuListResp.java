package tech.sud.mgp.hello.service.room.resp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 弹幕列表 返回参数
 */
public class DanmakuListResp {

    public static final int CALL_MODE_DANMAKU = 1; // 召唤方式，弹幕
    public static final int CALL_MODE_GIFT = 2; // 召唤方式，礼物

    public String guideText; // 引导内容

    public List<JoinTeam> joinTeamList; // 加入战队

    @SerializedName(value = "callWarcraftInfoList")
    public List<Prop> propList; // 道具列表

    public List<Prop> actionList; // 第二行

    public static class Prop {
        public int callMode; // 召唤方式（1弹幕，2礼物）
        public String title; // 标题
        public int warcraftType; // 魔兽类型（1弹幕魔兽，2初级魔兽，3精英魔兽，4远古魔兽）
        public String name; // 名称
        public String titleColor; // 标题色值
        public String content; // 文本内容(弹幕使用)
        public List<String> warcraftImageList; // 魔兽图片列表
        public int giftAmount; // 礼物数量
        public long giftId; // 礼物id
        public int giftPrice; // 礼物价格
        public String giftUrl; // 礼物图片
        public String animationUrl; // 礼物动图
        public String backgroundUrl;
    }

    public static class JoinTeam {
        public String name; // 名称
        public String content; // 文本内容(弹幕使用)
        public String buttonPic; // 按钮图片
        public String backgroundColor; // 背景颜色
    }

}
