package tech.sud.mgp.hello.ui.scenes.audio.widget.view.mic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;

/**
 * 语聊房开启游戏时显示的麦位
 */
public class AudioRoomGameMicView extends BaseMicView {

    public AudioRoomGameMicView(@NonNull Context context) {
        this(context, null);
    }

    public AudioRoomGameMicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRoomGameMicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_room_game_mic, this);
        LinearLayout container = findViewById(R.id.ll_container);
        for (int i = 0; i < 9; i++) {
            AudioRoomGameMicItemView itemView = new AudioRoomGameMicItemView(getContext());
            container.addView(itemView, DensityUtils.dp2px(getContext(), 48), LayoutParams.WRAP_CONTENT);
            mItemViews.add(itemView);
            int finalI = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnMicItemClickListener listener = mOnMicItemClickListener;
                    if (listener != null) {
                        listener.onItemClick(v, finalI);
                    }
                }
            });
        }
    }


}
