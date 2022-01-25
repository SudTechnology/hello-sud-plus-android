package tech.sud.mgp.audio.example.widget.view.chat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.common.utils.DensityUtils;

public class AudioRoomChatView extends ConstraintLayout {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RoomChatAdapter mAdapter;

    public AudioRoomChatView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomChatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomChatView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setPadding(0, 0, 0, DensityUtils.dp2px(getContext(), 16));
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(DensityUtils.dp2px(getContext(), 30));
        mRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.topMargin = DensityUtils.dp2px(getContext(), 36);
        params.bottomToBottom = LayoutParams.BOTTOM;
        addView(mRecyclerView, params);

        mAdapter = new RoomChatAdapter();
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setList(List<Object> list) {
        mAdapter.setList(list);
    }

    public void addMsg(Object obj) {
        mAdapter.addData(obj);
    }

    public void moveToBottom() {
        int scrollToPosition = mAdapter.getData().size() - 1;
        mLayoutManager.scrollToPosition(scrollToPosition);
    }

}
