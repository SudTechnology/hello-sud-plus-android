package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;

/**
 * 跨域 组队状态下车位ItemView
 */
public class CrossAppStallItemView extends ConstraintLayout {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvCaptain;

    public CrossAppStallItemView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppStallItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppStallItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppStallItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_cross_app_stall_item, this);
        ivIcon = findViewById(R.id.stall_item_iv_icon);
        tvName = findViewById(R.id.stall_item_tv_name);
        tvCaptain = findViewById(R.id.stall_item_tv_captain);
    }

    /**
     * 设置显示模式
     *
     * @param isBig true 大的
     */
    public void setMode(boolean isBig) {
        if (isBig) {
            int iconSize = DensityUtils.dp2px(getContext(), 72);
            ViewGroup.LayoutParams iconParams = ivIcon.getLayoutParams();
            iconParams.width = iconSize;
            iconParams.height = iconSize;
            ivIcon.setLayoutParams(iconParams);

            ViewUtils.setMarginTop(tvCaptain, DensityUtils.dp2px(getContext(), 61));
        } else {
            int iconSize = DensityUtils.dp2px(getContext(), 54);
            ViewGroup.LayoutParams iconParams = ivIcon.getLayoutParams();
            iconParams.width = iconSize;
            iconParams.height = iconSize;
            ivIcon.setLayoutParams(iconParams);

            ViewUtils.setMarginTop(tvCaptain, DensityUtils.dp2px(getContext(), 43));
        }
    }

    /** 设置数据 */
    public void setData(UserInfoResp model) {
        boolean hasUser = model.userId > 0;

        if (hasUser) {
            ImageLoader.loadAvatar(ivIcon, model.getUseAvatar());
        } else {
            ImageLoader.loadDrawable(ivIcon, R.drawable.ic_click_join);
        }

        if (hasUser) {
            tvName.setText(model.nickname);
            tvName.setTextColor(Color.WHITE);
        } else {
            tvName.setText(R.string.click_join);
            tvName.setTextColor(Color.parseColor("#66ffffff"));
        }

        if (hasUser && model.isCaptain) {
            tvCaptain.setVisibility(View.VISIBLE);
        } else {
            tvCaptain.setVisibility(View.GONE);
        }
    }

}