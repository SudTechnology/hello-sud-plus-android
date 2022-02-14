package tech.sud.mgp.hello.common.widget.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;

public class EmptyProvider extends BaseItemProvider<Object> {

    public static final int TYPE_EMPTY = -999;

    @Override
    public int getItemViewType() {
        return TYPE_EMPTY;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_empty;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {

    }
}
