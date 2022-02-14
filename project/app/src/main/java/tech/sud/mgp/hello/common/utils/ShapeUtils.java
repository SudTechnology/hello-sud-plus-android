package tech.sud.mgp.hello.common.utils;

import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;

public class ShapeUtils {

    /**
     * 创建一个Shape - GradientDrawable
     *
     * @param strokeWidth - 沿边线厚度；
     * @param roundRadius - 圆角半径；
     * @param shape       - shape绘制类型(rectangle、oval等)；
     * @param strokeColor - 沿边线颜色；
     * @param fillColor   - 内部填充颜色；
     * @return
     */
    public static GradientDrawable createShape(Integer strokeWidth, Float roundRadius,
                                               int shape, @ColorInt Integer strokeColor, @ColorInt Integer fillColor) {
        GradientDrawable gd = new GradientDrawable();
        if (fillColor != null) {
            gd.setColor(fillColor);
        }

        gd.setShape(shape);

        if (roundRadius != null) {
            gd.setCornerRadius(roundRadius);
        }

        if (strokeWidth != null && strokeColor != null) {
            gd.setStroke(strokeWidth, strokeColor);
        }

        return gd;
    }

}
