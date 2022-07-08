package tech.sud.mgp.hello.ui.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;

/**
 * 横向的，头像View，有六个用户头像
 */
public class AvatarGroupView extends ConstraintLayout {

    public AvatarGroupView(@NonNull Context context) {
        this(context, null);
    }

    public AvatarGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AvatarGroupView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        int baseMarginStart = DensityUtils.dp2px(getContext(), 18);
        addAvatar(R.drawable.ic_avatar_1, baseMarginStart);
        addAvatar(R.drawable.ic_avatar_2, baseMarginStart);
        addAvatar(R.drawable.ic_avatar_3, baseMarginStart);
        addAvatar(R.drawable.ic_avatar_4, baseMarginStart);
        addAvatar(R.drawable.ic_avatar_5, baseMarginStart);
        addAvatar(R.drawable.ic_avatar_6, baseMarginStart);
    }

    // 添加一个参与者头像
    private void addAvatar(int resId, int baseMarginStart) {
        RoundedImageView iv = new RoundedImageView(getContext());
        iv.setOval(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(resId);
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.startToStart = LayoutParams.PARENT_ID;
        params.setMarginStart(baseMarginStart * getChildCount());
        params.dimensionRatio = "1:1";
        addView(iv, 0, params);
    }

}
