package tech.sud.mgp.hello.ui.main.discover;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.model.HSUserInfo;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.repository.UserInfoRepository;
import tech.sud.mgp.hello.service.main.resp.AuthMatchRoomResp;
import tech.sud.mgp.hello.service.main.resp.AuthRoomListResp;
import tech.sud.mgp.hello.service.main.resp.AuthRoomModel;
import tech.sud.mgp.hello.service.main.resp.UserInfoResp;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.common.widget.EmptyDataView;
import tech.sud.mgp.hello.ui.common.widget.refresh.ListModel;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.main.base.widget.MainUserInfoView;
import tech.sud.mgp.hello.ui.main.nft.model.BindWalletInfoModel;
import tech.sud.mgp.hello.ui.main.nft.model.NftModel;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.CancelWearNftListener;
import tech.sud.mgp.hello.ui.main.nft.viewmodel.NFTViewModel;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

public class DiscoverFragment extends BaseFragment {

    private EditText searchEt;
    private TextView goSearch;
    private RefreshView refreshView;
    private MainUserInfoView userInfoView;
    private RefreshDataHelper<AuthRoomModel> refreshDataHelper;
    private DiscoverRoomAdapter adapter;
    private boolean isInMatchRoom; // 是否正在匹配房间
    private final NFTViewModel nftViewModel = new NFTViewModel();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        searchEt = mRootView.findViewById(R.id.search_et);
        goSearch = mRootView.findViewById(R.id.go_search);
        refreshView = mRootView.findViewById(R.id.refresh_view);
        userInfoView = mRootView.findViewById(R.id.user_info_view);

        userInfoView.setNftMask(R.drawable.ic_nft_mask_white);
        userInfoView.setViewWalletAddressArrowVisible(false);
        View viewStatusBar = findViewById(R.id.view_statusbar);
        ViewUtils.setHeight(viewStatusBar, ImmersionBar.getStatusBarHeight(requireContext()));

        initRefreshDataHelper();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = refreshView.getRecyclerView();
        int paddingHorizontal = DensityUtils.dp2px(9);
        recyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
    }

    private void initRefreshDataHelper() {
        adapter = new DiscoverRoomAdapter();
        refreshDataHelper = new RefreshDataHelper<AuthRoomModel>() {
            @Override
            protected RefreshView getRefreshView() {
                return refreshView;
            }

            @Override
            protected RecyclerView.LayoutManager getLayoutManager() {
                return new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
            }

            @Override
            protected BaseQuickAdapter<AuthRoomModel, BaseViewHolder> getAdapter() {
                return adapter;
            }

            @Override
            protected GetDataListener getDataListener() {
                return new GetDataListener() {
                    @Override
                    public void onGetData(int pageNumber, int pageSize) {
                        getRoomList(pageNumber, pageSize);
                    }
                };
            }

            @Override
            protected View getEmptyView() {
                Context context = getContext();
                if (context == null) return null;
                EmptyDataView view = new EmptyDataView(context);
//                view.setText(getString(R.string.empty_room_match));
                return view;
            }
        };
    }

    /** 获取数据 */
    private void getRoomList(int pageNumber, int pageSize) {
        ListModel<AuthRoomModel> listModel = new ListModel<>();
        listModel.pageNumber = pageNumber;
        listModel.pageSize = pageSize;
        HomeRepository.authRoomList(this, pageNumber, pageSize, new RxCallback<AuthRoomListResp>() {
            @Override
            public void onNext(BaseResponse<AuthRoomListResp> t) {
                super.onNext(t);
                if (t.getRetCode() == RetCode.SUCCESS) {
                    if (t.getData() != null) {
                        listModel.datas = t.getData().roomInfos;
                    }
                    refreshDataHelper.respDatasSuccess(listModel);
                } else {
                    refreshDataHelper.respDatasFailed(listModel);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                refreshDataHelper.respDatasFailed(listModel);
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            String keyword = searchEt.getText().toString();
            if (keyword.length() > 0) {
                goSearch.setVisibility(View.VISIBLE);
            } else {
                goSearch.setVisibility(View.GONE);
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    goSearch.setVisibility(View.VISIBLE);
                } else {
                    goSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                enterRoom();
            }
            return false;
        });
        goSearch.setOnClickListener(v -> enterRoom());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                clickRoom(position);
            }
        });
    }

    private void clickRoom(int position) {
        if (isInMatchRoom) {
            return;
        }
        isInMatchRoom = true;
        AuthRoomModel item = adapter.getItem(position);
        HomeRepository.authMatchRoom(this, item.authSecret, item.roomId, new RxCallback<AuthMatchRoomResp>() {
            @Override
            public void onSuccess(AuthMatchRoomResp resp) {
                super.onSuccess(resp);
                matchRoomOnSuccess(resp);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                isInMatchRoom = false;
            }
        });
    }

    private void matchRoomOnSuccess(AuthMatchRoomResp resp) {
        if (resp == null) {
            return;
        }
        LifecycleUtils.safeLifecycle(this, new CompletedListener() {
            @Override
            public void onCompleted() {
                EnterRoomUtils.enterRoom(requireContext(), resp.localRoomId);
            }
        });
    }

    private void enterRoom() {
        try {
            String roomIdString = searchEt.getText().toString().trim();
            if (!TextUtils.isEmpty(roomIdString)) {
                long roomId = Long.parseLong(roomIdString);
                EnterRoomUtils.enterRoom(requireContext(), roomId);
            }
            KeyboardUtils.hideSoftInput(searchEt);
            searchEt.setText("");
            searchEt.clearFocus();
        } catch (Exception e) {
            ToastUtils.showShort(getString(R.string.search_room_error));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataHelper.initData();
        userInfoView.updateUserInfo();
        updateNftHeader();
    }

    /** 更新nft头像 */
    private void updateNftHeader() {
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(HSUserInfo.userId);
        UserInfoRepository.getUserInfoList(this, userIdList, new UserInfoRepository.UserInfoResultListener() {
            @Override
            public void userInfoList(List<UserInfoResp> userInfos) {
                BindWalletInfoModel bindWalletInfoModel = nftViewModel.getBindWalletInfoByCache();
                if (userInfos == null || userInfos.size() == 0 || bindWalletInfoModel == null) {
                    return;
                }
                NftModel wearNft = bindWalletInfoModel.getWearNft();
                if (wearNft == null) {
                    return;
                }
                UserInfoResp userInfoResp = userInfos.get(0);
                if (userInfoResp.headerType != 1 || !Objects.equals(userInfoResp.headerNftToken, wearNft.detailsToken)) {
                    nftViewModel.cancelWearNft(new CancelWearNftListener() {
                        @Override
                        public void onSuccess() {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                userInfoView.updateUserInfo();
                            });
                        }

                        @Override
                        public void onFailure(int retCode, String retMsg) {
                            LifecycleUtils.safeLifecycle(mFragment, () -> {
                                nftViewModel.clearWearNft();
                                userInfoView.updateUserInfo();
                            });
                        }
                    });
                }
            }
        });
    }

}