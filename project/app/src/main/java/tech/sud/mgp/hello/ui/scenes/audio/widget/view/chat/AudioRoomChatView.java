package tech.sud.mgp.hello.ui.scenes.audio.widget.view.chat;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.widget.view.TopFadingEdgeRecyclerView;

public class AudioRoomChatView extends ConstraintLayout {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RoomChatAdapter mAdapter;
    private AudioRoomChatStyle chatStyle = AudioRoomChatStyle.NORMAL;

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
        mRecyclerView = new TopFadingEdgeRecyclerView(getContext());
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setPadding(0, 0, 0, DensityUtils.dp2px(getContext(), 16));
        mRecyclerView.setVerticalFadingEdgeEnabled(true);
        mRecyclerView.setFadingEdgeLength(DensityUtils.dp2px(getContext(), 30));
        mRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        addView(mRecyclerView);

        mAdapter = new RoomChatAdapter();
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        updateStyle();
    }

    /**
     * 根据不同的模式，显示不同规格的UI
     */
    private void updateStyle() {
        switch (chatStyle) {
            case NORMAL:
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
                params.topMargin = DensityUtils.dp2px(getContext(), 36);
                params.topToTop = LayoutParams.PARENT_ID;
                params.bottomToBottom = LayoutParams.PARENT_ID;
                mRecyclerView.setLayoutParams(params);
                break;
            case GAME:
                params = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtils.dp2px(getContext(), 106));
                params.bottomToBottom = LayoutParams.PARENT_ID;
                mRecyclerView.setLayoutParams(params);
                break;
        }
    }

    public void setList(List<Object> list) {
        mAdapter.setList(list);
    }

    public void addMsg(Object obj) {
        mAdapter.addData(obj);
        moveToBottom();
    }

    public void moveToBottom() {
        int scrollToPosition = mAdapter.getData().size() - 1;
        mLayoutManager.scrollToPosition(scrollToPosition);
    }

    public void setChatStyle(AudioRoomChatStyle style) {
        if (chatStyle == style) {
            return;
        }
        chatStyle = style;
        updateStyle();
    }

    public enum AudioRoomChatStyle {
        NORMAL, // 纯语音聊天时的样式
        GAME    // 玩游戏时的样式
    }

}
