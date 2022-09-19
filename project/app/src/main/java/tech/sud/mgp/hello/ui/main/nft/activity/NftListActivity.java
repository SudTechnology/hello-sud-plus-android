package tech.sud.mgp.hello.ui.main.nft.activity;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.common.widget.HSTopBar;
import tech.sud.mgp.hello.ui.common.widget.refresh.ListModel;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.main.nft.adapter.NftListAdapter;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftListResultModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.model.ZoneType;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.nft.core.model.param.SudNFTGetCnNFTListParamModel;
import tech.sud.nft.core.model.param.SudNFTGetNFTListParamModel;

/**
 * NFT列表 页面
 */
public class NftListActivity extends BaseActivity {

    private NftListAdapter adapter;
    private final NFTViewModel viewModel = new NFTViewModel();
    private RefreshView refreshView;
    private HSTopBar topBar;
    private RefreshDataHelper<NftModel> refreshDataHelper;
    private final Map<Integer, String> pageKeyMaps = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nft_list;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        adapter = new NftListAdapter();
        refreshView = findViewById(R.id.refresh_view);
        topBar = findViewById(R.id.top_bar);

        initRecyclerView();
        initRefreshDataHelper();
    }

    private void initRefreshDataHelper() {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        refreshDataHelper = new RefreshDataHelper<NftModel>() {
            @Override
            protected RefreshView getRefreshView() {
                return refreshView;
            }

            @Override
            protected RecyclerView.LayoutManager getLayoutManager() {
                return new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            }

            @Override
            protected BaseQuickAdapter<NftModel, BaseViewHolder> getAdapter() {
                return adapter;
            }

            @Override
            protected GetDataListener getDataListener() {
                return new GetDataListener() {
                    @Override
                    public void onGetData(int pageNumber, int pageSize) {
                        getData(pageNumber, pageSize);
                    }
                };
            }
        };
        if (bindWalletInfo != null && bindWalletInfo.getZoneType() == ZoneType.OVERSEAS) {
            refreshDataHelper.setRefreshDataModel(RefreshDataHelper.RefreshDataModel.IGNORE_PAGE_SIZE);
        } else {
            refreshDataHelper.setFirstPageNumber(0);
        }
    }

    private void getData(int pageNumber, int pageSize) {
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo == null) {
            return;
        }
        if (bindWalletInfo.getZoneType() == ZoneType.OVERSEAS) {
            getOverseasData(pageNumber, pageSize, bindWalletInfo);
        } else {
            getInternalData(pageNumber, pageSize, bindWalletInfo);
        }
    }

    private void getInternalData(int pageNumber, int pageSize, BindWalletInfoModel bindWalletInfo) {
        SudNFTGetCnNFTListParamModel model = new SudNFTGetCnNFTListParamModel();
        model.walletType = bindWalletInfo.walletType;
        model.walletToken = bindWalletInfo.getWalletToken();
        model.pageNumber = pageNumber;
        model.pageSize = pageSize;
        viewModel.getCNNftList(model, new NFTViewModel.GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    List<NftModel> datas;
                    if (model == null) {
                        datas = null;
                    } else {
                        datas = model.list;
                    }
                    setNftDetail(datas, bindWalletInfo.walletType, bindWalletInfo.getZoneType());
                    moveDressedInToFirst(datas);
                    refreshDataHelper.respDatasSuccess(new ListModel<>(pageNumber, pageSize, datas));
                    refreshView.getRecyclerView().post(() -> {
                        checkDressedIn();
                    });
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    refreshDataHelper.respDatasFailed(new ListModel<>(pageNumber, pageSize));
                });
            }
        });
    }

    private void getOverseasData(int pageNumber, int pageSize, BindWalletInfoModel bindWalletInfo) {
        if (pageNumber == refreshDataHelper.getFirstPageNumber()) {
            pageKeyMaps.clear();
        }
        String pageKey = getPageKey(pageNumber);
        if (pageNumber != refreshDataHelper.getFirstPageNumber() && TextUtils.isEmpty(pageKey)) {
            // 没有下一页了
            refreshDataHelper.respDatasSuccess(new ListModel<>(pageNumber, pageSize));
            return;
        }

        SudNFTGetNFTListParamModel model = new SudNFTGetNFTListParamModel();
        model.walletToken = bindWalletInfo.getWalletToken();
        model.chainType = bindWalletInfo.getChainType();
        model.walletAddress = bindWalletInfo.getWalletAddress();
        model.pageKey = pageKey;
        viewModel.getNftList(model, new NFTViewModel.GetNftListListener() {
            @Override
            public void onSuccess(NftListResultModel model) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    List<NftModel> datas;
                    if (model == null) {
                        datas = null;
                    } else {
                        datas = model.list;
                        pageKeyMaps.put(pageNumber + 1, model.pageKey);
                    }
                    setNftDetail(datas, bindWalletInfo.walletType, bindWalletInfo.getZoneType());
                    moveDressedInToFirst(datas);
                    refreshDataHelper.respDatasSuccess(new ListModel<>(pageNumber, pageSize, datas));
                    refreshView.getRecyclerView().post(() -> {
                        checkDressedIn();
                    });
                });
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                LifecycleUtils.safeLifecycle(context, () -> {
                    refreshDataHelper.respDatasFailed(new ListModel<>(pageNumber, pageSize));
                });
            }
        });
    }

    private void setNftDetail(List<NftModel> list, long walletType, int zoneType) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (NftModel nftModel : list) {
            nftModel.walletType = walletType;
            nftModel.zoneType = zoneType;
        }
    }

    private String getPageKey(int pageNumber) {
        return pageKeyMaps.get(pageNumber);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = refreshView.getRecyclerView();
        int paddingHorizontal = DensityUtils.dp2px(12);
        int paddingBottom = DensityUtils.dp2px(10);
        recyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, paddingBottom);
    }

    @Override
    protected void initData() {
        super.initData();
        viewModel.initData(this);
        BindWalletInfoModel bindWalletInfo = viewModel.getBindWalletInfo();
        if (bindWalletInfo != null && bindWalletInfo.getZoneType() == ZoneType.INTERNAL) {
            topBar.setTitle(getString(R.string.my_digital_collection));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDressedIn();
    }

    /**
     * 检查哪个是穿戴中的
     * 1，穿戴中的NFT移到第一位
     * 2，显示穿戴中
     */
    private void checkDressedIn() {
        NftModel wearNft = viewModel.getWearNft();
        int dressedInPosition = -1;
        // 给其赋值是否穿戴了
        for (int i = 0; i < adapter.getData().size(); i++) {
            NftModel item = adapter.getItem(i);
            if (Objects.equals(wearNft, item)) {
                // 已穿戴
                if (!item.isDressedIn) {
                    if (i != 0) {
                        dressedInPosition = i;
                    }
                    item.isDressedIn = true;
                    adapter.notifyItemChanged(i);
                }
            } else {
                // 未穿戴
                if (item.isDressedIn) {
                    item.isDressedIn = false;
                    adapter.notifyItemChanged(i);
                }
            }
        }
        // 穿戴的nft移到第一位
        if (dressedInPosition != -1) {
            NftModel item = adapter.getItem(dressedInPosition);
            adapter.removeAt(dressedInPosition);
            adapter.addData(0, item);
        }
    }

    /**
     * 检查哪个是穿戴中的
     * 1，穿戴中的NFT移到第一位
     * 2，显示穿戴中
     */
    private void moveDressedInToFirst(List<NftModel> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        NftModel wearNft = viewModel.getWearNft();
        int dressedInPosition = -1;
        // 给其赋值是否穿戴了
        for (int i = 0; i < datas.size(); i++) {
            NftModel item = datas.get(i);
            if (Objects.equals(wearNft, item)) {
                // 已穿戴
                if (!item.isDressedIn) {
                    if (i != 0) {
                        dressedInPosition = i;
                    }
                    item.isDressedIn = true;
                }
            } else {
                // 未穿戴
                if (item.isDressedIn) {
                    item.isDressedIn = false;
                }
            }
        }
        // 穿戴的nft移到第一位
        if (dressedInPosition != -1) {
            NftModel item = datas.get(dressedInPosition);
            datas.remove(dressedInPosition);
            datas.add(0, item);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                onNftItemClick(position);
            }
        });
        viewModel.bindWalletInfoMutableLiveData.observe(this, new Observer<BindWalletInfoModel>() {
            @Override
            public void onChanged(BindWalletInfoModel bindWalletInfoModel) {
                if (bindWalletInfoModel != null) {
                    refreshDataHelper.autoRefresh();
                }
            }
        });
    }

    private void onNftItemClick(int position) {
        NftModel item = adapter.getItem(position);
        NftDetailActivity.start(this, item);
    }

}
