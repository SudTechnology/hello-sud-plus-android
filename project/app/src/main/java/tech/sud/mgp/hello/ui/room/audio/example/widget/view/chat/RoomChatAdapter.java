package tech.sud.mgp.hello.ui.room.audio.example.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;

import java.util.List;

import tech.sud.mgp.hello.ui.room.audio.example.model.RoomTextModel;
import tech.sud.mgp.hello.ui.room.audio.gift.model.GiftNotifyDetailodel;
import tech.sud.mgp.hello.common.widget.adapter.EmptyProvider;

public class RoomChatAdapter extends BaseProviderMultiAdapter<Object> {

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_GIFT_NOTIFY = 2;
    public static final int TYPE_NORMAL_MSG = 3;

    public RoomChatAdapter() {
        addItemProvider(new RoomTextProvider());
        addItemProvider(new RoomNormalTextProvider());
        addItemProvider(new EmptyProvider());
        addItemProvider(new RoomGiftNotifyProvider());
    }

    @Override
    protected int getItemType(@NonNull List<?> list, int position) {
        Object item = list.get(position);
        if (item instanceof RoomTextModel) {
            return TYPE_TEXT;
        } else if (item instanceof GiftNotifyDetailodel) {
            return TYPE_GIFT_NOTIFY;
        } else if (item instanceof String) {
            return TYPE_NORMAL_MSG;
        }
        return EmptyProvider.TYPE_EMPTY;
    }

}