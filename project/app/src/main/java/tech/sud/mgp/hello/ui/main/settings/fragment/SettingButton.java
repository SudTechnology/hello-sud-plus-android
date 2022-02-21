package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 设置页的button
 */
public class SettingButton extends ConstraintLayout {

    private final TextView tvName = new TextView(getContext());
    private final TextView tvHint = new TextView(getContext());
    private final View viewArrow = new View(getContext());

    public SettingButton(@NonNull Context context) {
        this(context, null);
    }

    public SettingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(DensityUtils.dp2px(context, 20), 0, DensityUtils.dp2px(context, 18), 0);
        initView(context);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingButton, defStyleAttr, 0);
        tvName.setText(typedArray.getText(R.styleable.SettingButton_sb_name));
        tvHint.setText(typedArray.getText(R.styleable.SettingButton_sb_hint));
        typedArray.recycle();
    }

    private void initView(Context context) {
        // 左边名称
        tvName.setTextSize(16);
        tvName.setTextColor(ContextCompat.getColor(context, R.color.c_1a1a1a));
        LayoutParams nameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nameParams.startToStart = LayoutParams.PARENT_ID;
        nameParams.topToTop = LayoutParams.PARENT_ID;
        nameParams.bottomToBottom = LayoutParams.PARENT_ID;
        addView(tvName, nameParams);

        // 右边的文字
        tvHint.setTextSize(14);
        tvHint.setTextColor(ContextCompat.getColor(context, R.color.c_8a8a8e));
        LayoutParams hintParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        hintParams.endToEnd = LayoutParams.PARENT_ID;
        hintParams.topToTop = LayoutParams.PARENT_ID;
        hintParams.bottomToBottom = LayoutParams.PARENT_ID;
        hintParams.setMarginEnd(DensityUtils.dp2px(context, 28));
        addView(tvHint, hintParams);

        // 箭头
        viewArrow.setBackgroundResource(R.drawable.ic_right_arrow);
        int arrowSize = DensityUtils.dp2px(context, 12);
        LayoutParams arrowParams = new LayoutParams(arrowSize, arrowSize);
        arrowParams.endToEnd = LayoutParams.PARENT_ID;
        arrowParams.topToTop = LayoutParams.PARENT_ID;
        arrowParams.bottomToBottom = LayoutParams.PARENT_ID;
        addView(viewArrow, arrowParams);
    }

    // 设置左边的名称
    public void setName(String value) {
        tvName.setText(value);
    }

    // 设置右边文字
    public void setHint(String value) {
        tvHint.setText(value);
    }

}
