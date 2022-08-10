package tech.sud.mgp.hello.ui.main.settings.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.main.settings.model.NftModel;

/**
 * nft列表 适配器
 */
public class NftListAdapter extends BaseQuickAdapter<NftModel, BaseViewHolder> implements LoadMoreModule {

    public NftListAdapter() {
        super(R.layout.item_nft);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, NftModel nftModel) {
        ImageView ivIcon = holder.getView(R.id.iv_icon);
        ImageLoader.loadNftImage(ivIcon, nftModel.metadataModel.image, DensityUtils.dp2px(6));

        holder.setText(R.id.tv_name, nftModel.getName());
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
