package tech.sud.mgp.hello.common.widget.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TopFadingEdgeRecyclerView extends RecyclerView {

    public TopFadingEdgeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TopFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0;
    }
}
