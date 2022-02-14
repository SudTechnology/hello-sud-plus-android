package tech.sud.mgp.hello.ui.room.audio.example.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.room.audio.example.model.RoomTextModel;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.round.RoundedImageView;

/**
 * 房间内用户说话，公屏消息样式
 */
public class RoomTextProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_TEXT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.audio_item_room_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        RoomTextModel item = (RoomTextModel) o;
        RoundedImageView ivIcon = baseViewHolder.getView(R.id.riv_avatar);
        ImageLoader.loadAvatar(ivIcon, item.avatar);
        String nickName = item.nickName.isEmpty() ? "" : item.nickName;
        baseViewHolder.setText(R.id.tv_name, nickName + "：");
        baseViewHolder.setText(R.id.tv_text, item.text);
    }
}
