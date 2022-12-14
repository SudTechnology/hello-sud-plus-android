package tech.sud.mgp.hello.ui.main.discover;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseFragment;
import tech.sud.mgp.hello.common.http.param.BaseResponse;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.utils.DensityUtils;
import tech.sud.mgp.hello.service.main.repository.HomeRepository;
import tech.sud.mgp.hello.service.main.resp.AuthMatchRoomResp;
import tech.sud.mgp.hello.service.main.resp.AuthRoomListResp;
import tech.sud.mgp.hello.service.main.resp.AuthRoomModel;
import tech.sud.mgp.hello.ui.common.utils.CompletedListener;
import tech.sud.mgp.hello.ui.common.utils.LifecycleUtils;
import tech.sud.mgp.hello.ui.common.widget.EmptyDataView;
import tech.sud.mgp.hello.ui.common.widget.refresh.ListModel;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshDataHelper;
import tech.sud.mgp.hello.ui.common.widget.refresh.RefreshView;
import tech.sud.mgp.hello.ui.scenes.base.utils.EnterRoomUtils;

/**
 * 跨域授权房间页面
 */
public class DiscoverFragment extends BaseFragment {

    private RefreshView refreshView;
    private RefreshDataHelper<AuthRoomModel> refreshDataHelper;
    private DiscoverRoomAdapter adapter;
    private boolean isInMatchRoom; // 是否正在匹配房间

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        refreshView = mRootView.findViewById(R.id.refresh_view);

        initRefreshDataHelper();
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = refreshView.getRecyclerView();
        int paddingHorizontal = DensityUtils.dp2px(12);
        recyclerView.setPadding(paddingHorizontal, 0, paddingHorizontal, DensityUtils.dp2px(20));
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
        HomeRepository.authMatchRoom(this, item.authSecret, item.roomId, item.mgId, new RxCallback<AuthMatchRoomResp>() {
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

    @Override
    public void onResume() {
        super.onResume();
        refreshDataHelper.initData();
    }

}