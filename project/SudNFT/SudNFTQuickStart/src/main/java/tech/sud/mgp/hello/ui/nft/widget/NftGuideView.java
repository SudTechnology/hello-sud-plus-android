package tech.sud.mgp.hello.ui.nft.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ShapeUtils;

/**
 * Nft引导提示View
 */
public class NftGuideView extends LinearLayout {

    private TextView textView;

    public NftGuideView(@NonNull Context context) {
        this(context, null);
    }

    public NftGuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NftGuideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NftGuideView, defStyleAttr, 0);
        String content = typedArray.getString(R.styleable.NftGuideView_ngv_text);
        typedArray.recycle();

        textView = new TextView(context);
        textView.setText(content);
        textView.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(context, 30),
                null, GradientDrawable.RECTANGLE, null, Color.parseColor("#b3000000")));
        int paddingVertical = DensityUtils.dp2px(context, 3);
        int paddingHorizontal = DensityUtils.dp2px(context, 12);
        textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        textView.setTextSize(12);
        textView.setTextColor(Color.WHITE);
        addView(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        View viewArrow = new View(context);
        viewArrow.setBackgroundResource(R.drawable.ic_guide_arrow_down);
        addView(viewArrow, DensityUtils.dp2px(context, 12), DensityUtils.dp2px(context, 6));
    }

    public void setText(String text) {
        textView.setText(text);
    }

}
