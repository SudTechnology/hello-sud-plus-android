package tech.sud.mgp.hello.ui.room.common.gift.manager.stategy.lottie;

import com.airbnb.lottie.LottieAnimationView;

import tech.sud.mgp.hello.ui.room.common.gift.model.PlayGiftModel;

public class GiftJsonModel extends PlayGiftModel {
    private LottieAnimationView lottieAnimationView;

    public LottieAnimationView getLottieAnimationView() {
        return lottieAnimationView;
    }

    public void setLottieAnimationView(LottieAnimationView lottieAnimationView) {
        this.lottieAnimationView = lottieAnimationView;
    }
}
