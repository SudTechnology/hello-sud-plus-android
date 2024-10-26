package tech.sud.mgp.hello.ui.scenes.common.gift.model;

public enum EffectAnimationFormat {
    SVGA(1),
    MP4(2),
    JSON(3),
    WEBP(4),
    ROCKET(5),
    UNDEFINE(100);

    private int mState = 100;

    private EffectAnimationFormat(int mState) {
        this.mState = mState;
    }

    public int getState() {
        return mState;
    }
}
