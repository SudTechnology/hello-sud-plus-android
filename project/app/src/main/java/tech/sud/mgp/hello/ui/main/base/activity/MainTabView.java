package tech.sud.mgp.hello.ui.main.base.activity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.common.utils.DensityUtils;

public class MainTabView extends LinearLayout implements View.OnClickListener {
    private ImageView icon;
    private TextView text;
    private TabModel tabModel;
    private TabClickListener tabClickListener;

    public MainTabView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MainTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        setOnClickListener(this);
        icon = new ImageView(context);
        LayoutParams iconParams = new LayoutParams(DensityUtils.dp2px(context, 24.0f), DensityUtils.dp2px(context, 24.0f));
        icon.setLayoutParams(iconParams);
        icon.setColorFilter(Color.parseColor("#8c8c8c"));
        addView(icon);
        text = new TextView(context);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(textParams);
        text.setTextSize(10);
        text.setTextColor(Color.parseColor("#8c8c8c"));
        addView(text);
    }

    public void setData(TabModel model) {
        this.tabModel = model;
        text.setText(model.text);
        icon.setImageResource(model.iconId);
    }

    public void setTabClickListener(TabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }

    public void setViewState(boolean isSelected) {
        if (isSelected) {
            icon.setColorFilter(Color.parseColor("#1a1a1a"));
            text.setTextColor(Color.parseColor("#1a1a1a"));
        } else {
            icon.setColorFilter(Color.parseColor("#8c8c8c"));
            text.setTextColor(Color.parseColor("#8c8c8c"));
        }
    }

    @Override
    public void onClick(View v) {
        if (this.tabClickListener != null) {
            this.tabClickListener.onChangePage(this.tabModel.index);
        }
    }

    public interface TabClickListener {
        void onChangePage(int index);
    }

}
