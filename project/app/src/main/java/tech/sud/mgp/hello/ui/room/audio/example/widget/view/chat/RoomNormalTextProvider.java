package tech.sud.mgp.hello.ui.room.audio.example.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;

/**
 * 房间内，纯文字样式
 */
public class RoomNormalTextProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_NORMAL_MSG;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_audio_room_normal_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        String item = (String) o;
        baseViewHolder.setText(R.id.tv_text, item);
    }
}
