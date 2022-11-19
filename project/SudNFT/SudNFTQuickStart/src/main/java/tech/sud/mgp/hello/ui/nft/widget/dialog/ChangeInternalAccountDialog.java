package tech.sud.mgp.hello.ui.nft.widget.dialog;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.ui.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.nft.viewmodel.NFTViewModel;

/**
 * 切换国内nft账号
 */
public class ChangeInternalAccountDialog extends BaseDialogFragment {

    private MyAdapter adapter;
    private final NFTViewModel viewModel = new NFTViewModel();
    private ChangeAccountListener changeAccountListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_change_internal_account;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(296);
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
    protected void initData() {
        super.initData();
        viewModel.initNFT(getContext());

        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            adapter.setList(bindWalletInfo.walletList);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                WalletInfoModel item = adapter.getItem(position);
                if (isUseWallet(item.type)) {
                    return;
                }
                dismiss();
                viewModel.changeWallet(item.type);
                if (changeAccountListener != null) {
                    changeAccountListener.onChange(item);
                }
            }
        });
    }

    public void setChangeAccountListener(ChangeAccountListener changeAccountListener) {
        this.changeAccountListener = changeAccountListener;
    }

    public interface ChangeAccountListener {
        void onChange(WalletInfoModel model);
    }

    private class MyAdapter extends BaseQuickAdapter<WalletInfoModel, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_internal_account);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, WalletInfoModel model) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadImage(ivIcon, model.icon);

            holder.setText(R.id.tv_name, model.name);

            holder.setVisible(R.id.view_selected, isUseWallet(model.type));
        }
    }

    private boolean isUseWallet(long walletType) {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            return bindWalletInfo.walletType == walletType;
        }
        return false;
    }

}
