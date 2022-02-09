package tech.sud.mgp.audio.example.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.audio.R;
import tech.sud.mgp.game.middle.model.GameMessageModel;

/**
 * 房间内游戏消息样式
 */
public class RoomGameTextProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_GAME_MSG;
    }

    @Override
    public int getLayoutId() {
        return R.layout.audio_item_room_game_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        GameMessageModel item = (GameMessageModel) o;
        baseViewHolder.setText(R.id.tv_text, item.msg);
    }
}
