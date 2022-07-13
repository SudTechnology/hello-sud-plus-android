package tech.sud.mgp.hello.ui.main.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tech.sud.mgp.hello.R;

public class HomeHeaderView extends ConstraintLayout {

    private TextView tvInfo;

    public HomeHeaderView(@NonNull Context context) {
        this(context, null);
    }

    public HomeHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HomeHeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_home_header, this);
        tvInfo = findViewById(R.id.tv_info);
    }

    public void setCustomConfigOnClickListener(OnClickListener listener) {
        findViewById(R.id.iv_custom_config).setOnClickListener(listener);
    }

    public void setInfo(String text) {
        tvInfo.setText(text);
    }

}
