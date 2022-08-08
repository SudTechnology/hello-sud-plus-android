package tech.sud.mgp.hello.ui.main.settings.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ImageUtils;
import tech.sud.mgp.hello.common.widget.view.YStretchDrawable;
import tech.sud.nft.core.model.SudNFTGetWalletListModel;

/**
 * nft列表View
 */
public class WalletListView extends ConstraintLayout {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    public WalletListView(@NonNull Context context) {
        this(context, null);
    }

    public WalletListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WalletListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
        initData();
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_wallet_list, this);
        recyclerView = findViewById(R.id.recycler_view);
        View rootView = findViewById(R.id.root_view);
//        rootView.setBackgroundResource(R.drawable.ic_wallet_bg);
        Bitmap bitmap = ImageUtils.getBitmap(R.drawable.ic_wallet_bg, DensityUtils.getScreenWidth(), DensityUtils.getScreenHeight());
        rootView.setBackground(new YStretchDrawable(bitmap));
    }

    private void initData() {
        adapter = new MyAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    public void setDatas(List<SudNFTGetWalletListModel.WalletInfo> list) {
        adapter.setList(list);
    }

    private static class MyAdapter extends BaseQuickAdapter<SudNFTGetWalletListModel.WalletInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_wallet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, SudNFTGetWalletListModel.WalletInfo walletInfo) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, walletInfo.icon);

            holder.setText(R.id.tv_name, walletInfo.name);
        }
    }

}
