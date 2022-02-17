package tech.sud.mgp.hello.ui.main.settings.fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.room.audio.gift.utils.FileUtils;

/**
 * 展示app占用大小的View
 */
public class AppSizeView extends LinearLayout {

    private final ColorRatioView colorRatioView = new ColorRatioView(getContext());

    public AppSizeView(@NonNull Context context) {
        this(context, null);
    }

    public AppSizeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppSizeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);

        // 显示色值比例的View
        addView(colorRatioView, LayoutParams.MATCH_PARENT, DensityUtils.dp2px(getContext(), 40));
    }

    /**
     * 设置数据
     *
     * @param list
     */
    public void setDatas(List<AppSizeModel> list) {
        clearItemView();
        if (list == null || list.size() == 0) {
            colorRatioView.setDatas(null);
            return;
        }
        // 计算size总量
        long totalSize = 0;
        for (AppSizeModel model : list) {
            totalSize += model.size;
        }

        // 设置View
        List<ColorRatioView.ColorRatioModel> colorRatioModels = new ArrayList<>();
        for (AppSizeModel model : list) {
            float ratio = model.size * 1.0f / totalSize;
            colorRatioModels.add(new ColorRatioView.ColorRatioModel(model.color, ratio));
            addItemView(model);
        }
        colorRatioView.setDatas(colorRatioModels);
    }

    private void clearItemView() {
        removeViews(1, getChildCount() - 1);
    }

    // 增加一个展示大小的View
    private void addItemView(AppSizeModel model) {
        boolean isFirstItemView = getChildCount() == 1;

        // 构建一个容器
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams containerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        containerParams.topMargin = DensityUtils.dp2px(getContext(), isFirstItemView ? 9 : 13);
        addView(container, containerParams);

        // 颜色
        View colorView = new View(getContext());
        colorView.setBackgroundColor(model.color);
        int colorViewSize = DensityUtils.dp2px(getContext(), 16);
        container.addView(colorView, colorViewSize, colorViewSize);
        // 名称
        TextView tvName = new TextView(getContext());
        tvName.setTextSize(12);
        tvName.setTextColor(Color.parseColor("#1a1a1a"));
        tvName.setText(model.name);
        LayoutParams nameParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nameParams.leftMargin = DensityUtils.dp2px(getContext(), 4);
        container.addView(tvName, nameParams);
        // 大小
        TextView tvSize = new TextView(getContext());
        tvSize.setTextSize(10);
        tvSize.setTextColor(Color.parseColor("#8a8a8e"));
        tvSize.setText(FileUtils.formatFileSize(model.size));
        tvSize.setGravity(Gravity.END);
        container.addView(tvSize, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public static class AppSizeModel {
        @ColorInt
        public int color; // 颜色值
        public String name; // 名称
        public long size; // 占用大小

        public AppSizeModel(int color, String name, long size) {
            this.color = color;
            this.name = name;
            this.size = size;
        }
    }

}
