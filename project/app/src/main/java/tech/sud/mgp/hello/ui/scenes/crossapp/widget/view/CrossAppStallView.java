package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;

/**
 * 跨域 组队状态下车位View
 */
public class CrossAppStallView extends ConstraintLayout {

    private final List<UserInfoResp> datas = new ArrayList<>();
    private final List<CrossAppStallItemView> itemViewList = new ArrayList<>();
    private final LinearLayout container1 = new LinearLayout(getContext());
    private final LinearLayout container2 = new LinearLayout(getContext());

    public CrossAppStallView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppStallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppStallView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppStallView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        container1.setOrientation(LinearLayout.HORIZONTAL);
        container2.setOrientation(LinearLayout.HORIZONTAL);
        container1.setGravity(Gravity.CENTER);
        container2.setGravity(Gravity.CENTER);

        container1.setId(View.generateViewId());

        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(container1, params1);

        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params2.topToBottom = container1.getId();
        params2.topMargin = DensityUtils.dp2px(getContext(), 30);
        addView(container2, params2);
    }

    /** 设置数据 */
    public void setDatas(List<UserInfoResp> list) {
        int oldCount = datas.size();
        datas.clear();
        int newCount = 0;
        if (list != null) {
            datas.addAll(list);
            newCount = list.size();
        }
        if (oldCount == newCount) {
            notifyDataSetChanged();
        } else {
            initItemView();
        }
    }

    private void notifyDataSetChanged() {
        for (int i = 0; i < datas.size(); i++) {
            UserInfoResp model = datas.get(i);
            CrossAppStallItemView itemView = getItemView(i);
            if (itemView != null) {
                itemView.setData(model);
            }
        }
    }

    private CrossAppStallItemView getItemView(int index) {
        if (index >= 0 && itemViewList.size() > 0 && index < itemViewList.size()) {
            return itemViewList.get(index);
        }
        return null;
    }

    private void initItemView() {
        container1.removeAllViews();
        container2.removeAllViews();
        itemViewList.clear();
        if (datas.size() == 0) {
            return;
        }
        switch (datas.size()) {
            case 1:
            case 2:
                createItemViewList(container1, datas, true, DensityUtils.dp2px(getContext(), 107));
                break;
            case 3:
                createItemViewList(container1, datas, true, DensityUtils.dp2px(getContext(), 59));
                break;
            case 4:
                createItemViewList(container1, datas, true, DensityUtils.dp2px(getContext(), 26));
                break;
            case 6:
                createItemViewList(container1, datas.subList(0, 3), true, DensityUtils.dp2px(getContext(), 59));
                createItemViewList(container2, datas.subList(3, 6), false, DensityUtils.dp2px(getContext(), 59));
                break;
            default:
                createItemViewList(container1, datas.subList(0, 4), true, DensityUtils.dp2px(getContext(), 26));
                createItemViewList(container2, datas.subList(4, datas.size()), false, DensityUtils.dp2px(getContext(), 11));
                break;
        }
        notifyDataSetChanged();
    }

    private void createItemViewList(LinearLayout container, List<UserInfoResp> list, boolean isFirstLine, int secondMarginStart) {
        for (int i = 0; i < list.size(); i++) {
            CrossAppStallItemView itemView = new CrossAppStallItemView(getContext());
            itemViewList.add(itemView);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.setMarginStart(secondMarginStart);
            }
            container.addView(itemView, params);
            if (isFirstLine) {
                itemView.setMode(i == 0);
            } else {
                itemView.setMode(false);
            }
        }
    }


}