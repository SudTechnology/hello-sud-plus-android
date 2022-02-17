package tech.sud.mgp.hello.ui.main.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示不同颜色占不同比例的View
 */
public class ColorRatioView extends View {

    private final List<ColorRatioModel> colorRatioModels = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ColorRatioView(Context context) {
        super(context);
    }

    public ColorRatioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorRatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDatas(List<ColorRatioModel> list) {
        colorRatioModels.clear();
        if (list != null) {
            colorRatioModels.addAll(list);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (colorRatioModels.isEmpty()) {
            return;
        }
        int totalWidth = getMeasuredWidth();
        int startWidth = 0;
        int top = 0;
        int bottom = getMeasuredHeight();
        for (ColorRatioModel colorRatioModel : colorRatioModels) {
            // 画色框
            int right = (int) (colorRatioModel.ratio * totalWidth) + startWidth;
            paint.setColor(colorRatioModel.color);
            canvas.drawRect(startWidth, top, right, bottom, paint);

            // 起点设置
            startWidth = right;
        }
    }

    public static class ColorRatioModel {
        @ColorInt
        public int color; // 颜色值
        public float ratio; // 所占比例，比例总和为1

        public ColorRatioModel(int color, float ratio) {
            this.color = color;
            this.ratio = ratio;
        }
    }

}
