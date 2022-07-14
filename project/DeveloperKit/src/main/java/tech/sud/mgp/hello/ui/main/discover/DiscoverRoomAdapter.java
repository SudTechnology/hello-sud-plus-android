package tech.sud.mgp.hello.ui.main.discover;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.ImageLoader;

/**
 * 发现页房间列表
 */
public class DiscoverRoomAdapter extends BaseQuickAdapter<DiscoverRoomModel, BaseViewHolder> implements LoadMoreModule {

    public DiscoverRoomAdapter() {
        super(R.layout.item_discover_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, DiscoverRoomModel model) {
        ImageView ivIcon = holder.getView(R.id.iv_icon);
        ImageLoader.loadAvatar(ivIcon, null);

        holder.setText(R.id.tv_id, model.room_id);
        holder.setText(R.id.tv_count, model.player_total + model.ob_total + "");

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
    
}
