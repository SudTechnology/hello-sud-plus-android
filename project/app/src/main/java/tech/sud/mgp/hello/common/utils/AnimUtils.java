package tech.sud.mgp.hello.common.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

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

}
