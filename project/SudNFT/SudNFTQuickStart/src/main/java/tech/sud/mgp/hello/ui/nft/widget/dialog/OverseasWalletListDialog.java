package tech.sud.mgp.hello.ui.nft.widget.dialog;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseBottomSheetDialogFragment;
import tech.sud.mgp.hello.common.listener.CompletedListener;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.ui.nft.listener.OnSelectedWalletListener;
import tech.sud.mgp.hello.ui.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * 海外钱包列表弹窗
 */
public class OverseasWalletListDialog extends BaseBottomSheetDialogFragment {

    private MyAdapter adapter;
    private OnSelectedWalletListener onSelectedWalletListener;
    public NFTViewModel viewModel = new NFTViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_overseas_wallet_list;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getScreenHeight() * 0.645);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                refreshData(sudNFTGetWalletListModel);
            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtils.showLong(ResponseUtils.conver(code, msg));
            }
        });
    }

    private void refreshData(SudNFTGetWalletListModel resp) {
        if (resp == null) {
            return;
        }
        LifecycleUtils.safeLifecycle(this, new CompletedListener() {
            @Override
            public void onCompleted() {
                adapter.setList(filterData(resp.walletList));
            }
        });
    }

    private List<SudNFTGetWalletListModel.WalletInfo> filterData(List<SudNFTGetWalletListModel.WalletInfo> walletList) {
        if (walletList == null) {
            return null;
        }
        List<SudNFTGetWalletListModel.WalletInfo> list = new ArrayList<>();
        for (SudNFTGetWalletListModel.WalletInfo walletInfo : walletList) {
            if (walletInfo.zoneType == ZoneType.OVERSEAS) {
                list.add(walletInfo);
            }
        }
        return list;
    }

    @Override
    protected void setBehavior(BottomSheetBehavior<View> behavior) {
        super.setBehavior(behavior);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                SudNFTGetWalletListModel.WalletInfo item = adapter.getItem(position);
                if (onSelectedWalletListener != null) {
                    onSelectedWalletListener.onSelected(item);
                }
            }
        });
    }

    public void setOnSelectedWalletListener(OnSelectedWalletListener onSelectedWalletListener) {
        this.onSelectedWalletListener = onSelectedWalletListener;
    }

    private static class MyAdapter extends BaseQuickAdapter<SudNFTGetWalletListModel.WalletInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_overseas_wallet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, SudNFTGetWalletListModel.WalletInfo walletInfo) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, walletInfo.icon);

            holder.setText(R.id.tv_name, walletInfo.name);
        }
    }

}
