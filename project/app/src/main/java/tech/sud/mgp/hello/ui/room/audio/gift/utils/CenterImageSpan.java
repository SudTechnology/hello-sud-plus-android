package tech.sud.mgp.hello.ui.room.audio.gift.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenterImageSpan extends ImageSpan {
    private boolean isSmallImage = false;

    public CenterImageSpan(@NonNull Drawable drawable) {
        super(drawable, DynamicDrawableSpan.ALIGN_CENTER);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontH = fmPaint.descent - fmPaint.ascent; //文字行的高度(具体字符顶部到底部的真实高度)
            int imageH = rect.bottom - rect.top; // 图片的高度
            //如果图片的高度 <= 文本的高度,可以直接使用 ImageSpan.ALIGN_CENTER 来实现垂直居中
            //即将当前图片所在行的图片相对于文字的高度居中，但仅支持单行有效
            if (imageH > fontH) {
                isSmallImage = false;
                //这里直接将文字行的高度调整至Image高度对应的top和bottom，即将当前图片所在行的文字相对于图片的高度居中
                fm.ascent = fmPaint.ascent - (imageH - fontH) / 2;
                fm.top = fmPaint.ascent - (imageH - fontH) / 2;
                fm.bottom = fmPaint.descent + (imageH - fontH) / 2;
                fm.descent = fmPaint.descent + (imageH - fontH) / 2;
            } else {
                //如果是小图片，就直接使用DynamicDrawableSpan.getSize()里面但方法
                isSmallImage = true;
                fm.ascent = -rect.bottom;
                fm.descent = 0;
                fm.top = fm.ascent;
                fm.bottom = 0;
            }
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = bottom - b.getBounds().bottom;
        if (isSmallImage) {
            //但是对于多行使用小图标时，需要我们稍加改变使其居中
            transY -= ((bottom - top) / 2 - b.getBounds().height() / 2);
        }
        canvas.translate(x, (float) transY);
        b.draw(canvas);
        canvas.restore();
    }
}
