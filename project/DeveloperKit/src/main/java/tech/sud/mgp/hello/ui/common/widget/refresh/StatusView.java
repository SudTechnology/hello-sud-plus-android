package tech.sud.mgp.hello.ui.common.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 状态View，待实现
 * 1,显示错误
 */
public class StatusView extends ConstraintLayout {

    private RefreshListener refreshListener;

    public StatusView(@NonNull Context context) {
        this(context, null);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    /** 设置是否可以刷新 */
    public void setRefreshEnable(boolean enable) {
    }

    /** 设置刷新监听器 */
    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface RefreshListener {
        void onRefresh();
    }

}
