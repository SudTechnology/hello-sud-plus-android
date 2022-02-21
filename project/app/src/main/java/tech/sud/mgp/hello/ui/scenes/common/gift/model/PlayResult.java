package tech.sud.mgp.hello.ui.scenes.common.gift.model;

public enum PlayResult {
    START(0),
    PLAYEND(1),
    PLAYERROR(2);

    private int mState = 0;

    private PlayResult(int mState) {
        this.mState = mState;
    }

    public int getState() {
        return mState;
    }
}
