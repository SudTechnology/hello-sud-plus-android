package tech.sud.mgp.audio.example.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.audio.R;

public class AudioRoomMicView extends BaseMicView {

    public AudioRoomMicView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomMicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomMicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.audio_view_room_mic, this);
        AudioRoomMicSingleItemView singleItemView = findViewById(R.id.single_item_view);
        mItemViews.add(singleItemView);
        setItemViewClickListener(0, singleItemView);
        LinearLayout firstContainer = findViewById(R.id.first_line_container);
        LinearLayout secondContainer = findViewById(R.id.second_line_container);
        createLineContainer(firstContainer);
        createLineContainer(secondContainer);
    }

    public void createLineContainer(LinearLayout container) {
        for (int i = 0; i < 4; i++) {
            int position = mItemViews.size();
            AudioRoomMicItemView itemView = new AudioRoomMicItemView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            container.addView(itemView, params);
            mItemViews.add(itemView);
            setItemViewClickListener(position, itemView);
        }
    }

    private void setItemViewClickListener(int position, View itemView) {
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMicItemClickListener listener = mOnMicItemClickListener;
                if (listener != null) {
                    listener.onItemClick(v, position);
                }
            }
        });
    }

}
