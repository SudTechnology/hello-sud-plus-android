package tech.sud.mgp.hello.ui.scenes.disco.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;
import tech.sud.mgp.hello.ui.scenes.common.cmd.model.disco.ContributionModel;

/**
 * 在蹦迪房间 顶部，显示排行前三的View
 */
public class DiscoRankingView extends FrameLayout {

    private ConstraintLayout viewRanking1;
    private ImageView ivIcon1;
    private ConstraintLayout viewRanking2;
    private ImageView ivIcon2;
    private ConstraintLayout viewRanking3;
    private ImageView ivIcon3;

    public DiscoRankingView(Context context) {
        this(context, null);
    }

    public DiscoRankingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscoRankingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DiscoRankingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        viewRanking1 = createRankingView(1);
        ivIcon1 = viewRanking1.findViewById(R.id.iv_icon);
        viewRanking2 = createRankingView(2);
        ivIcon2 = viewRanking2.findViewById(R.id.iv_icon);
        viewRanking3 = createRankingView(3);
        ivIcon3 = viewRanking3.findViewById(R.id.iv_icon);
    }

    @SuppressLint("SetTextI18n")
    private ConstraintLayout createRankingView(int ranking) {
        ConstraintLayout viewGroup = new ConstraintLayout(getContext());

        int color;
        switch (ranking) {
            case 1:
                color = Color.parseColor("#fed786");
                break;
            case 2:
                color = Color.parseColor("#c5d4d7");
                break;
            default:
                color = Color.parseColor("#b6946e");
                break;
        }

        RoundedImageView ivIcon = new RoundedImageView(getContext());
        ivIcon.setId(R.id.iv_icon);
        ivIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivIcon.setOval(true);
        ivIcon.setBorderWidth(DensityUtils.dp2px(getContext(), 1));
        ivIcon.setBorderColor(color);
        int iconSize = DensityUtils.dp2px(24);
        ConstraintLayout.LayoutParams iconParams = new ConstraintLayout.LayoutParams(iconSize, iconSize);
        iconParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        iconParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        viewGroup.addView(ivIcon, iconParams);

        TextView tv = new TextView(getContext());
        tv.setTextSize(6);
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        tv.setText(ranking + "");
        tv.setGravity(Gravity.CENTER);
        int paddingHorizontal = DensityUtils.dp2px(getContext(), 5);
        tv.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tv.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(getContext(), 10), null,
                GradientDrawable.RECTANGLE, null, color));
        ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        tvParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.topMargin = DensityUtils.dp2px(getContext(), 19);
        viewGroup.addView(tv, tvParams);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMarginStart(DensityUtils.dp2px(getContext(), 20) * getChildCount());
        addView(viewGroup, params);
        return viewGroup;
    }

    /** 设置数据 */
    public void setDatas(List<ContributionModel> list) {
        if (list == null) {
            viewRanking1.setVisibility(View.GONE);
            viewRanking2.setVisibility(View.GONE);
            viewRanking3.setVisibility(View.GONE);
            return;
        }
        switch (list.size()) {
            case 0:
                viewRanking1.setVisibility(View.GONE);
                viewRanking2.setVisibility(View.GONE);
                viewRanking3.setVisibility(View.GONE);
                break;
            case 1:
                viewRanking1.setVisibility(View.VISIBLE);
                viewRanking2.setVisibility(View.GONE);
                viewRanking3.setVisibility(View.GONE);
                break;
            case 2:
                viewRanking1.setVisibility(View.VISIBLE);
                viewRanking2.setVisibility(View.VISIBLE);
                viewRanking3.setVisibility(View.GONE);
                break;
            case 3:
                viewRanking1.setVisibility(View.VISIBLE);
                viewRanking2.setVisibility(View.VISIBLE);
                viewRanking3.setVisibility(View.VISIBLE);
                break;
        }
        for (int i = 0; i < list.size(); i++) {
            ContributionModel model = list.get(i);
            switch (i) {
                case 0:
                    setIcon(ivIcon1, model);
                    break;
                case 1:
                    setIcon(ivIcon2, model);
                    break;
                case 2:
                    setIcon(ivIcon3, model);
                    break;
            }
        }
    }

    private void setIcon(ImageView ivIcon, ContributionModel model) {
        if (model.fromUser == null) {
            ImageLoader.loadAvatar(ivIcon, null);
        } else {
            ImageLoader.loadAvatar(ivIcon, model.fromUser.icon);
        }
    }

}
