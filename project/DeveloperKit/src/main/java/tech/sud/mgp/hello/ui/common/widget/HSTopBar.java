package tech.sud.mgp.hello.ui.common.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ActivityUtils;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 页面顶部标题
 */
public class HSTopBar extends ConstraintLayout {

    private TextView tvTitle = new TextView(getContext());
    private ImageView ivBack = new ImageView(getContext());

    public HSTopBar(@NonNull Context context) {
        this(context, null);
    }

    public HSTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HSTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        setListeners();
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HSTopBar, defStyleAttr, 0);
        String titleText = array.getString(R.styleable.HSTopBar_topbar_titleText);
        Drawable backSrc = array.getDrawable(R.styleable.HSTopBar_topbar_backSrc);
        int titleTextColor = array.getColor(R.styleable.HSTopBar_topbar_titleTextColor, ContextCompat.getColor(getContext(), R.color.c_1a1a1a));
        array.recycle();

        // 返回按钮
        int ivPadding = DensityUtils.dp2px(context, 10);
        ivBack.setPadding(ivPadding, ivPadding, ivPadding, ivPadding);
        if (backSrc == null) {
            ivBack.setImageResource(R.drawable.icon_navi_back);
        } else {
            ivBack.setImageDrawable(backSrc);
        }
        ivBack.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int ivSize = DensityUtils.dp2px(context, 44);
        LayoutParams ivParams = new LayoutParams(ivSize, ivSize);
        ivParams.topToTop = LayoutParams.PARENT_ID;
        ivParams.bottomToBottom = LayoutParams.PARENT_ID;
        ivParams.startToStart = LayoutParams.PARENT_ID;
        ivParams.leftMargin = DensityUtils.dp2px(context, 6);
        addView(ivBack, ivParams);

        // 标题
        tvTitle.setMaxLines(1);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(titleTextColor);
        tvTitle.setText(titleText);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        LayoutParams tvParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int marginHorizontal = DensityUtils.dp2px(context, 50);
        tvParams.topToTop = LayoutParams.PARENT_ID;
        tvParams.bottomToBottom = LayoutParams.PARENT_ID;
        tvParams.leftMargin = marginHorizontal;
        tvParams.rightMargin = marginHorizontal;
        addView(tvTitle, tvParams);
    }

    private void setListeners() {
        ivBack.setOnClickListener(v -> {
            Activity topActivity = ActivityUtils.getTopActivity();
            if (topActivity != null) {
                topActivity.finish();
            }
        });
    }

    /**
     * 设置标题栏
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置返回按钮点击监听
     */
    public void setBackOnClickListener(OnClickListener listener) {
        ivBack.setOnClickListener(listener);
    }

}
