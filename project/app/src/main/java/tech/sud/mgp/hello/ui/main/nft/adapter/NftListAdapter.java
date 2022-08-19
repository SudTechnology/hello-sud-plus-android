package tech.sud.mgp.hello.ui.main.nft.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.widget.view.CustomColorDrawable;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;

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
        CustomColorDrawable drawable = new CustomColorDrawable();
        drawable.setRadius(DensityUtils.dp2px(6));
        drawable.setStartColor(Color.parseColor("#0d000000"));
        drawable.setEndColor(Color.parseColor("#1a000000"));

        ImageLoader.loadNftImage(ivIcon, nftModel.coverUrl, drawable);

        holder.setText(R.id.tv_name, nftModel.name);

        holder.setVisible(R.id.tv_dressed_in, nftModel.isDressedIn);
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(baseQuickAdapter);
    }
}
