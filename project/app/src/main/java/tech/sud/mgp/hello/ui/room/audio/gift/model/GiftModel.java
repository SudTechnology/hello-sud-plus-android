package tech.sud.mgp.hello.ui.room.audio.gift.model;

public class GiftModel {
    public int giftId;
    public int giftImage;
    public int giftSmallImage;
    public EffectAnimationFormat animationType = EffectAnimationFormat.UNDEFINE;
    //存放在assets中的资源名字
    public String path;
    //因为asesets中有大小限制，所以部分资源放在raw，resId为资源id
    public int resId;
    public String giftName;
    public boolean checkState =false;
}
