package tech.sud.mgp.hello.ui.scenes.base.interaction.base.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;

public class InteractionBannerIndicatorView extends LinearLayout {

    private int ciclrSize = DensityUtils.dp2px(getContext(), 6);

    public InteractionBannerIndicatorView(Context context) {
        this(context, null);
    }

    public InteractionBannerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InteractionBannerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public InteractionBannerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public void initIndicator(int count) {
        removeAllViews();
        if (count < 1) {
            return;
        }
        int marginStart = DensityUtils.dp2px(getContext(), 4);
        for (int i = 0; i < count; i++) {
            View view = new View(getContext());
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
            GradientDrawable drawable;
            if (i == position) {
                drawable = ShapeUtils.createShape(null, (float) ciclrSize, null, GradientDrawable.RECTANGLE,
                        null, Color.parseColor("#ffffff"));
            } else {
                drawable = ShapeUtils.createShape(null, (float) ciclrSize, null, GradientDrawable.RECTANGLE,
                        null, Color.parseColor("#66ffffff"));
            }
            view.setBackground(drawable);
        }
    }

}
