package tech.sud.mgp.hello.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;

public class EmptyDataView extends ConstraintLayout {

    private TextView tvText = new TextView(getContext());

    public EmptyDataView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyDataView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyDataView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EmptyDataView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        tvText.setGravity(Gravity.CENTER);
        tvText.setTextSize(14);
        tvText.setTextColor(ContextCompat.getColor(getContext(), R.color.c_8a8a8e));
        LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textParams.topToTop = LayoutParams.PARENT_ID;
        textParams.topMargin = DensityUtils.dp2px(getContext(), 127);
        int marginHorizontal = DensityUtils.dp2px(getContext(), 16);
        textParams.setMarginEnd(marginHorizontal);
        textParams.setMarginEnd(marginHorizontal);
        addView(tvText, textParams);
    }

    public void setText(String text) {
        tvText.setText(text);
    }

}
