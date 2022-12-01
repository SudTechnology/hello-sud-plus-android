package tech.sud.mgp.hello.ui.scenes.crossapp.widget.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 跨域 显示当前状态的View
 */
public class CrossAppStatusView extends ConstraintLayout {

    private CrossAppTeamView crossAppTeamView = new CrossAppTeamView(getContext());
    private CrossAppMatchView crossAppMatchView = new CrossAppMatchView(getContext());

    public CrossAppStatusView(@NonNull Context context) {
        this(context, null);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CrossAppStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        addView(crossAppTeamView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(crossAppMatchView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

}
