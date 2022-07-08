package tech.sud.mgp.hello.ui.scenes.common.gift.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.ui.scenes.common.gift.model.GiftModel;
import tech.sud.mgp.hello.ui.scenes.common.gift.view.GiftItemView;

public class GiftListAdapter extends BaseQuickAdapter<GiftModel, BaseViewHolder> {

    public boolean isShowFlag; // 是否显示标记

    public GiftListAdapter() {
        super(R.layout.item_gift_list);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, GiftModel giftModel) {
        GiftItemView itemView = baseViewHolder.getView(R.id.item_root_view);
        itemView.isShowFlag = isShowFlag;
        itemView.setModel(giftModel);
    }
}
