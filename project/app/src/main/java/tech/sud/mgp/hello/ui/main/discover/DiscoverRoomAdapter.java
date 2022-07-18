package tech.sud.mgp.hello.ui.main.discover;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.service.main.resp.AuthRoomModel;

/**
 * 发现页房间列表
 */
public class DiscoverRoomAdapter extends BaseQuickAdapter<AuthRoomModel, BaseViewHolder> implements LoadMoreModule {
    public DiscoverRoomAdapter() {
        super(R.layout.item_discover_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, AuthRoomModel discoverRoomModel) {

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
