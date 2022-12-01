package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 跨域 匹配状态View
 */
public class CrossAppMatchView extends ConstraintLayout {

    public CrossAppMatchView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppMatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
