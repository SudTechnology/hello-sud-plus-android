package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomTextModel;

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
        return R.layout.item_audio_room_text;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        RoomTextModel item = (RoomTextModel) o;
        ImageView ivIcon = baseViewHolder.getView(R.id.riv_avatar);
        ImageLoader.loadAvatar(ivIcon, item.avatar);
        String nickName = item.nickName.isEmpty() ? "" : item.nickName;
        baseViewHolder.setText(R.id.tv_name, nickName + "：");
        baseViewHolder.setText(R.id.tv_text, item.text);
    }
}
