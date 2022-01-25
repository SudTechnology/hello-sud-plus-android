package tech.sud.mgp.audio.example.widget.view.chat;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;

import java.util.List;

import tech.sud.mgp.audio.example.model.RoomTextModel;
import tech.sud.mgp.common.widget.adapter.EmptyProvider;

public class RoomChatAdapter extends BaseProviderMultiAdapter<Object> {

    public static final int TYPE_TEXT = 1;

    public RoomChatAdapter() {
        addItemProvider(new RoomTextProvider());
        addItemProvider(new EmptyProvider());
    }

    @Override
    protected int getItemType(@NonNull List<?> list, int position) {
        Object item = list.get(position);
        if (item instanceof RoomTextModel) {
            return TYPE_TEXT;
        }
        return EmptyProvider.TYPE_EMPTY;
    }

}
