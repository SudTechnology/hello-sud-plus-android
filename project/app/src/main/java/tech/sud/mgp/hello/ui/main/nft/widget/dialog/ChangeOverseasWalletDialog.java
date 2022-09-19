package tech.sud.mgp.hello.ui.main.nft.widget.dialog;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * 切换国外钱包弹窗
 */
public class ChangeOverseasWalletDialog extends BaseDialogFragment {

    private final NFTViewModel viewModel = new NFTViewModel();
    private final MyAdapter mAdapter = new MyAdapter();
    private ChangeWalletListener changeWalletListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_change_overseas_wallet;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null && bindWalletInfo.walletList != null) {
            ArrayList<WalletInfoModel> list = new ArrayList<>(bindWalletInfo.walletList);
            viewModel.getWalletList(new ISudNFTListenerGetWalletList() {
                @Override
                public void onSuccess(SudNFTGetWalletListModel model) {
                    LifecycleUtils.safeLifecycle(fragment, () -> {
                        Collections.sort(list, new Comparator<WalletInfoModel>() {
                            @Override
                            public int compare(WalletInfoModel o1, WalletInfoModel o2) {
                                return Integer.compare(findIndex(model, o1), findIndex(model, o2));
                            }
                        });
                        mAdapter.setList(list);
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    LifecycleUtils.safeLifecycle(fragment, () -> {
                        mAdapter.setList(list);
                    });
                }
            });
        }
    }

    private int findIndex(SudNFTGetWalletListModel listModel, WalletInfoModel model) {
        if (listModel != null && listModel.walletList != null) {
            for (int i = 0; i < listModel.walletList.size(); i++) {
                SudNFTGetWalletListModel.WalletInfo walletInfo = listModel.walletList.get(i);
                if (walletInfo.type == model.type) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                WalletInfoModel item = mAdapter.getItem(position);
                if (isSelected(item.type)) {
                    dismiss();
                    return;
                }
                viewModel.changeWallet(item.type);
                mAdapter.notifyDataSetChanged();
                dismiss();
                if (changeWalletListener != null) {
                    changeWalletListener.onChange(item);
                }
            }
        });
    }

    public void setChangeWalletListener(ChangeWalletListener changeWalletListener) {
        this.changeWalletListener = changeWalletListener;
    }

    public interface ChangeWalletListener {
        void onChange(WalletInfoModel model);
    }

    private class MyAdapter extends BaseQuickAdapter<WalletInfoModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_change_overseas_wallet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, WalletInfoModel model) {
            holder.setText(R.id.tv_address, model.walletAddress);
            holder.setText(R.id.tv_name, model.name);
            holder.setVisible(R.id.view_selected, isSelected(model.type));
        }
    }

    private boolean isSelected(long walletType) {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            return bindWalletInfo.walletType == walletType;
        }
        return false;
    }

}
