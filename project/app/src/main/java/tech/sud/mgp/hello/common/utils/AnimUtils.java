package tech.sud.mgp.hello.common.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;

public class AnimUtils {

    /**
     * 抖动动画，上下浮动
     *
     * @param view 要执行动画的view
     */
    public static void shakeVertical(View view) {
        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        int y = DensityUtils.dp2px(view.getContext(), 3);
        TranslateAnimation shake = new TranslateAnimation(0f, 0f, y, -y); //移动方向
        shake.setDuration(1000);  //执行总时间
        shake.setRepeatCount(Animation.INFINITE);
        shake.setRepeatMode(Animation.REVERSE);
        view.startAnimation(shake);
    }

    /**
     * 创建一个缩放动画
     */
    public static Animator createScaleAnimation(View view, long duration, int repeatCount, int repeatMode, float... scale) {
        ArrayList<Animator> animatorList = new ArrayList<>();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scale);
        scaleXAnimator.setRepeatCount(repeatCount);
        scaleXAnimator.setRepeatMode(repeatMode);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scale);
        scaleYAnimator.setRepeatCount(repeatCount);
        scaleYAnimator.setRepeatMode(repeatMode);
        animatorList.add(scaleXAnimator);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        animatorSet.setDuration(duration);
        return animatorSet;
    }

    /**
     * 创建一个呼吸动画
     */
    public static Animator breathe(View view) {
        Animator animator = AnimUtils.createScaleAnimation(view, 1000L,
                ValueAnimator.INFINITE,
                ValueAnimator.REVERSE,
                1.0f, 1.2f, 1.0f
        );
        animator.start();
        return animator;
    }

}
