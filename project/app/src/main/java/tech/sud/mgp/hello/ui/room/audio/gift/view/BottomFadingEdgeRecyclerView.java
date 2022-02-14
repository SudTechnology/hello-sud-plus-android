package tech.sud.mgp.hello.ui.room.audio.gift.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class BottomFadingEdgeRecyclerView extends RecyclerView {
    public BottomFadingEdgeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public BottomFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomFadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 0f;
    }
}
