package tech.sud.mgp.hello.ui.scenes.common.gift.model;

/**
 * 礼物描述
 */
public class GiftModel {
    public long giftId;
    public int giftImage;
    public int giftSmallImage;
    public EffectAnimationFormat animationType = EffectAnimationFormat.UNDEFINE;
    // 存放在assets中的资源名字
    public String path;
    // 因为asesets中有大小限制，所以部分资源放在raw，resId为资源id
    public int resId;
    public String giftName;
    public boolean checkState = false;

    public int giftPrice; // 礼物价格(金币)
    public int type; // 1.4.0新增:礼物类型 0：内置礼物 1：后端配置礼物
    public String giftUrl; // 礼物图片
    public String animationUrl; // 礼物动图

    public boolean isFeature; // 是否是特写
    public boolean isEffect; // 是否是特效

    public String extData; // 扩展参数

}
