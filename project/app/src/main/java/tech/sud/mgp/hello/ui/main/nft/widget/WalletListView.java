package tech.sud.mgp.hello.ui.main.nft.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ImageUtils;
import tech.sud.mgp.hello.common.widget.view.YStretchDrawable;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel.WalletInfo;

/**
 * nft列表View
 */
public class WalletListView extends ConstraintLayout {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private View viewOperate;
    private View viewArrow;
    private TextView tvOperate;

    private List<WalletInfo> datas;
    private int shirnkCount = 4; // 大于此数，则具备展开收起功能
    private boolean isSpread;

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
        setListeners();
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(context, R.layout.view_wallet_list, this);
        recyclerView = findViewById(R.id.recycler_view);
        View rootView = findViewById(R.id.root_view);
        viewOperate = findViewById(R.id.view_operate);
        tvOperate = findViewById(R.id.tv_operate);
        viewArrow = findViewById(R.id.view_arrow);

        Bitmap bitmap = ImageUtils.getBitmap(R.drawable.ic_wallet_bg, DensityUtils.getScreenWidth(), DensityUtils.getScreenHeight());
        rootView.setBackground(new YStretchDrawable(bitmap));

        recyclerView.setNestedScrollingEnabled(false);
    }

    private void initData() {
        adapter = new MyAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void setListeners() {
        viewOperate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpread = !isSpread;
                updateOperateInfo();
                updateWalletList();
            }
        });
    }

    /** 设置钱包数据 */
    public void setDatas(List<WalletInfo> list) {
        datas = list;
        if (list != null && list.size() > shirnkCount) {
            viewOperate.setVisibility(View.VISIBLE);
            updateOperateInfo();
        } else {
            viewOperate.setVisibility(View.GONE);
        }
        updateWalletList();
    }

    /** 设置钱包点击事件 */
    public void setWalletOnClickListener(WalletOnClickListener listener) {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                walletOnClick(position, listener);
            }
        });
    }

    private void walletOnClick(int position, WalletOnClickListener listener) {
        WalletInfo item = adapter.getItem(position);
        if (listener != null) {
            listener.onClick(item);
        }
    }

    public interface WalletOnClickListener {
        void onClick(WalletInfo walletInfo);
    }

    private void updateOperateInfo() {
        if (isSpread) {
            tvOperate.setText(R.string.pack_up);
            viewArrow.setRotation(180);
        } else {
            tvOperate.setText(R.string.more);
            viewArrow.setRotation(0);
        }
    }

    private void updateWalletList() {
        if (isSpread) {
            adapter.setList(datas);
        } else {
            if (datas != null && datas.size() > shirnkCount) {
                adapter.setList(datas.subList(0, shirnkCount));
            } else {
                adapter.setList(datas);
            }
        }
    }

    private static class MyAdapter extends BaseQuickAdapter<WalletInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_wallet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, WalletInfo walletInfo) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, walletInfo.icon);

            holder.setText(R.id.tv_name, walletInfo.name);
        }
    }

}
