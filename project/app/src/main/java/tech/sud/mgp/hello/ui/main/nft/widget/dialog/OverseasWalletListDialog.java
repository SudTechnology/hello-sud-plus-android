package tech.sud.mgp.hello.ui.main.nft.widget.dialog;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseBottomSheetDialogFragment;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ImageLoader;
import tech.sud.mgp.hello.common.utils.ResponseUtils;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.WalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.CancelWearNftListener;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.listener.ISudNFTListenerGetWalletList;
import tech.sud.nft.core.listener.ISudNFTListenerUnbindWallet;
import tech.sud.nft.core.model.resp.SudNFTGetWalletListModel;

/**
 * 海外钱包列表弹窗
 */
public class OverseasWalletListDialog extends BaseBottomSheetDialogFragment {

    private MyAdapter adapter;
    private OperateListener operateListener;
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
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        viewModel.getWalletList(new ISudNFTListenerGetWalletList() {
            @Override
            public void onSuccess(SudNFTGetWalletListModel sudNFTGetWalletListModel) {
                getDataSuccess(sudNFTGetWalletListModel);
            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtils.showLong(ResponseUtils.conver(code, msg));
            }
        });
    }

    private void getDataSuccess(SudNFTGetWalletListModel resp) {
        if (resp == null) {
            return;
        }
        LifecycleUtils.safeLifecycle(this, new CompletedListener() {
            @Override
            public void onCompleted() {
                List<SudNFTGetWalletListModel.WalletInfo> list = filterData(resp.walletList);
                list = sortList(list);
                adapter.setList(list);
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

    private List<SudNFTGetWalletListModel.WalletInfo> sortList(List<SudNFTGetWalletListModel.WalletInfo> walletList) {
        if (walletList == null) {
            return null;
        }
        ArrayList<SudNFTGetWalletListModel.WalletInfo> list = new ArrayList<>(walletList);
        Collections.sort(list, new Comparator<SudNFTGetWalletListModel.WalletInfo>() {
            @Override
            public int compare(SudNFTGetWalletListModel.WalletInfo o1, SudNFTGetWalletListModel.WalletInfo o2) {
                if (o1 == null) {
                    return -1;
                }
                if (o2 == null) {
                    return 1;
                }
                boolean o1IsBinding = isBinding(o1.type);
                boolean o2IsBinding = isBinding(o2.type);
                if (o1IsBinding && o2IsBinding) {
                    return Integer.compare(list.indexOf(o1), list.indexOf(o2));
                }
                if (o1IsBinding) {
                    return -1;
                }
                if (o2IsBinding) {
                    return 1;
                }
                return Integer.compare(list.indexOf(o1), list.indexOf(o2));
            }
        });
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
        adapter.addChildClickViewIds(R.id.tv_btn);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                SudNFTGetWalletListModel.WalletInfo item = adapter.getItem(position);
                if (view.getId() == R.id.tv_btn) {
                    if (isBinding(item.type)) { // 解绑
                        onClickUnbindWallet(item);
                    } else { // 绑定
                        if (operateListener == null) {
                            dismiss();
                        } else {
                            operateListener.onBind(item);
                        }
                    }
                }
            }
        });
    }

    private void onClickUnbindWallet(SudNFTGetWalletListModel.WalletInfo item) {
        UnbindOverseasWalletDialog dialog = UnbindOverseasWalletDialog.newInstance(item);
        dialog.setOnUnbindListener(new UnbindOverseasWalletDialog.OnUnbindListener() {
            @Override
            public void onUnbind() {
                unbindWalletPre(item);
            }
        });
        dialog.show(getChildFragmentManager(), null);
    }

    /** 执行解绑预处理 */
    private void unbindWalletPre(SudNFTGetWalletListModel.WalletInfo item) {
        // 如果当前穿戴所属为该钱包，则先进行解除穿戴
        NftModel wearNft = viewModel.getWearNft();
        if (wearNft != null && wearNft.walletType == item.type) {
            viewModel.cancelWearNft(new CancelWearNftListener() {
                @Override
                public void onSuccess() {
                    unbindWallet(item);
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                }
            });
        } else {
            unbindWallet(item);
        }
    }

    /** 执行解绑 */
    private void unbindWallet(SudNFTGetWalletListModel.WalletInfo item) {
        viewModel.unbindWallet(item.type, new ISudNFTListenerUnbindWallet() {
            @Override
            public void onSuccess() {
                LifecycleUtils.safeLifecycle(getViewLifecycleOwner(), () -> {
                    unbindWalletSuccess(item);
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtils.showLong(ResponseUtils.nftConver(code, msg));
            }
        });
    }

    private void unbindWalletSuccess(SudNFTGetWalletListModel.WalletInfo item) {
        if (operateListener != null) {
            operateListener.onUnbindCompleted(item);
        }
        if (existsBindWallet()) {
            refreshData();
        } else {
            dismiss();
        }
    }

    /** 检查是否存在绑定了的钱包 */
    private boolean existsBindWallet() {
        return viewModel.getBindWalletInfo() != null;
    }

    public void setOperateListener(OperateListener operateListener) {
        this.operateListener = operateListener;
    }

    public interface OperateListener {
        void onBind(SudNFTGetWalletListModel.WalletInfo walletInfo);

        void onUnbindCompleted(SudNFTGetWalletListModel.WalletInfo walletInfo);
    }

    private class MyAdapter extends BaseQuickAdapter<SudNFTGetWalletListModel.WalletInfo, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_overseas_wallet);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, SudNFTGetWalletListModel.WalletInfo walletInfo) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            ImageLoader.loadAvatar(ivIcon, walletInfo.icon);

            holder.setText(R.id.tv_name, walletInfo.name);

            boolean isBinding = isBinding(walletInfo.type);
            TextView tvBtn = holder.getView(R.id.tv_btn);
            if (isBinding) {
                tvBtn.setText(R.string.unbind);
                tvBtn.setTextColor(Color.BLACK);
                tvBtn.setBackgroundResource(R.drawable.shape_stroke_black);
            } else {
                tvBtn.setText(R.string.bind);
                tvBtn.setTextColor(Color.WHITE);
                tvBtn.setBackgroundColor(Color.BLACK);
            }

            TextView tvBindInfo = holder.getView(R.id.tv_bind_info);
            View containerBindInfo = holder.getView(R.id.container_bind_info);

            if (isBinding) {
                String address = getBindAddress(walletInfo.type);
                if (address == null) {
                    address = "";
                }
                tvBindInfo.setText(address);
                containerBindInfo.setVisibility(View.VISIBLE);
            } else {
                containerBindInfo.setVisibility(View.GONE);
            }
        }
    }

    public boolean isBinding(long walletType) {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            return bindWalletInfo.isContainer(walletType);
        }
        return false;
    }

    public String getBindAddress(long walletType) {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null) {
            WalletInfoModel walletInfoModel = bindWalletInfo.getWalletInfoModel(walletType);
            if (walletInfoModel != null) {
                return walletInfoModel.walletAddress;
            }
        }
        return null;
    }

}
