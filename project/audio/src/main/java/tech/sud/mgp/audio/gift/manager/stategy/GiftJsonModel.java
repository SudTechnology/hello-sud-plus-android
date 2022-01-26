package tech.sud.mgp.audio.gift.manager.stategy;

import com.airbnb.lottie.LottieAnimationView;

import tech.sud.mgp.audio.gift.model.PlayGiftModel;

public class GiftJsonModel extends PlayGiftModel {
    private LottieAnimationView lottieAnimationView;

    public LottieAnimationView getLottieAnimationView() {
        return lottieAnimationView;
    }

    public void setLottieAnimationView(LottieAnimationView lottieAnimationView) {
        this.lottieAnimationView = lottieAnimationView;
    }
}
