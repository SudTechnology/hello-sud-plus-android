package tech.sud.mgp.hello.ui.scenes.common.gift.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftItemView;

public class GiftListAdapter extends BaseQuickAdapter<GiftModel, BaseViewHolder> {
    public GiftListAdapter(@Nullable List<GiftModel> data) {
        super(R.layout.item_gift_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, GiftModel giftModel) {
        GiftItemView itemView = baseViewHolder.getView(R.id.item_root_view);
        itemView.setModel(giftModel);
    }
}
