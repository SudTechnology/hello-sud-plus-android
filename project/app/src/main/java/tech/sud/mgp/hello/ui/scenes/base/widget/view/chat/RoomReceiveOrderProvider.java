package tech.sud.mgp.hello.ui.scenes.base.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.base.model.GameTextModel;
import tech.sud.mgp.hello.ui.scenes.orderentertainment.model.ReceiveInviteMsgModel;

/**
 * 房间内，主播接受点单公屏消息
 * 暂时未使用
 */
@Deprecated
public class RoomReceiveOrderProvider extends BaseItemProvider<Object> {
    @Override
    public int getItemViewType() {
        return RoomChatAdapter.TYPE_RECEIVE_INVITE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_room_receive_order;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        ReceiveInviteMsgModel item = (ReceiveInviteMsgModel) o;
        String text = getContext().getString(R.string.user_receive_invite_msg,item.userName,item.gameName);
        baseViewHolder.setText(R.id.tv_text, text);
    }
}
