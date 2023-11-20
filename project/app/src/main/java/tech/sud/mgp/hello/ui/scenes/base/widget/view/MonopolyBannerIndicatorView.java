package tech.sud.mgp.hello.ui.scenes.base.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;

public class MonopolyBannerIndicatorView extends LinearLayout {

    private int ciclrSize = DensityUtils.dp2px(getContext(), 6);
    private int normalPadding = DensityUtils.dp2px(getContext(), 1);

    public MonopolyBannerIndicatorView(Context context) {
        this(context, null);
    }

    public MonopolyBannerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonopolyBannerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MonopolyBannerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public void initIndicator(int count) {
        removeAllViews();
        if (count < 2) {
            return;
        }
        int marginStart = DensityUtils.dp2px(getContext(), 6);
        for (int i = 0; i < count; i++) {
            ImageView view = new ImageView(getContext());
            LayoutParams params = new LayoutParams(ciclrSize, ciclrSize);
            if (i > 0) {
                params.setMarginStart(marginStart);
            }
            addView(view, params);
        }
    }

    public void updatePosition(int position) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof ImageView) {
                ImageView iv = (ImageView) view;
                GradientDrawable drawable;
                if (i == position) {
                    drawable = ShapeUtils.createShape(null, (float) ciclrSize, null, GradientDrawable.RECTANGLE,
                            null, Color.parseColor("#ffffff"));
                    iv.setPadding(0, 0, 0, 0);
                } else {
                    drawable = ShapeUtils.createShape(null, (float) ciclrSize, null, GradientDrawable.RECTANGLE,
                            null, Color.parseColor("#66ffffff"));
                    iv.setPadding(normalPadding, normalPadding, normalPadding, normalPadding);
                }
                iv.setImageDrawable(drawable);
            }
        }
    }

}
