package tech.sud.mgp.audio.gift.manager.stategy;

import android.animation.Animator;

import com.airbnb.lottie.LottieAnimationView;

import tech.sud.mgp.audio.gift.callback.PlayResultCallback;
import tech.sud.mgp.audio.gift.model.PlayResult;

public class GiftJsonStrategy extends PlayStrategy<GiftJsonModel>{
    @Override
    public void play(GiftJsonModel giftJsonModel) {
        loadJson(giftJsonModel.getPath(), giftJsonModel.getLottieAnimationView(),giftJsonModel.getCallback());
    }

    /**
     * 加载json效果
     * */
    private void loadJson(String path, LottieAnimationView lottieAnimationView, PlayResultCallback callback){
        lottieAnimationView.setAnimation(path);
        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (callback != null) {
                    callback.result(PlayResult.START);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.result(PlayResult.PLAYEND);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (callback != null) {
                    callback.result(PlayResult.PLAYEND);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
