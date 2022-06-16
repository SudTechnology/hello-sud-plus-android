package tech.sud.mgp.hello.ui.scenes.danmaku.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 弹幕场景内的弹幕列表
 */
public class DanmakuListView extends ConstraintLayout {

    public DanmakuListView(@NonNull Context context) {
        this(context, null);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DanmakuListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setBackgroundColor(Color.parseColor("#e6000000"));
    }

}
