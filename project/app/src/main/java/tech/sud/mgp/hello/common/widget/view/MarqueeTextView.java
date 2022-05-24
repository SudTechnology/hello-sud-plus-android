package tech.sud.mgp.hello.common.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    /** 在进行走马灯时，绘制一个蒙板效果 */
    private boolean drawMask = false;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//禁用全局反色
            setForceDarkAllowed(false);
        }
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView, defStyleAttr, 0);
        drawMask = typeArray.getBoolean(R.styleable.MarqueeTextView_mtv_drawMask, false);
        typeArray.recycle();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return 0;
    }

}
