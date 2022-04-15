package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;

/**
 * 房间内，游戏消息
 */
public class RoomGameTextProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_GAME_MSG;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_room_game_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        GameTextModel item = (GameTextModel) o;
        baseViewHolder.setText(R.id.tv_text, item.message);
    }
}
