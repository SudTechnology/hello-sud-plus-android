package tech.sud.mgp.hello.ui.main.discover;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;

/**
 * 发现页房间列表
 */
public class DiscoverRoomAdapter extends BaseQuickAdapter<DiscoverRoomModel, BaseViewHolder> implements LoadMoreModule {
    public DiscoverRoomAdapter() {
        super(R.layout.item_discover_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, DiscoverRoomModel discoverRoomModel) {

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
