package tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.lottie;

import android.animation.Animator;

import com.airbnb.lottie.LottieAnimationView;

import tech.sud.mgp.hello.ui.scenes.common.gift.listener.PlayResultListener;
import tech.sud.mgp.hello.ui.scenes.common.gift.manager.stategy.PlayStrategy;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.PlayResult;

public class GiftJsonStrategy extends PlayStrategy<GiftJsonModel> {
    @Override
    public void play(GiftJsonModel giftJsonModel) {
        loadJson(giftJsonModel.getPath(), giftJsonModel.getLottieAnimationView(), giftJsonModel.getPlayResultListener());
    }

    /**
     * 加载json效果
     */
    private void loadJson(String path, LottieAnimationView lottieAnimationView, PlayResultListener listener) {
        lottieAnimationView.setAnimation(path);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) {
                    listener.onResult(PlayResult.START);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onResult(PlayResult.PLAYEND);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (listener != null) {
                    listener.onResult(PlayResult.PLAYEND);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
