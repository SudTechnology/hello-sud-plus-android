package tech.sud.mgp.hello.ui.common.widget;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;

/**
 * 设置颜色以及大小
 */
public class SizeForegroundColorSpan extends ForegroundColorSpan {

    private float textSize;

    public SizeForegroundColorSpan(int color) {
        super(color);
    }

    public SizeForegroundColorSpan(@NonNull Parcel src) {
        super(src);
    }

    /** px单位 */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setTextSize(textSize);
    }

}
