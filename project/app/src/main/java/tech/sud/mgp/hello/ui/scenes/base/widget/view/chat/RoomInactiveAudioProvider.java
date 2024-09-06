package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.scenes.base.model.RoomInactiveAudioModel;

/**
 * 房间内用户说话，一个没有实际内容的音频消息展示
 */
public class RoomInactiveAudioProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_INACTIVE_AUDIO;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_audio_room_inactive_audio;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        RoomInactiveAudioModel item = (RoomInactiveAudioModel) o;
        ImageView ivIcon = baseViewHolder.getView(R.id.riv_avatar);
        ImageLoader.loadAvatar(ivIcon, item.avatar);
        String nickName = item.nickName.isEmpty() ? "" : item.nickName;
        baseViewHolder.setText(R.id.tv_name, nickName + "：");
    }
}
