package tech.sud.mgp.hello.common.widget.view;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

import java.util.Arrays;

/**
 * 自定义颜色渐变drawable
 */
public class CustomColorDrawable extends ColorDrawable {

    private int radius;
    private final Path path = new Path();
    private final float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private ValueAnimator valueAnimator;
    private int startColor;
    private int endColor;

    @Override
    public void draw(Canvas canvas) {
        if (radius > 0) {
            canvas.clipPath(path);
        }
        super.draw(canvas);
        initAnimator();
    }

    private void initAnimator() {
        if (valueAnimator != null) {
            return;
        }
        valueAnimator = ValueAnimator.ofArgb(startColor, endColor);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                if (isVisible()) {
                    setColor(color);
                } else {
                    valueAnimator.cancel();
                    valueAnimator = null;
                }
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    /** 设置圆角 */
    public void setRadius(int radius) {
        this.radius = radius;
        Arrays.fill(radiusArray, radius);
        updateRoundPath();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        initAnimator();
        updateRoundPath();
    }

    public void updateRoundPath() {
        int width = getBounds().width();
        int height = getBounds().height();
        path.reset();
        path.addRoundRect(0, 0, width, height, radiusArray, Path.Direction.CW);
    }

}
