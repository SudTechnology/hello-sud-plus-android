package tech.sud.mgp.hello.ui.main.nft.widget.dialog;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ShapeUtils;
import tech.sud.mgp.hello.ui.main.nft.model.WalletChainInfo;

/**
 * nft链选择弹窗
 */
public class NftChainDialog extends BaseDialogFragment {

    private WalletChainInfo selectedChainInfo;
    private List<WalletChainInfo> chainList;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private OnSelectedListener onSelectedListener;

    public static NftChainDialog newInstance(WalletChainInfo selectedChainInfo, List<WalletChainInfo> list) {
        Bundle args = new Bundle();
        NftChainDialog fragment = new NftChainDialog();
        fragment.setArguments(args);
        fragment.selectedChainInfo = selectedChainInfo;
        fragment.chainList = list;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_nft_chain;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new MyAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        View rootView = findViewById(R.id.root_view);
        rootView.setBackground(ShapeUtils.createShape(null, (float) DensityUtils.dp2px(8),
                null, GradientDrawable.RECTANGLE, null, Color.parseColor("#f2f2f2")));
    }

    @Override
    protected void initData() {
        super.initData();
        adapter.setList(chainList);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                WalletChainInfo item = NftChainDialog.this.adapter.getItem(position);
                dismiss();
                if (onSelectedListener != null) {
                    onSelectedListener.onSelected(item);
                }
            }
        });
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(WalletChainInfo chainInfo);
    }

    private class MyAdapter extends BaseQuickAdapter<WalletChainInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_nft_chain);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, WalletChainInfo model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, model.icon);

            holder.setText(R.id.tv_name, model.name);

            if (selectedChainInfo != null && selectedChainInfo.type == model.type) {
                holder.setVisible(R.id.view_selected, true);
            } else {
                holder.setVisible(R.id.view_selected, false);
            }
        }
    }

}
